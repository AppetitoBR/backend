package appetito.apicardapio.service;


import appetito.apicardapio.dto.UsuarioCadastro;
import appetito.apicardapio.entity.Usuario;
import appetito.apicardapio.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario cadastrarUsuario(UsuarioCadastro dadosUsuario) {
        Usuario usuario = new Usuario(
                dadosUsuario.nome_completo(),
                dadosUsuario.cpf(),
                dadosUsuario.email(),
                dadosUsuario.senha(),
                dadosUsuario.perfil()
        );
        return usuarioRepository.save(usuario);
    }
}
