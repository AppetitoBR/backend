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


/**
 * Serviço responsável por gerar e validar tokens JWT para autenticação
 * de usuários do sistema.
 *
 * <p>Os tokens contêm as informações do usuário, como {@code email}, {@code id}
 * e o {@code papel} (CLIENTE ou DASHBOARD), além de uma data de expiração.</p>
 *
 * <p><strong>⚠️ Planejamento futuro:</strong> está prevista a implementação
 * de suporte para JWT duplo (access token + refresh token), onde o método
 * {@code generateToken} poderá ser expandido para retornar um par de tokens,
 * e um novo método para renovação será adicionado.</p>
 */
@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    /**
     * Gera um token JWT para um usuário do tipo {@code UsuarioDashboard}.
     *
     * @param usuario o usuário do dashboard autenticado
     * @return token JWT válido por 2 horas
     */
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

    /**
     * Gera um token JWT para um usuário do tipo {@code Cliente}.
     *
     * @param cliente o cliente autenticado
     * @return token JWT válido por 2 horas
     */
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

    /**
     * Decodifica e valida um token JWT.
     *
     * @param tokenJWT o token JWT a ser validado
     * @return objeto {@code DecodedJWT} com as claims extraídas
     */
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

    /**
     * Define a data de expiração do token.
     *
     * @return {@code Instant} representando o momento de expiração (2 horas a partir de agora)
     */
    private Instant dataExpiracao() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}