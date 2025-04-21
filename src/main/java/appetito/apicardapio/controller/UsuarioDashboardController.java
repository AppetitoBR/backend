package appetito.apicardapio.controller;

import appetito.apicardapio.dto.cadastro.UsuarioDashboardCadastro;
import appetito.apicardapio.dto.detalhamento.UsuarioDashboardDetalhamento;
import appetito.apicardapio.dto.GetAll.UsuarioDados;
import appetito.apicardapio.entity.Cliente;
import appetito.apicardapio.entity.Estabelecimento;
import appetito.apicardapio.entity.UsuarioDashboard;
import appetito.apicardapio.entity.UsuarioEstabelecimento;
import appetito.apicardapio.enums.PapelUsuario;
import appetito.apicardapio.repository.EstabelecimentoRepository;
import appetito.apicardapio.repository.UsuarioDashboardRepository;
import appetito.apicardapio.repository.UsuarioEstabelecimentoRepository;
import appetito.apicardapio.security.DiscordAlert;
import appetito.apicardapio.service.EmailService;
import appetito.apicardapio.service.UsuarioDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
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
    private final UsuarioEstabelecimentoRepository usuarioEstabelecimentoRepository;
    private final EstabelecimentoRepository estabelecimentoRepository;
    private final EmailService emailService;
    public UsuarioDashboardController(UsuarioDashboardRepository usuarioRepository, UsuarioDashboardService usuarioService, EstabelecimentoRepository estabelecimentoRepository, UsuarioEstabelecimentoRepository usuarioEstabelecimentoRepository, EstabelecimentoRepository estabelecimentoRepository1, RestTemplateAutoConfiguration restTemplateAutoConfiguration, EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
        this.usuarioEstabelecimentoRepository = usuarioEstabelecimentoRepository;
        this.estabelecimentoRepository = estabelecimentoRepository1;
        this.emailService = emailService;
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
        emailService.enviarEmailTexto(usuario.getEmail(), "Novo usuario cadastrado", "Voce esta recebendo um email de cadastro com o nome: " + usuario.getNome_completo()); // em teste

        var email = dadosUsuario.email();
        new DiscordAlert().AlertDiscord("Novo Usuario Dashboard cadastrado: " + email);

        var uri = uriBuilder.path("/usuarios/{id}")
                .buildAndExpand(usuario.getUsuario_dashboard_id())
                .toUri();

        return ResponseEntity.created(uri).body(new UsuarioDashboardDetalhamento(usuario));
    }

    @PostMapping(value = "/{id}/upload-imagem", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadImagemPerfil(@PathVariable Long id, @RequestPart("file") MultipartFile file, HttpServletRequest request) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof UsuarioDashboard usuario)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autenticado.");
        }

        if (!usuario.getUsuario_dashboard_id().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Você não tem permissão para atualizar a imagem de outro usuário.");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || !(filename.endsWith(".jpg") || filename.endsWith(".jpeg") || filename.endsWith(".png"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Arquivo de imagem inválido. Apenas .jpg, .jpeg e .png são permitidos.");
        }

        if (file.getSize() > 2 * 1024 * 1024) { // 2MB = 2 * 1024 * 1024 bytes
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("O arquivo é muito grande. O tamanho máximo permitido é 2MB.");
        }

        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Arquivo de imagem não pode estar vazio!");
        }

        try {
            UsuarioDashboard usuarioAtualizado = usuarioService.salvarImagemPerfil(id, file, request);
            return usuarioAtualizado != null
                    ? ResponseEntity.ok("Imagem de perfil salva com sucesso!")
                    : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao salvar imagem: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/imagem-perfil")
    public ResponseEntity<byte[]> buscarImagemPerfil(@PathVariable Long id, HttpServletRequest request) {
        byte[] imagem = usuarioService.obterImagemPerfil(id, request);
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

        if (!(authentication.getPrincipal() instanceof UsuarioDashboard usuario)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autenticado.");
        }
        return ResponseEntity.ok(new UsuarioDashboardDetalhamento(usuario));
    }

    @GetMapping("/me/imagem")
    public ResponseEntity<byte[]> minhaImagem(HttpServletRequest request) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof UsuarioDashboard usuario)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        byte[] imagem = usuarioService.obterImagemPerfil(usuario.getUsuario_dashboard_id(), request);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(imagem);
    }

    @DeleteMapping("/me")
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
    public ResponseEntity<?> atualizarUsuario(@RequestBody @Valid UsuarioDashboardCadastro dadosAtualizados) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication.getPrincipal() instanceof UsuarioDashboard usuario)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autenticado.");
        }

        if (!usuario.getUsuario_dashboard_id().equals(authentication.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Você não tem permissão para atualizar o perfil de outro usuário.");
        }

        usuario.setNome_completo(dadosAtualizados.nome_completo());
        usuario.setEmail(dadosAtualizados.email());
        // usuario.setIdioma_padrao(dadosAtualizados.idioma_padrao()); // vou arrumar isso aqui depois

        if (dadosAtualizados.senha() != null && !dadosAtualizados.senha().isEmpty()) {
            usuario.setSenha(passwordEncoder.encode(dadosAtualizados.senha()));
        }
        usuarioRepository.save(usuario);
        return ResponseEntity.ok(new UsuarioDashboardDetalhamento(usuario));
    }

}