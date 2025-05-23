package appetito.apicardapio.security;

import appetito.apicardapio.entity.UsuarioDashboard;
import appetito.apicardapio.repository.UsuarioDashboardRepository;
import appetito.apicardapio.service.AppUserDetailsService;
import appetito.apicardapio.service.DashboardUserDetailsService;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * Filtro de segurança que intercepta todas as requisições HTTP para validar e
 * autenticar o usuário com base em um token JWT.
 *
 * <p>
 * Este filtro verifica a presença do token no header "Authorization",
 * decodifica o token JWT e autentica o usuário com base no tipo (papel) declarado
 * no token: {@code DASHBOARD} ou {@code CLIENTE}.
 * </p>
 *
 * <p>
 * Em caso de erro na verificação, o filtro bloqueia a requisição com o código apropriado
 * (401 ou 403). Caso o token seja válido, o usuário é autenticado e a requisição continua.
 * </p>
 *
 * <p><strong>⚠️ Planejamento futuro:</strong> está prevista a implementação de suporte
 * para **duplo JWT** (ex: token de acesso + refresh token ou múltiplos tokens por domínio).
 * Isso deve ser integrado neste filtro ou em um filtro complementar.</p>
 */
@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final DashboardUserDetailsService dashboardUserDetailsService;
    private final AppUserDetailsService appUserDetailsService;

    /**
     * Construtor que injeta os serviços necessários para autenticação.
     *
     * @param appUserDetailsService         serviço de autenticação para clientes (App)
     * @param dashboardUserDetailsService   serviço de autenticação para usuários do dashboard
     * @param tokenService                  serviço de manipulação e validação de JWT
     */
    public SecurityFilter(AppUserDetailsService appUserDetailsService, DashboardUserDetailsService dashboardUserDetailsService, TokenService tokenService) {
        this.appUserDetailsService = appUserDetailsService;
        this.dashboardUserDetailsService = dashboardUserDetailsService;
        this.tokenService = tokenService;
    }

    /**
     * Executa a lógica de filtro para autenticação baseada em JWT.
     *
     * @param request     requisição HTTP
     * @param response    resposta HTTP
     * @param filterChain cadeia de filtros
     * @throws ServletException em caso de erro do servlet
     * @throws IOException      em caso de erro de I/O
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        var tokenJWT = recuperarToken(request);

        if (tokenJWT != null) {
            try {
                DecodedJWT decodedJWT = tokenService.decodeToken(tokenJWT);

                if (decodedJWT.getSubject() == null) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido: subject ausente");
                    return;
                }

                String username = decodedJWT.getSubject();
                String papel = decodedJWT.getClaim("papel").asString();
                UserDetails userDetails;

                if ("DASHBOARD".equalsIgnoreCase(papel)) {
                    userDetails = dashboardUserDetailsService.loadUserByUsername(username);
                } else if ("CLIENTE".equalsIgnoreCase(papel)) {
                    userDetails = appUserDetailsService.loadUserByUsername(username);

                    if (request.getRequestURI().startsWith("/dashboard")) {
                        alertarHoneypot(request, username, papel);
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acesso não autorizado");
                        return;
                    }
                } else {
                    alertarHoneypot(request, username, papel);
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Tipo de usuário não reconhecido");
                    return;
                }

                var authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (JWTVerificationException e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido ou expirado");
                return;
            } catch (UsernameNotFoundException e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Usuário não encontrado");
                return;
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro durante a autenticação");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void alertarHoneypot(HttpServletRequest request, String username, String papel) {
        String ip = Optional.ofNullable(request.getHeader("X-Forwarded-For"))
                .orElse(request.getRemoteAddr());
        String uri = request.getRequestURI();

        String msg = String.format("🍯 HONEYPOT ALERT\nUsuário: **%s**\nPapel: **%s**\nURI: `%s`\nIP: `%s`",
                username, papel, uri, ip);

        new DiscordAlert().AlertDiscord(msg);
    }

    /**
     * Recupera o token JWT do cabeçalho {@code Authorization}.
     *
     * @param request requisição HTTP
     * @return token JWT ou {@code null} se não encontrado
     */
    private String recuperarToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null) {
            return authorizationHeader.replace("Bearer ", "");
        }
        return null;
    }
}
