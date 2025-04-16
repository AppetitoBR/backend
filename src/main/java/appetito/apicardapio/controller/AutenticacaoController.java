package appetito.apicardapio.controller;

import appetito.apicardapio.entity.UsuarioDashboard;
import appetito.apicardapio.entity.Cliente;
import appetito.apicardapio.dto.DadosTokenJWT;
import appetito.apicardapio.security.DiscordAlert;
import appetito.apicardapio.security.TokenService;
import appetito.apicardapio.dto.DadosAutenticacao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@RestController
@RequestMapping("/login")
public class AutenticacaoController {

    private final DaoAuthenticationProvider dashboardAuthProvider;
    private final DaoAuthenticationProvider appAuthProvider;
    private final TokenService tokenService;

    public AutenticacaoController(
            @Qualifier("dashboardAuthenticationProvider") DaoAuthenticationProvider dashboardAuthProvider,
            @Qualifier("appAuthenticationProvider") DaoAuthenticationProvider appAuthProvider,
            TokenService tokenService) {

        this.dashboardAuthProvider = dashboardAuthProvider;
        this.appAuthProvider = appAuthProvider;
        this.tokenService = tokenService;
    }

    @PostMapping("/dashboard")
    public ResponseEntity<?> loginDashboard(@RequestBody @Valid DadosAutenticacao dados) {
        try {
            var token = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());
            var auth = dashboardAuthProvider.authenticate(token);
            var usuario = (UsuarioDashboard) auth.getPrincipal();
            var tokenJWT = tokenService.generateToken(usuario);

            var emailDoUsuario = usuario.getEmail();
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            var ip = request.getRemoteAddr();
            new DiscordAlert().AlertDiscord("✅ Login em Dashboard realizado com sucesso por: " + emailDoUsuario + " (IP: " + ip + ")");
            Logger log = LoggerFactory.getLogger(getClass());
            log.warn(usuario.getAuthorities().toString());
            return ResponseEntity.ok(new DadosTokenJWT(tokenJWT));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas");
        }
    }
// mudar o que o professor pediu -- questao de autenticacao com os modulos
    @PostMapping("/app")
    public ResponseEntity<?> loginApp(@RequestBody @Valid DadosAutenticacao dados) {
        try {
            var token = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());
            var auth = appAuthProvider.authenticate(token);
            var cliente = (Cliente) auth.getPrincipal();

            var emailDoCliente = cliente.getEmail();
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            var ip = request.getRemoteAddr();
            new DiscordAlert().AlertDiscord("✅ Login em Cliente realizado com sucesso por: " + emailDoCliente + " (IP: " + ip + ")");
            Logger log = LoggerFactory.getLogger(getClass());
            log.warn(cliente.getAuthorities().toString());
            var tokenJWT = tokenService.generateToken(cliente);
            return ResponseEntity.ok(new DadosTokenJWT(tokenJWT));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas");
        }
    }
}


