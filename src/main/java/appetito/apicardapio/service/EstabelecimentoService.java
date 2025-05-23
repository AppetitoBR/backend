package appetito.apicardapio.service;

import appetito.apicardapio.dto.DadosFuncionario;
import appetito.apicardapio.dto.GetAll.EstabelecimentoDados;
import appetito.apicardapio.dto.cadastro.EstabelecimentoCadastro;
import appetito.apicardapio.dto.detalhamento.EstabelecimentoDetalhamento;
import appetito.apicardapio.entity.Estabelecimento;
import appetito.apicardapio.entity.UsuarioDashboard;
import appetito.apicardapio.entity.UsuarioEstabelecimento;
import appetito.apicardapio.enums.PapelUsuario;
import appetito.apicardapio.exception.ResourceNotFoundException;
import appetito.apicardapio.repository.EstabelecimentoRepository;
import appetito.apicardapio.repository.UsuarioDashboardRepository;
import appetito.apicardapio.repository.UsuarioEstabelecimentoRepository;
import appetito.apicardapio.security.DiscordAlert;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EstabelecimentoService {

    private final UsuarioEstabelecimentoRepository usuarioEstabelecimentoRepository;
    private final EstabelecimentoRepository estabelecimentoRepository;
    private final UsuarioDashboardRepository usuarioDashboardRepository;

    public EstabelecimentoService(UsuarioEstabelecimentoRepository usuarioEstabelecimentoRepository,
                                  EstabelecimentoRepository estabelecimentoRepository, UsuarioDashboardRepository usuarioDashboardRepository) {
        this.usuarioEstabelecimentoRepository = usuarioEstabelecimentoRepository;
        this.estabelecimentoRepository = estabelecimentoRepository;
        this.usuarioDashboardRepository = usuarioDashboardRepository;
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

    public void vincularFuncionario(DadosFuncionario dto, UsuarioDashboard administrador, String ip) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(dto.estabelecimentoId())
                .orElseThrow(() -> new ResourceNotFoundException("Estabelecimento não encontrado"));

        if (dto.papel() == null || !papelPermitido(dto.papel())) {
            throw new IllegalArgumentException("Papel de usuário inválido ou não permitido.");
        }

        if (dto.papel() == PapelUsuario.ADMINISTRADOR) {
            throw new IllegalArgumentException("Você não pode vincular outro administrador.");
        }

        UsuarioDashboard funcionario = usuarioDashboardRepository.findByEmail(dto.email())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário com e-mail não encontrado."));

        boolean jaVinculado = usuarioEstabelecimentoRepository.existsByUsuarioAndEstabelecimento(funcionario, estabelecimento);
        if (jaVinculado) {
            throw new IllegalArgumentException("Usuário já está vinculado a este estabelecimento.");
        }

        new DiscordAlert().AlertDiscord(
                "👨‍💼 **" + administrador.getEmail() + "** adicionou 👷 **" + funcionario.getEmail() + "** ao estabelecimento com sucesso!\n 🌐 IP: " + ip);

        UsuarioEstabelecimento vinculo = new UsuarioEstabelecimento(estabelecimento, funcionario, dto.papel());
        usuarioEstabelecimentoRepository.save(vinculo);
    }
    private boolean papelPermitido(PapelUsuario papel) {
        return papel == PapelUsuario.ATENDENTE
                || papel == PapelUsuario.GERENTE
                || papel == PapelUsuario.COZINHEIRO;
    }
}
