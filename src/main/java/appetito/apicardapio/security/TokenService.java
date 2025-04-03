package appetito.apicardapio.security;

import appetito.apicardapio.entity.UsuarioDashboard;
import appetito.apicardapio.entity.Cliente;
import appetito.apicardapio.repository.UsuarioEstabelecimentoRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(UsuarioDashboard usuario) {
        try {
            return JWT.create()
                    .withIssuer("appetito_dev")
                    .withSubject(usuario.getEmail())
                    .withClaim("id", usuario.getUsuario_dashboard_id())
                    .withClaim("papel", "DASHBOARD")
                    .withExpiresAt(dataExpiracao())
                    .sign(Algorithm.HMAC256(secret));
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token", exception);
        }
    }

    public String generateToken(Cliente cliente) {
        try {
            return JWT.create()
                    .withIssuer("appetito_dev")
                    .withSubject(cliente.getEmail())
                    .withClaim("id", cliente.getId())
                    .withClaim("papel", "CLIENTE")
                    .withExpiresAt(dataExpiracao())
                    .sign(Algorithm.HMAC256(secret));
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token", exception);
        }
    }

    public DecodedJWT decodeToken(String tokenJWT) {
        try {
            var algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("appetito_dev")
                    .build()
                    .verify(tokenJWT);
        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Token inválido ou expirado", exception);
        }
    }

    private Instant dataExpiracao() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}