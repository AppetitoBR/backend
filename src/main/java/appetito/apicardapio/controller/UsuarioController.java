package appetito.apicardapio.controller;

import appetito.apicardapio.dto.cadastro.UsuarioCadastro;
import appetito.apicardapio.dto.detalhamento.UsuarioDetalhamento;
import appetito.apicardapio.dto.GetAll.UsuarioDados;
import appetito.apicardapio.entity.Estabelecimento;
import appetito.apicardapio.entity.Usuario;
import appetito.apicardapio.entity.UsuarioEstabelecimento;
import appetito.apicardapio.enums.PapelUsuario;
import appetito.apicardapio.repository.EstabelecimentoRepository;
import appetito.apicardapio.repository.UsuarioRepository;
import appetito.apicardapio.service.UsuarioService;
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
public class UsuarioController {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final EstabelecimentoRepository estabelecimentoRepository;
    public UsuarioController(UsuarioRepository usuarioRepository, UsuarioService usuarioService, EstabelecimentoRepository estabelecimentoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
        this.estabelecimentoRepository = estabelecimentoRepository;
    }


    @GetMapping
    @Transactional
    public ResponseEntity<List<UsuarioDados>> listarUsuarios() {
        var lista =  usuarioRepository.findAll().stream().map(UsuarioDados::new).toList();
        return ResponseEntity.ok(lista);
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<UsuarioDetalhamento> cadastrarUsuario(@RequestBody @Valid UsuarioCadastro dadosUsuario, UriComponentsBuilder uriU){
        var usuario = new Usuario(dadosUsuario);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));


        List<Estabelecimento> estabelecimentos = estabelecimentoRepository.findAll();

        List<UsuarioEstabelecimento> associacoes = estabelecimentos.stream()
                .map(estabelecimento -> new UsuarioEstabelecimento(usuario, estabelecimento, PapelUsuario.CLIENTE))
                .toList();

        usuario.setEstabelecimentos(associacoes);

        usuarioRepository.save(usuario);

        var uri = uriU.path("/usuarios/{id}").buildAndExpand(usuario.getUsuario_id()).toUri();
        return ResponseEntity.created(uri).body(new UsuarioDetalhamento(usuario));
    }

    @Operation(summary = "Upload da imagem de perfil do usuário")
    @PostMapping(value = "/{id}/upload-imagem", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadImagemPerfil(@PathVariable Long id, @RequestPart("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Arquivo de imagem não pode estar vazio!");
        }

        try {
            Usuario usuario = usuarioService.salvarImagemPerfil(id, file);
            return usuario != null
                    ? ResponseEntity.ok("Imagem de perfil salva com sucesso!")
                    : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao salvar imagem: " + e.getMessage());
        }
    }
    @GetMapping("/{id}/imagem-perfil")
    public ResponseEntity<byte[]> buscarImagemPerfil(@PathVariable Long id) {
        byte[] imagem = usuarioService.obterImagemPerfil(id);
        if (imagem == null) {
            return ResponseEntity.notFound().build();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "image/png");
        return new ResponseEntity<>(imagem, headers, HttpStatus.OK);
    }
    @GetMapping("/me")
    public ResponseEntity<?> meuPerfil() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication.getPrincipal() instanceof Usuario usuario)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autenticado.");
        }
        return ResponseEntity.ok(new UsuarioDetalhamento(usuario));
    }
    @GetMapping("/me/imagem")
    public ResponseEntity<byte[]> minhaImagem() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof Usuario usuario)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        byte[] imagem = usuarioService.obterImagemPerfil(usuario.getUsuario_id());

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(imagem);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<UsuarioDetalhamento> deletarUsuario(@PathVariable Long id) {
        var usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        usuarioRepository.delete(usuario);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/me")
    public ResponseEntity<?> atualizarUsuario(@RequestBody @Valid UsuarioCadastro dadosAtualizados) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication.getPrincipal() instanceof Usuario usuario)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autenticado.");
        }

        usuario.setNome_completo(dadosAtualizados.nome_completo());
        usuario.setEmail(dadosAtualizados.email());
        usuario.setIdioma_padrao(dadosAtualizados.idioma_padrao());

        if (dadosAtualizados.senha() != null && !dadosAtualizados.senha().isEmpty()) {
            usuario.setSenha(passwordEncoder.encode(dadosAtualizados.senha()));
        }

        usuarioRepository.save(usuario);

        return ResponseEntity.ok(new UsuarioDetalhamento(usuario));
    }
}
