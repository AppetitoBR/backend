package appetito.apicardapio.security;


import appetito.apicardapio.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    @Autowired
    private TokenService tokenService;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        var tokenJWT = recuperarToken(request);
        if (tokenJWT != null) {
            try {
                var subject = tokenService.getSubject(tokenJWT);
                var usuario = usuarioRepository.findByEmail(subject);
                var auth = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
                logger.info("Usuário autenticado: " + usuario.getUsername());
            } catch (Exception e) {
                logger.error("Erro de autenticação: " + e.getMessage());
            }
        } else {
            logger.info("Token JWT não fornecido ou inválido.");
        }
        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request){
        var authorizationHeader = request.getHeader("Authorization");
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
            return authorizationHeader.replace("Bearer ", "");
        }
        return null;
    }
}
