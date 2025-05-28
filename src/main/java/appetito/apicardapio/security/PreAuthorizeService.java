package appetito.apicardapio.security;

import appetito.apicardapio.entity.Estabelecimento;
import appetito.apicardapio.entity.UsuarioDashboard;
import appetito.apicardapio.entity.UsuarioEstabelecimento;
import appetito.apicardapio.enums.PapelUsuario;
import appetito.apicardapio.exception.ResourceNotFoundException;
import appetito.apicardapio.repository.EstabelecimentoRepository;
import appetito.apicardapio.repository.UsuarioEstabelecimentoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;


/**
 * Serviço responsável por verificar permissões de acesso de usuários
 * do dashboard em relação aos estabelecimentos que estão vinculados.
 */
@Component
public class PreAuthorizeService {


    private final UsuarioEstabelecimentoRepository usuarioEstabelecimentoRepository;
    private final EstabelecimentoRepository estabelecimentoRepository;

    public PreAuthorizeService(UsuarioEstabelecimentoRepository usuarioEstabelecimentoRepository, EstabelecimentoRepository estabelecimentoRepository) {
        this.usuarioEstabelecimentoRepository = usuarioEstabelecimentoRepository;
        this.estabelecimentoRepository = estabelecimentoRepository;
    }

    /**
     * Verifica se o usuário tem permissão para gerenciar um estabelecimento.
     * Os papéis válidos para essa ação são GERENTE e ADMINISTRADOR.
     *
     * @param estabelecimentoId o estabelecimento a ser gerenciado
     * @param usuarioDashboard o usuário que está tentando realizar a ação
     * @return true se o usuário tiver papel de GERENTE ou ADMINISTRADOR no estabelecimento
     */
    public boolean podeGerenciarEstabelecimento(Long estabelecimentoId, UsuarioDashboard usuarioDashboard) {
        return usuarioEstabelecimentoRepository
                .findByUsuarioAndEstabelecimento_EstabelecimentoIdAndPapelIn(
                        usuarioDashboard,
                        estabelecimentoId,
                        List.of(PapelUsuario.GERENTE, PapelUsuario.ADMINISTRADOR)
                )
                .isPresent();
    }

    /**
     * Verifica se o usuário autenticado é ADMINISTRADOR do estabelecimento.
     *
     * @param principal o objeto autenticado (esperado ser um {@link UsuarioDashboard})
     * @param estabelecimento o estabelecimento a ser verificado
     * @return true se o usuário for ADMINISTRADOR no estabelecimento
     */
    public boolean ehAdministrador(Object principal, Estabelecimento estabelecimento) {
        if (!(principal instanceof UsuarioDashboard usuarioDashboard)) {
            return false;
        }
        Optional<UsuarioEstabelecimento> vinculo =
                usuarioEstabelecimentoRepository.findByUsuarioAndEstabelecimento(usuarioDashboard, estabelecimento);
        return vinculo
                .map(v -> v.getPapel() == PapelUsuario.ADMINISTRADOR)
                .orElse(false);
    }

    /**
     * Verifica se o usuário está vinculado ao estabelecimento, independentemente do papel.
     *
     * @param estabelecimento o estabelecimento a ser verificado
     * @param usuarioDashboard o usuário a ser verificado
     * @return true se o vínculo existir
     */
    public boolean estaVinculadoAoEstabelecimento(Estabelecimento estabelecimento, UsuarioDashboard usuarioDashboard) {
        return usuarioEstabelecimentoRepository.existsByUsuarioAndEstabelecimento(usuarioDashboard, estabelecimento);
    }

    /**
     * Verifica se o usuário pode acessar o dashboard de atendimento.
     * Os papéis permitidos são ATENDENTE, GERENTE e ADMINISTRADOR.
     *
     * @param estabelecimento o estabelecimento a ser acessado
     * @param usuarioDashboard o usuário que deseja acessar
     * @return true se o usuário tiver permissão para atender
     */
    public boolean podeAtenderEstabelecimento(Estabelecimento estabelecimento, UsuarioDashboard usuarioDashboard) {
        return usuarioEstabelecimentoRepository
                .findByUsuarioAndEstabelecimentoAndPapelIn(
                        usuarioDashboard,
                        estabelecimento,
                        List.of(PapelUsuario.ATENDENTE, PapelUsuario.GERENTE, PapelUsuario.ADMINISTRADOR)
                ).isPresent();
    }
    // esse aqui é personalizado
    public boolean podeAtenderEstabelecimentoPorNomeFantasia(String nomeFantasia, UsuarioDashboard usuarioDashboard) {
        return estabelecimentoRepository.findByNomeFantasia(nomeFantasia)
                .map(estabelecimento -> podeAtenderEstabelecimento(estabelecimento, usuarioDashboard))
                .orElse(false);
    }

    /**
     * Verifica se o usuário pode aceitar pedidos na cozinha.
     * Os papéis permitidos são COZINHEIRO, GERENTE e ADMINISTRADOR.
     *
     * @param estabelecimentoId o estabelecimento que está processando pedidos
     * @param usuarioDashboard o usuário que deseja aceitar pedidos
     * @return true se o usuário tiver permissão para aceitar pedidos da cozinha
     */
    public boolean podeAceitarPedidoCozinha(Long estabelecimentoId, UsuarioDashboard usuarioDashboard) {
        estabelecimentoRepository.findById(estabelecimentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Estabelecimento não encontrado."));
        return usuarioEstabelecimentoRepository
                .findByUsuarioAndEstabelecimento_EstabelecimentoIdAndPapelIn(
                        usuarioDashboard,
                        estabelecimentoId,
                        List.of(PapelUsuario.COZINHEIRO, PapelUsuario.GERENTE, PapelUsuario.ADMINISTRADOR)
                ).isPresent();
    }
}

