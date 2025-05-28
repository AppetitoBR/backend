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

/**
 * Controlador responsável por lidar com autenticação de usuários para dois módulos distintos:
 * Dashboard (usuários internos) e App (clientes).
 * <p>
 * Possui dois endpoints:
 * - /login/dashboard → login de usuários do sistema interno
 * - /login/app       → login de clientes do cardápio
 * <p>
 * Utiliza dois `DaoAuthenticationProvider` distintos para lidar com os contextos de autenticação separadamente.
 */
@RestController
@RequestMapping("/login")
public class AutenticacaoController {

    private final DaoAuthenticationProvider dashboardAuthProvider;
    private final DaoAuthenticationProvider appAuthProvider;
    private final TokenService tokenService;
    private final DiscordAlert discordAlert;

    /**
     * Construtor com injeção de dependência dos providers e do serviço de token JWT.
     *
     * @param dashboardAuthProvider Provider para autenticação de usuários do Dashboard.
     * @param appAuthProvider       Provider para autenticação de clientes do App.
     * @param tokenService          Serviço responsável pela geração de tokens JWT.
     */
    public AutenticacaoController(
            @Qualifier("dashboardAuthenticationProvider") DaoAuthenticationProvider dashboardAuthProvider,
            @Qualifier("appAuthenticationProvider") DaoAuthenticationProvider appAuthProvider,
            TokenService tokenService, DiscordAlert discordAlert) {

        this.dashboardAuthProvider = dashboardAuthProvider;
        this.appAuthProvider = appAuthProvider;
        this.tokenService = tokenService;
        this.discordAlert = discordAlert;
    }

    /**
     * Endpoint de login para usuários do Dashboard.
     *
     * @param dados Objeto contendo o email e a senha fornecidos para autenticação.
     * @return Token JWT válido caso as credenciais estejam corretas, ou status 401 se inválidas.
     */
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

    /**
     * Endpoint de login para clientes do App (Cardápio).
     *
     * @param dados Objeto contendo o email e a senha fornecidos para autenticação.
     * @return Token JWT válido caso as credenciais estejam corretas, ou status 401 se inválidas.
     */
    @PostMapping("/app")
    public ResponseEntity<?> loginApp(@RequestBody @Valid DadosAutenticacao dados) {
        try {
            var token = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());
            var auth = appAuthProvider.authenticate(token);
            var cliente = (Cliente) auth.getPrincipal();

            var emailDoCliente = cliente.getEmail();
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            var ip = request.getRemoteAddr();
            discordAlert.AlertDiscord("✅ Login em Cliente realizado com sucesso por: " + emailDoCliente + " (IP: " + ip + ")");
            Logger log = LoggerFactory.getLogger(getClass());
            log.warn(cliente.getAuthorities().toString());

            var tokenJWT = tokenService.generateToken(cliente);
            return ResponseEntity.ok(new DadosTokenJWT(tokenJWT));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas");
        }
    }
}

