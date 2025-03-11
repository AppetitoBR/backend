package appetito.apicardapio.infra;
import appetito.apicardapio.entity.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {
    @Value("${api.security.token.secret}")
    private String secret;
        public String generateToken(Usuario usuario) {
            try {
                var algorithm = Algorithm.HMAC256(secret);
                return JWT.create().withIssuer("appetito_db")
                        .withSubject(usuario.getEmail())
                        .withClaim("id", usuario.getUsuario_id())
                        .withExpiresAt(dataExpiracao())
                        .sign(algorithm);
            }catch (JWTCreationException exception) {
                throw new RuntimeException("Erro ao gerar token", exception);
            }
        }
    public String getSubject(String tokenJWT) {
            try {
                var algorithm = Algorithm.HMAC256(secret);
                return JWT.require(algorithm)
                        .withIssuer("appetito_db")
                        .build()
                        .verify(tokenJWT)
                        .getSubject();
            }catch (JWTVerificationException exception) {
                throw new RuntimeException("Token invalido ou Expirado", exception);
            }
    }
    private Instant dataExpiracao() {
           return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
