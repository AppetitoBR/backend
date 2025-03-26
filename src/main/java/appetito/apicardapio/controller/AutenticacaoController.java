package appetito.apicardapio.controller;

import appetito.apicardapio.entity.Usuario;
import appetito.apicardapio.dto.DadosTokenJWT;
import appetito.apicardapio.repository.UsuarioEstabelecimentoRepository;
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

    private final AuthenticationManager manager;

    private final TokenService tokenService;

    private final UsuarioEstabelecimentoRepository usuarioEstabelecimentoRepository;

    public AutenticacaoController(AuthenticationManager manager, TokenService tokenService, UsuarioEstabelecimentoRepository usuarioEstabelecimentoRepository) {
        this.manager = manager;
        this.tokenService = tokenService;
        this.usuarioEstabelecimentoRepository = usuarioEstabelecimentoRepository;
    }

        @PostMapping
        public ResponseEntity<?> autenticar(@RequestBody @Valid DadosAutenticacao dados) {
            try {
                var token = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());
                var autenticacao = manager.authenticate(token);

                var usuario = (Usuario) autenticacao.getPrincipal();

                var usuarioEstabelecimento = usuarioEstabelecimentoRepository.findByUsuario(usuario);

                if (usuarioEstabelecimento != null) {
                    var tokenJWT = tokenService.generateToken(usuario);
                    return ResponseEntity.ok(new DadosTokenJWT(tokenJWT));
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body("Usuário não possui papel associado a um estabelecimento.");
                }
            } catch (BadCredentialsException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro durante a autenticação: " + e.getMessage());
            }
        }
    }
