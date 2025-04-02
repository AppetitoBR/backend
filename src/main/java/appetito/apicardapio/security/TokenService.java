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

    @Autowired
    private UsuarioEstabelecimentoRepository usuarioEstabelecimentoRepository;

    public String generateToken(UsuarioDashboard usuario) {
        try {
            var usuarioEstabelecimento = usuarioEstabelecimentoRepository.findByUsuario(usuario);
            if (usuarioEstabelecimento == null) {
                throw new RuntimeException("Usuário não associado a nenhum estabelecimento.");
            }
            return generateJwt(usuario.getUsername(),
                    usuario.getUsuario_dashboard_id(),
                    usuarioEstabelecimento.getEstabelecimento().getEstabelecimento_id(),
                    usuarioEstabelecimento.getPapel().name());
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token", exception);
        }
    }

    public String generateToken(Cliente cliente) {
        try {
            return generateJwt(cliente.getUsername(),
                    cliente.getId(),
                    null,
                    "CLIENTE");
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token", exception);
        }
    }

    private String generateJwt(String username, Long userId, Long estabelecimentoId, String role) {
        var algorithm = Algorithm.HMAC256(secret);
        var jwtBuilder = JWT.create()
                .withIssuer("appetito_dev")  // Corrigi o nome do issuer (estava "appetito_dev" no decode)
                .withSubject(username)
                .withClaim("id", userId)
                .withClaim("papel", role)
                .withExpiresAt(dataExpiracao());

        if (estabelecimentoId != null) {
            jwtBuilder.withClaim("estabelecimento_id", estabelecimentoId);
        }

        return jwtBuilder.sign(algorithm);
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