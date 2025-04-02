package appetito.apicardapio.controller;

import appetito.apicardapio.entity.UsuarioDashboard;
import appetito.apicardapio.entity.Cliente;
import appetito.apicardapio.dto.DadosTokenJWT;
import appetito.apicardapio.security.TokenService;
import appetito.apicardapio.dto.DadosAutenticacao;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
public class AutenticacaoController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AutenticacaoController(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @PostMapping("/dashboard")
    public ResponseEntity<?> loginDashboard(@RequestBody @Valid DadosAutenticacao dados) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());
        var authentication = authenticationManager.authenticate(authenticationToken);

        var usuario = (UsuarioDashboard) authentication.getPrincipal();
        var tokenJWT = tokenService.generateToken(usuario);

        return ResponseEntity.ok(new DadosTokenJWT(tokenJWT));
    }

    @PostMapping("/app")
    public ResponseEntity<?> loginApp(@RequestBody @Valid DadosAutenticacao dados) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());
        var authentication = authenticationManager.authenticate(authenticationToken);

        var cliente = (Cliente) authentication.getPrincipal();
        var tokenJWT = tokenService.generateToken(cliente);

        return ResponseEntity.ok(new DadosTokenJWT(tokenJWT));
    }
}