package appetito.apicardapio.service;

import appetito.apicardapio.dto.DadosFuncionario;
import appetito.apicardapio.dto.GetAll.EstabelecimentoDados;
import appetito.apicardapio.dto.GetAll.FuncionarioDados;
import appetito.apicardapio.dto.cadastro.EstabelecimentoCadastro;
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

/**
 * Serviço responsável pelas operações relacionadas a estabelecimentos,
 * incluindo cadastro, listagem, vínculo de funcionários e alteração de papéis.
 */
@Service
@Transactional
public class EstabelecimentoService {

    private final UsuarioEstabelecimentoRepository usuarioEstabelecimentoRepository;
    private final EstabelecimentoRepository estabelecimentoRepository;
    private final UsuarioDashboardRepository usuarioDashboardRepository;

    /**
     * Construtor da classe EstabelecimentoService.
     *
     * @param usuarioEstabelecimentoRepository repositório de vínculo usuário-estabelecimento
     * @param estabelecimentoRepository repositório de estabelecimentos
     * @param usuarioDashboardRepository repositório de usuários do dashboard
     */
    public EstabelecimentoService(UsuarioEstabelecimentoRepository usuarioEstabelecimentoRepository,
                                  EstabelecimentoRepository estabelecimentoRepository,
                                  UsuarioDashboardRepository usuarioDashboardRepository) {
        this.usuarioEstabelecimentoRepository = usuarioEstabelecimentoRepository;
        this.estabelecimentoRepository = estabelecimentoRepository;
        this.usuarioDashboardRepository = usuarioDashboardRepository;
    }

    /**
     * Cadastra um novo estabelecimento e vincula o usuário como ADMINISTRADOR.
     *
     * @param dadosEstabelecimento dados do novo estabelecimento
     * @param usuarioDashboard usuário que está cadastrando o estabelecimento
     * @return o estabelecimento cadastrado
     * @throws AccessDeniedException se o usuário já possui um estabelecimento
     */
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

    /**
     * Lista os estabelecimentos vinculados a um determinado usuário.
     *
     * @param usuario o usuário logado
     * @return lista de dados dos estabelecimentos
     */
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

    /**
     * Lista estabelecimentos com nome fantasia contendo o texto informado.
     *
     * @param nomeFantasia texto a ser buscado no nome fantasia
     * @return lista de dados dos estabelecimentos encontrados
     */
    public List<EstabelecimentoDados> listarPorNomeFantasia(String nomeFantasia) {
        List<Estabelecimento> estabelecimentos = estabelecimentoRepository
                .findByNomeFantasiaContainingIgnoreCase(nomeFantasia);

        return estabelecimentos.stream()
                .map(EstabelecimentoDados::new)
                .toList();
    }

    /**
     * Vincula um funcionário a um estabelecimento com o papel informado.
     *
     * @param dto dados do funcionário e do estabelecimento
     * @param administrador usuário administrador que está realizando o vínculo
     * @param ip endereço IP da solicitação (usado para log no Discord)
     * @throws IllegalArgumentException se o papel for inválido ou o usuário já estiver vinculado
     * @throws ResourceNotFoundException se o estabelecimento ou funcionário não for encontrado
     */
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

    /**
     * Atualiza o papel de um funcionário em um estabelecimento.
     *
     * @param dto dados do funcionário com novo papel
     * @param administrador usuário administrador que está realizando a atualização
     * @param ip endereço IP da solicitação (usado para log no Discord)
     * @throws AccessDeniedException se o estabelecimento não for encontrado
     * @throws IllegalArgumentException se o papel for inválido ou o funcionário não estiver vinculado
     */
    public void atualizarPapelFuncionario(DadosFuncionario dto, UsuarioDashboard administrador, String ip) throws AccessDeniedException {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(dto.estabelecimentoId())
                .orElseThrow(() -> new AccessDeniedException("Estabelecimento não encontrado ou acesso negado"));

        if (dto.papel() == null || !papelPermitido(dto.papel())) {
            throw new IllegalArgumentException("Papel de usuário inválido ou não permitido.");
        }

        UsuarioDashboard funcionario = usuarioDashboardRepository.findByEmail(dto.email())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário com e-mail não encontrado."));

        UsuarioEstabelecimento vinculo = usuarioEstabelecimentoRepository
                .findByUsuarioAndEstabelecimento(funcionario, estabelecimento)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não está vinculado ao estabelecimento informado."));

        vinculo.setPapel(dto.papel());
        usuarioEstabelecimentoRepository.save(vinculo);

        new DiscordAlert().AlertDiscord(
                "✏️ **" + administrador.getEmail() + "** alterou o papel de 👷 **" + funcionario.getEmail() + "** para **" + dto.papel().name() + "** 🌐 IP: " + ip);
    }

    /**
     * Lista os funcionários vinculados a um estabelecimento, excluindo o administrador logado.
     *
     * @param estabelecimentoId ID do estabelecimento
     * @param administrador usuário administrador logado
     * @return lista de dados dos funcionários
     * @throws AccessDeniedException se o estabelecimento não for encontrado
     */
    public List<FuncionarioDados> listarFuncionarios(Long estabelecimentoId, UsuarioDashboard administrador) throws AccessDeniedException {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(estabelecimentoId)
                .orElseThrow(() -> new AccessDeniedException("Estabelecimento não encontrado ou acesso negado"));

        return usuarioEstabelecimentoRepository.findAllByEstabelecimento(estabelecimento).stream()
                .filter(v -> !v.getUsuario().getUsuario_dashboard_id().equals(administrador.getUsuario_dashboard_id()))
                .map(v -> new FuncionarioDados(
                        v.getUsuario().getUsuario_dashboard_id(),
                        v.getUsuario().getNome_completo(),
                        v.getUsuario().getEmail(),
                        v.getPapel()))
                .toList();
    }

    /**
     * Verifica se um papel de usuário é permitido para vínculo.
     *
     * @param papel papel a ser verificado
     * @return true se o papel for ATENDENTE, GERENTE ou COZINHEIRO
     */
    private boolean papelPermitido(PapelUsuario papel) {
        return papel == PapelUsuario.ATENDENTE
                || papel == PapelUsuario.GERENTE
                || papel == PapelUsuario.COZINHEIRO;
    }
}
