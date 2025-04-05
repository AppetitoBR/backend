package appetito.apicardapio.controller;

import appetito.apicardapio.dto.cadastro.ClienteCadastro;
import appetito.apicardapio.dto.cadastro.UsuarioDashboardCadastro;
import appetito.apicardapio.dto.detalhamento.ClienteDetalhamento;
import appetito.apicardapio.dto.detalhamento.UsuarioDashboardDetalhamento;
import appetito.apicardapio.entity.Cliente;
import appetito.apicardapio.entity.UsuarioDashboard;
import appetito.apicardapio.repository.ClienteRepository;
import appetito.apicardapio.security.DiscordAlert;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/cliente")
public class ClienteController {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final ClienteRepository clienteRepository;

    public ClienteController(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
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
        var email = cliente.getEmail();
        new DiscordAlert().AlertDiscord("Novo Cliente cadastrado: " + email);
        var uri = uriBuilder.path("/clientes/{id}")
                .buildAndExpand(cliente.getId())
                .toUri();

        return ResponseEntity.created(uri).body(new ClienteDetalhamento(cliente));
    }



}
