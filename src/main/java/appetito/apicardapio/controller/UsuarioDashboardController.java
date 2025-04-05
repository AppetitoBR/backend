package appetito.apicardapio.controller;

import appetito.apicardapio.dto.cadastro.UsuarioDashboardCadastro;
import appetito.apicardapio.dto.detalhamento.UsuarioDashboardDetalhamento;
import appetito.apicardapio.dto.GetAll.UsuarioDados;
import appetito.apicardapio.entity.UsuarioDashboard;
import appetito.apicardapio.repository.EstabelecimentoRepository;
import appetito.apicardapio.repository.UsuarioDashboardRepository;
import appetito.apicardapio.security.DiscordAlert;
import appetito.apicardapio.service.UsuarioDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuário", description = "Gerenciamento de usuários e imagens de perfil")
public class UsuarioDashboardController {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final UsuarioDashboardRepository usuarioRepository;
    private final UsuarioDashboardService usuarioService;

    public UsuarioDashboardController(UsuarioDashboardRepository usuarioRepository, UsuarioDashboardService usuarioService, EstabelecimentoRepository estabelecimentoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    @Transactional
    public ResponseEntity<List<UsuarioDados>> listarUsuarios() {
        var lista = usuarioRepository.findAll().stream().map(UsuarioDados::new).toList();
        return ResponseEntity.ok(lista);
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrarUsuarioDashboard(
            @RequestBody @Valid UsuarioDashboardCadastro dadosUsuario,
            UriComponentsBuilder uriBuilder) {

        if (usuarioRepository.existsByEmail(dadosUsuario.email())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("E-mail já cadastrado!");
        }

        var usuario = new UsuarioDashboard(dadosUsuario);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuarioRepository.save(usuario);
        var email = dadosUsuario.email();
        new DiscordAlert().AlertDiscord("Novo Usuario Dashboard cadastrado: " + email);

        var uri = uriBuilder.path("/usuarios/{id}")
                .buildAndExpand(usuario.getUsuario_dashboard_id())
                .toUri();

        return ResponseEntity.created(uri).body(new UsuarioDashboardDetalhamento(usuario));
    }

  //  @Operation(summary = "Upload da imagem de perfil do usuário")
    //@PostMapping(value = "/{id}/upload-imagem", consumes = "multipart/form-data")
  //  public ResponseEntity<String> uploadImagemPerfil(@PathVariable Long id, @RequestPart("file") MultipartFile file) {
  //      if (file.isEmpty()) {
   //         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Arquivo de imagem não pode estar vazio!");
   //     }
//
     //   try {
  //          UsuarioDashboard usuario = usuarioService.salvarImagemPerfil(id, file);
    //        return usuario != null
    //                ? ResponseEntity.ok("Imagem de perfil salva com sucesso!")
      //              : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado.");
      //  } catch (IOException e) {
     //       return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao salvar imagem: " + e.getMessage());
    //   }
  //  }

   // @GetMapping("/{id}/imagem-perfil")
   // public ResponseEntity<byte[]> buscarImagemPerfil(@PathVariable Long id) {
   //     byte[] imagem = usuarioService.obterImagemPerfil(id);
    //    if (imagem == null) {
    //        return ResponseEntity.notFound().build();
   //     }
    //    HttpHeaders headers = new HttpHeaders();
    //    headers.add("Content-Type", "image/png");
     //   return new ResponseEntity<>(imagem, headers, HttpStatus.OK);
  //  }

    @GetMapping("/me")
    public ResponseEntity<?> meuPerfil() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication.getPrincipal() instanceof UsuarioDashboard usuario)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autenticado.");
        }
        return ResponseEntity.ok(new UsuarioDashboardDetalhamento(usuario));
    }

   // @GetMapping("/me/imagem")
   // public ResponseEntity<byte[]> minhaImagem() {
    //    var authentication = SecurityContextHolder.getContext().getAuthentication();
    //    if (!(authentication.getPrincipal() instanceof UsuarioDashboard usuario)) {
   //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    //    }
//
   //     byte[] imagem = usuarioService.obterImagemPerfil(usuario.getUsuario_dashboard_id());
//
   //     return ResponseEntity.ok()
    //            .contentType(MediaType.IMAGE_PNG)
  ///              .body(imagem);
   // }

    @DeleteMapping("/{id}")
    public ResponseEntity<UsuarioDashboardDetalhamento> deletarUsuario(@PathVariable Long id) {
        var usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        usuarioRepository.delete(usuario);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/me")
    public ResponseEntity<?> atualizarUsuario(@RequestBody @Valid UsuarioDashboardCadastro dadosAtualizados) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication.getPrincipal() instanceof UsuarioDashboard usuario)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autenticado.");
        }

        usuario.setNome_completo(dadosAtualizados.nome_completo());
        usuario.setEmail(dadosAtualizados.email());
      //  usuario.setIdioma_padrao(dadosAtualizados.idioma_padrao());

        if (dadosAtualizados.senha() != null && !dadosAtualizados.senha().isEmpty()) {
            usuario.setSenha(passwordEncoder.encode(dadosAtualizados.senha()));
        }

        usuarioRepository.save(usuario);

        return ResponseEntity.ok(new UsuarioDashboardDetalhamento(usuario));
    }
}