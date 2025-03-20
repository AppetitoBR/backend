package appetito.apicardapio.service;

import appetito.apicardapio.entity.Usuario;
import appetito.apicardapio.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Optional;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario salvarImagemPerfil(Long usuarioId, MultipartFile file) throws IOException {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setImagem_perfil(file.getBytes());
            return usuarioRepository.save(usuario);
        }
        return null;
    }

    public byte[] obterImagemPerfil(Long usuarioId) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
        return usuarioOpt.map(Usuario::getImagem_perfil).orElse(null);
    }
}

