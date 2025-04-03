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

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private DashboardUserDetailsService dashboardUserDetailsService;

    @Autowired
    private AppUserDetailsService appUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        var tokenJWT = recuperarToken(request);

        if (tokenJWT != null) {
            try {
                DecodedJWT decodedJWT = tokenService.decodeToken(tokenJWT);

                // Verifica se o token contém as claims necessárias
                if (decodedJWT.getSubject() == null) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido: subject ausente");
                    return;
                }

                String username = decodedJWT.getSubject();
                UserDetails userDetails;

                // Verifica o escopo do token (dashboard ou app)
                String papel = decodedJWT.getClaim("papel").asString();

                if ("DASHBOARD".equalsIgnoreCase(papel)) {
                    userDetails = dashboardUserDetailsService.loadUserByUsername(username);
                } else if ("CLIENTE".equalsIgnoreCase(papel)) {
                    userDetails = appUserDetailsService.loadUserByUsername(username);
                } else {
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

    private String recuperarToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null) {
            return authorizationHeader.replace("Bearer ", "");
        }
        return null;
    }
}
