package appetito.apicardapio.service;

import appetito.apicardapio.dto.GetAll.EstabelecimentoDados;
import appetito.apicardapio.dto.cadastro.EstabelecimentoCadastro;
import appetito.apicardapio.dto.detalhamento.EstabelecimentoDetalhamento;
import appetito.apicardapio.entity.Estabelecimento;
import appetito.apicardapio.entity.UsuarioDashboard;
import appetito.apicardapio.entity.UsuarioEstabelecimento;
import appetito.apicardapio.enums.PapelUsuario;
import appetito.apicardapio.repository.EstabelecimentoRepository;
import appetito.apicardapio.repository.UsuarioEstabelecimentoRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EstabelecimentoService {

    private final UsuarioEstabelecimentoRepository usuarioEstabelecimentoRepository;
    private final EstabelecimentoRepository estabelecimentoRepository;

    public EstabelecimentoService(UsuarioEstabelecimentoRepository usuarioEstabelecimentoRepository,
                                  EstabelecimentoRepository estabelecimentoRepository) {
        this.usuarioEstabelecimentoRepository = usuarioEstabelecimentoRepository;
        this.estabelecimentoRepository = estabelecimentoRepository;
    }

    public Estabelecimento cadastrarEstabelecimento(EstabelecimentoCadastro dadosEstabelecimento, UsuarioDashboard usuarioDashboard) {
        boolean jaPossuiEstabelecimento = usuarioEstabelecimentoRepository.existsByUsuario(usuarioDashboard);
        if (jaPossuiEstabelecimento) {
            throw new AccessDeniedException("Você já possui um estabelecimento cadastrado.");
        }

        Estabelecimento estabelecimento = new Estabelecimento(dadosEstabelecimento);
        estabelecimento.setUsuarioCadastro(usuarioDashboard);
        estabelecimentoRepository.save(estabelecimento);

        if (estabelecimento.getUrl_cardapio_digital() == null) {
            String urlCardapio = "https://" + estabelecimento.getNomeFantasia() + ".localhost:8080";
            estabelecimento.setUrl_cardapio_digital(urlCardapio);
            estabelecimentoRepository.save(estabelecimento);
        }

        UsuarioEstabelecimento usuariodoestabelecimento = new UsuarioEstabelecimento(estabelecimento, usuarioDashboard, PapelUsuario.ADMINISTRADOR);
        usuarioEstabelecimentoRepository.save(usuariodoestabelecimento);

        return estabelecimento;

    }

    public List<EstabelecimentoDados> listarEstabelecimentosDoUsuario(UsuarioDashboard usuario) {
        List<Estabelecimento> estabelecimentos = usuarioEstabelecimentoRepository
                .findAllByUsuario(usuario)
                .stream()
                .map(UsuarioEstabelecimento::getEstabelecimento)
                .toList();

        return estabelecimentos.stream()
                .map(EstabelecimentoDados::new)
                .toList();
    }

    public List<EstabelecimentoDados> listarPorNomeFantasia(String nomeFantasia) {
        List<Estabelecimento> estabelecimentos = estabelecimentoRepository
                .findByNomeFantasiaContainingIgnoreCase(nomeFantasia);

        return estabelecimentos.stream()
                .map(EstabelecimentoDados::new)
                .toList();
    }
}
