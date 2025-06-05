package appetito.apicardapio.controller;

import appetito.apicardapio.dto.cadastro.UsuarioDashboardCadastro;
import appetito.apicardapio.dto.detalhamento.UsuarioDashboardDetalhamento;
import appetito.apicardapio.entity.Estabelecimento;
import appetito.apicardapio.entity.UsuarioDashboard;
import appetito.apicardapio.entity.UsuarioEstabelecimento;
import appetito.apicardapio.enums.PapelUsuario;
import appetito.apicardapio.repository.EstabelecimentoRepository;
import appetito.apicardapio.repository.UsuarioDashboardRepository;
import appetito.apicardapio.repository.UsuarioEstabelecimentoRepository;
import appetito.apicardapio.security.DiscordAlert;
import appetito.apicardapio.service.UsuarioDashboardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

public class UsuarioDashboardController {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final UsuarioDashboardRepository usuarioRepository;
    private final UsuarioDashboardService usuarioService;
    private final UsuarioEstabelecimentoRepository usuarioEstabelecimentoRepository;
    private final EstabelecimentoRepository estabelecimentoRepository;
    public UsuarioDashboardController(UsuarioDashboardRepository usuarioRepository, UsuarioDashboardService usuarioService, UsuarioEstabelecimentoRepository usuarioEstabelecimentoRepository, EstabelecimentoRepository estabelecimentoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
        this.usuarioEstabelecimentoRepository = usuarioEstabelecimentoRepository;
        this.estabelecimentoRepository = estabelecimentoRepository;
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrarUsuarioDashboard(
            @RequestBody @Valid UsuarioDashboardCadastro dadosUsuario,
            UriComponentsBuilder uriBuilder) {

        try {
            UsuarioDashboard usuario = usuarioService.cadastrarUsuarioDashboard(dadosUsuario);

            var uri = uriBuilder.path("/usuarios/{id}")
                    .buildAndExpand(usuario.getUsuario_dashboard_id())
                    .toUri();

            return ResponseEntity.created(uri).body(new UsuarioDashboardDetalhamento(usuario));

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/{id}/upload-imagem", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImagemPerfil(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal UsuarioDashboard usuarioAutenticado) {

        if (!usuarioAutenticado.getUsuario_dashboard_id().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Você não tem permissão para atualizar a imagem de outro usuário.");
        }

        try {
            usuarioService.uploadImagemPerfil(id, file);
            return ResponseEntity.ok("Imagem de perfil salva com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Erro ao salvar imagem: " + e.getMessage());
        }
    }


    @GetMapping("/{id}/imagem-perfil")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> buscarImagemPerfil(@PathVariable Long id, HttpServletRequest request) {
        byte[] imagem = usuarioService.obterImagemPerfil(id);
        if (imagem == null) {
               return ResponseEntity.notFound().build();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "image/png");
        return new ResponseEntity<>(imagem, headers, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<?> meuPerfil() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication.getPrincipal() instanceof UsuarioDashboard usuario)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autenticado.");
        }

        return ResponseEntity.ok(new UsuarioDashboardDetalhamento(usuario));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me/imagem")
    public ResponseEntity<byte[]> minhaImagem(HttpServletRequest request) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof UsuarioDashboard usuario)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        byte[] imagem = usuarioService.obterImagemPerfil(usuario.getUsuario_dashboard_id());

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(imagem);
    }

    @DeleteMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deletarUsuarioDashboard() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication.getPrincipal() instanceof UsuarioDashboard usuario)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autenticado.");
        }

        List<UsuarioEstabelecimento> vinculos = usuarioEstabelecimentoRepository.findByUsuario(usuario);
        for (UsuarioEstabelecimento vinculo : vinculos) {
            if (vinculo.getPapel() == PapelUsuario.ADMINISTRADOR) {
                Estabelecimento est = vinculo.getEstabelecimento();
                usuarioEstabelecimentoRepository.deleteAllByEstabelecimento(est);
                estabelecimentoRepository.delete(est);
            } else {
                usuarioEstabelecimentoRepository.delete(vinculo);
            }
        }

        usuarioRepository.delete(usuario);

        return ResponseEntity.ok("Conta e vínculos removidos com sucesso.");
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> atualizarUsuario(@RequestBody @Valid UsuarioDashboardCadastro dadosAtualizados) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication.getPrincipal() instanceof UsuarioDashboard usuario)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autenticado.");
        }

        UsuarioDashboardDetalhamento usuarioAtualizado = usuarioService.atualizarUsuario(usuario, dadosAtualizados);
        return ResponseEntity.ok(usuarioAtualizado);
    }


}