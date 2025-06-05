package appetito.apicardapio.controller;

import appetito.apicardapio.dto.cadastro.ClienteCadastro;
import appetito.apicardapio.dto.detalhamento.ClienteDetalhamento;
import appetito.apicardapio.entity.Cliente;
import appetito.apicardapio.repository.ClienteRepository;
import appetito.apicardapio.security.DiscordAlert;
import appetito.apicardapio.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Objects;
/**
 * Controlador responsável por gerenciar as operações relacionadas aos clientes,
 * como cadastro, upload e visualização de imagem de perfil, obtenção de informações
 * do perfil e exclusão de conta.
 */
@RestController
@RequestMapping("/cliente")
public class ClienteController {

    private final DiscordAlert discordAlert;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final ClienteRepository clienteRepository;
    private final ClienteService clienteService;

    /**
     * Construtor da classe ClienteController.
     *
     * @param discordAlert        Serviço de alerta via Discord.
     * @param clienteRepository   Repositório de dados do cliente.
     * @param clienteService      Serviço com regras de negócio relacionadas ao cliente.
     */
    public ClienteController(DiscordAlert discordAlert, ClienteRepository clienteRepository, ClienteService clienteService) {
        this.discordAlert = discordAlert;
        this.clienteRepository = clienteRepository;
        this.clienteService = clienteService;
    }

    /**
     * Cadastra um novo cliente na aplicação.
     *
     * @param dadosCliente Dados fornecidos para cadastro do cliente.
     * @param uriBuilder   Builder para construção de URI do novo recurso.
     * @return ResponseEntity com status de criação ou erro.
     */
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

    /**
     * Realiza o upload da imagem de perfil do cliente autenticado.
     *
     * @param id                 ID do cliente.
     * @param file               Arquivo de imagem enviado.
     * @param clienteAutenticado Cliente autenticado no contexto da requisição.
     * @return ResponseEntity com status da operação.
     */
    @PostMapping("/{id}/upload-imagem")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> uploadImagemPerfil(@PathVariable Long id, @RequestPart("file") MultipartFile file, @AuthenticationPrincipal Cliente clienteAutenticado) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Arquivo de imagem não pode estar vazio!");
        }
        if (!clienteAutenticado.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Você não tem permissão para alterar a imagem de outro usuário.");
        }

        if (!Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
            return ResponseEntity.badRequest().body("Arquivo deve ser uma imagem válida.");
        }

        try {
            Cliente cliente = clienteService.salvarImagemPerfil(id, file);
            return cliente != null
                    ? ResponseEntity.ok("Imagem de perfil salva com sucesso!")
                    : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente não encontrado.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao salvar imagem: " + e.getMessage());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    /**
     * Recupera a imagem de perfil de um cliente com base no ID.
     *
     * @param id      ID do cliente.
     * @return ResponseEntity contendo a imagem ou status de erro.
     */
    @GetMapping("/{id}/imagem-perfil")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> buscarImagemPerfil(@PathVariable Long id) {
        try {
            byte[] imagem = clienteService.obterImagemPerfil(id);
            if (imagem == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(imagem);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Recupera a imagem de perfil do cliente autenticado.
     *
     * @param cliente Cliente autenticado.
     * @return ResponseEntity contendo a imagem ou erro de autenticação.
     */
    @GetMapping("/me/imagem")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> minhaImagemPerfil(@AuthenticationPrincipal Cliente cliente) {
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        byte[] imagem = clienteService.obterImagemPerfil(cliente.getId());
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(imagem);
    }

    /**
     * Obtém os dados do cliente autenticado.
     *
     * @param cliente Cliente autenticado.
     * @return ResponseEntity com os dados detalhados do cliente ou erro de autenticação.
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> meuPerfil(@AuthenticationPrincipal Cliente cliente) {
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autenticado.");
        }
        return ResponseEntity.ok(new ClienteDetalhamento(cliente));
    }

    /**
     * Exclui a conta do cliente autenticado.
     *
     * @param cliente Cliente autenticado.
     * @return ResponseEntity com status da operação.
     */
    @DeleteMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deletarCliente(@AuthenticationPrincipal Cliente cliente) {
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Cliente não autenticado.");
        }

        return clienteService.deletarCliente(cliente);
    }
}
