package appetito.apicardapio.controller;

import appetito.apicardapio.dto.cadastro.ClienteCadastro;
import appetito.apicardapio.dto.cadastro.UsuarioDashboardCadastro;
import appetito.apicardapio.dto.detalhamento.ClienteDetalhamento;
import appetito.apicardapio.dto.detalhamento.UsuarioDashboardDetalhamento;
import appetito.apicardapio.entity.Cliente;
import appetito.apicardapio.entity.UsuarioDashboard;
import appetito.apicardapio.repository.ClienteRepository;
import appetito.apicardapio.security.DiscordAlert;
import appetito.apicardapio.service.ClienteService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/cliente")
public class ClienteController {
    private final DiscordAlert discordAlert;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final ClienteRepository clienteRepository;
    private final ClienteService clienteService;

    public ClienteController(DiscordAlert discordAlert, ClienteRepository clienteRepository, ClienteService clienteService) {
        this.discordAlert = discordAlert;
        this.clienteRepository = clienteRepository;
        this.clienteService = clienteService;
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrarCliente(
            @RequestBody @Valid ClienteCadastro dadosCliente,
            UriComponentsBuilder uriBuilder) {

        if (clienteRepository.existsByEmail(dadosCliente.email())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("E-mail já cadastrado!");
        }

        var cliente = new Cliente(dadosCliente);
        cliente.setSenha(passwordEncoder.encode(cliente.getSenha()));
        clienteRepository.save(cliente);

        discordAlert.AlertDiscord("Novo Cliente cadastrado: " + cliente.getEmail());

        var uri = uriBuilder.path("/clientes/{id}")
                .buildAndExpand(cliente.getId())
                .toUri();

        return ResponseEntity.created(uri).body(new ClienteDetalhamento(cliente));
    }

    @PostMapping("/{id}/upload-imagem")
    public ResponseEntity<String> uploadImagemPerfil(@PathVariable Long id, @RequestPart("file") MultipartFile file, HttpServletRequest request) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Arquivo de imagem não pode estar vazio!");
        }

        try {
            Cliente cliente = clienteService.salvarImagemPerfil(id, file, request);
            return cliente != null
                    ? ResponseEntity.ok("Imagem de perfil salva com sucesso!")
                    : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente não encontrado.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao salvar imagem: " + e.getMessage());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @GetMapping("/{id}/imagem-perfil")
    public ResponseEntity<byte[]> buscarImagemPerfil(@PathVariable Long id, HttpServletRequest request) {
        try {
            byte[] imagem = clienteService.obterImagemPerfil(id, request);
            if (imagem == null) {
                return ResponseEntity.notFound().build();
            }
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "image/png");
            return new ResponseEntity<>(imagem, headers, HttpStatus.OK);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @GetMapping("/me/imagem")
    public ResponseEntity<byte[]> minhaImagemPerfil(HttpServletRequest request) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof Cliente cliente)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        byte[] imagem = clienteService.obterImagemPerfil(cliente.getId(), request);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(imagem);
    }
    @GetMapping("/me")
    public ResponseEntity<?> meuPerfil() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof Cliente cliente)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autenticado.");
        }
        return ResponseEntity.ok(new ClienteDetalhamento(cliente));
    }

    @DeleteMapping("/me")
    public ResponseEntity<?> deletarCliente() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication.getPrincipal() instanceof Cliente cliente)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Cliente não autenticado.");
        }

        var clienteExistente = clienteRepository.findById(cliente.getId()).orElse(null);
        if (clienteExistente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente não encontrado.");
        }

        clienteRepository.delete(clienteExistente);
        return ResponseEntity.ok("Conta do cliente excluída com sucesso.");
    }

}
