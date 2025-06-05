package appetito.apicardapio.service;

import appetito.apicardapio.dto.cadastro.UsuarioDashboardCadastro;
import appetito.apicardapio.dto.detalhamento.UsuarioDashboardDetalhamento;
import appetito.apicardapio.dto.put.UsuarioDashboardAtualizacao;
import appetito.apicardapio.entity.Estabelecimento;
import appetito.apicardapio.entity.UsuarioDashboard;
import appetito.apicardapio.entity.UsuarioEstabelecimento;
import appetito.apicardapio.enums.PapelUsuario;
import appetito.apicardapio.repository.EstabelecimentoRepository;
import appetito.apicardapio.repository.UsuarioDashboardRepository;
import appetito.apicardapio.repository.UsuarioEstabelecimentoRepository;
import appetito.apicardapio.security.DiscordAlert;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioDashboardService {
    private final UsuarioDashboardRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioEstabelecimentoRepository usuarioEstabelecimentoRepository;
    private final EstabelecimentoRepository estabelecimentoRepository;

    public UsuarioDashboardService(UsuarioDashboardRepository usuarioRepository, PasswordEncoder passwordEncoder, UsuarioEstabelecimentoRepository usuarioEstabelecimentoRepository, EstabelecimentoRepository estabelecimentoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioEstabelecimentoRepository = usuarioEstabelecimentoRepository;
        this.estabelecimentoRepository = estabelecimentoRepository;
    }
    public byte[] obterImagemPerfil(Long usuarioId) {
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

    public UsuarioDashboard cadastrarUsuarioDashboard(UsuarioDashboardCadastro dadosUsuario) {
        if (usuarioRepository.existsByEmail(dadosUsuario.email())) {
            throw new IllegalStateException("E-mail já cadastrado!");
        }

        UsuarioDashboard usuario = new UsuarioDashboard(dadosUsuario);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuarioRepository.save(usuario);

        new DiscordAlert().AlertDiscord("Novo Usuario Dashboard cadastrado: " + usuario.getEmail());

        return usuario;
    }
    public void uploadImagemPerfil(Long id, MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();

        if (filename == null || !(filename.endsWith(".jpg") || filename.endsWith(".jpeg") || filename.endsWith(".png"))) {
            throw new IllegalArgumentException("Arquivo de imagem inválido. Apenas .jpg, .jpeg e .png são permitidos.");
        }

        if (file.getSize() > 2 * 1024 * 1024) {
            throw new IllegalArgumentException("O arquivo é muito grande. O tamanho máximo permitido é 2MB.");
        }

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Arquivo de imagem não pode estar vazio!");
        }

        UsuarioDashboard usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        usuario.setImagem_perfil(file.getBytes());
        usuarioRepository.save(usuario);
    }

    @Transactional
    public UsuarioDashboardDetalhamento atualizarUsuario(UsuarioDashboard usuario, UsuarioDashboardAtualizacao dadosAtualizados) {

        usuario.setNome_completo(dadosAtualizados.nome_completo());
        usuario.setTelefone(dadosAtualizados.telefone());
        usuario.setSituacao(dadosAtualizados.situacao());
        usuario.setData_atualizacao(LocalDate.now());

        usuarioRepository.save(usuario);
        return new UsuarioDashboardDetalhamento(usuario);
    }

    @Transactional
    public void deletarUsuarioDashboard(UsuarioDashboard usuario) {
        List<UsuarioEstabelecimento> vinculos = usuarioEstabelecimentoRepository.findByUsuario(usuario);

        for (UsuarioEstabelecimento vinculo : vinculos) {
            if (vinculo.getPapel() == PapelUsuario.ADMINISTRADOR) {
                Estabelecimento est = vinculo.getEstabelecimento();
                usuarioEstabelecimentoRepository.deleteAllByEstabelecimento(est);
                estabelecimentoRepository.delete(est);
            } else {
                usuarioEstabelecimentoRepository.delete(vinculo);
            }
        }

        usuarioRepository.delete(usuario);
    }



}

