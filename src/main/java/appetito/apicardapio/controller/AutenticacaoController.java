package appetito.apicardapio.controller;

import appetito.apicardapio.entity.Usuario;
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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
public class AutenticacaoController {
    @Autowired
    private AuthenticationManager manager;
    @Autowired
    private TokenService tokenService;
    @PostMapping
    public ResponseEntity<?> autenticar(@RequestBody @Valid DadosAutenticacao dados) {
        try {
            var token = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());
            var autenticacao = manager.authenticate(token);
            var usuario = (Usuario) autenticacao.getPrincipal();
            var tokenJWT = tokenService.generateToken(usuario);
            return ResponseEntity.ok(new DadosTokenJWT(tokenJWT));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro durante a autenticação: " + e.getMessage());
        }
    }
}