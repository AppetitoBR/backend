package appetito.apicardapio.service;

import appetito.apicardapio.entity.UsuarioDashboard;
import appetito.apicardapio.repository.UsuarioDashboardRepository;
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

   // public UsuarioDashboard salvarImagemPerfil(Long usuarioId, MultipartFile file) throws IOException {
   //     Optional<UsuarioDashboard> usuarioOpt = usuarioRepository.findById(usuarioId);
   //     if (usuarioOpt.isPresent()) {
   //         UsuarioDashboard usuario = usuarioOpt.get();
          //  usuario.setImagem_perfil(file.getBytes());
     //       return usuarioRepository.save(usuario);
      //  }
     //   return null;
  //  }

  //  public byte[] obterImagemPerfil(Long usuarioId) {
    //    Optional<UsuarioDashboard> usuarioOpt = usuarioRepository.findById(usuarioId);
      //  return usuarioOpt.map(UsuarioDashboard::getImagem_perfil).orElse(null);
    //}
}

