package appetito.apicardapio.service;

import appetito.apicardapio.entity.UsuarioDashboard;
import appetito.apicardapio.repository.UsuarioDashboardRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Optional;

@Service
public class UsuarioDashboardService {
    private final UsuarioDashboardRepository usuarioRepository;

    public UsuarioDashboardService(UsuarioDashboardRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public UsuarioDashboard salvarImagemPerfil(Long usuarioId, MultipartFile file, HttpServletRequest request) throws IOException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof UsuarioDashboard usuarioAutenticado)) {
            throw new AccessDeniedException("Você não está autenticado.");
        }
        if (!usuarioAutenticado.getUsuario_dashboard_id().equals(usuarioId)) {
            throw new AccessDeniedException("Você não tem permissão para salvar a imagem de outro usuário.");
        }

        Optional<UsuarioDashboard> usuarioOpt = usuarioRepository.findById(usuarioId);
        if (usuarioOpt.isPresent()) {
            UsuarioDashboard usuario = usuarioOpt.get();
            usuario.setImagem_perfil(file.getBytes());
            return usuarioRepository.save(usuario);
        }
        return null;
    }
    public byte[] obterImagemPerfil(Long usuarioId, HttpServletRequest request) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof UsuarioDashboard usuarioAutenticado)) {
            throw new AccessDeniedException("Você não está autenticado.");
        }
        if (!usuarioAutenticado.getUsuario_dashboard_id().equals(usuarioId)) {
            throw new AccessDeniedException("Você não tem permissão para ver a imagem de outro usuário.");
        }
        Optional<UsuarioDashboard> usuarioOpt = usuarioRepository.findById(usuarioId);
        return usuarioOpt.map(UsuarioDashboard::getImagem_perfil).orElse(null);
    }
}

