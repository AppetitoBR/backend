package appetito.apicardapio.security;

import appetito.apicardapio.entity.Estabelecimento;
import appetito.apicardapio.entity.UsuarioDashboard;
import appetito.apicardapio.entity.UsuarioEstabelecimento;
import appetito.apicardapio.enums.PapelUsuario;
import appetito.apicardapio.repository.UsuarioEstabelecimentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;


/**
 * Serviço responsável por verificar permissões de acesso de usuários
 * do dashboard em relação aos estabelecimentos que estão vinculados.
 */
@Component
public class PreAuthorizeService {

    @Autowired
    private UsuarioEstabelecimentoRepository usuarioEstabelecimentoRepository;

    /**
     * Verifica se o usuário tem permissão para gerenciar um estabelecimento.
     * Os papéis válidos para essa ação são GERENTE e ADMINISTRADOR.
     *
     * @param estabelecimento o estabelecimento a ser gerenciado
     * @param usuarioDashboard o usuário que está tentando realizar a ação
     * @return true se o usuário tiver papel de GERENTE ou ADMINISTRADOR no estabelecimento
     */
    public boolean podeGerenciarEstabelecimento(Estabelecimento estabelecimento, UsuarioDashboard usuarioDashboard) {
        return usuarioEstabelecimentoRepository
                .findByUsuarioAndEstabelecimentoAndPapelIn(
                        usuarioDashboard,
                        estabelecimento,
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
            return false; // retorna false se não for um UsuarioDashboard
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
    public boolean podeAtenderEstabelecimenmto(Estabelecimento estabelecimento, UsuarioDashboard usuarioDashboard) {
        return usuarioEstabelecimentoRepository
                .findByUsuarioAndEstabelecimentoAndPapelIn(
                        usuarioDashboard,
                        estabelecimento,
                        List.of(PapelUsuario.ATENDENTE, PapelUsuario.GERENTE, PapelUsuario.ADMINISTRADOR)
                ).isPresent();
    }

    /**
     * Verifica se o usuário pode aceitar pedidos na cozinha.
     * Os papéis permitidos são COZINHEIRO, GERENTE e ADMINISTRADOR.
     *
     * @param estabelecimento o estabelecimento que está processando pedidos
     * @param usuarioDashboard o usuário que deseja aceitar pedidos
     * @return true se o usuário tiver permissão para aceitar pedidos da cozinha
     */
    public boolean podeAceitarPedidoCozinha(Estabelecimento estabelecimento, UsuarioDashboard usuarioDashboard) {
        return usuarioEstabelecimentoRepository
                .findByUsuarioAndEstabelecimentoAndPapelIn(
                        usuarioDashboard,
                        estabelecimento,
                        List.of(PapelUsuario.COZINHEIRO, PapelUsuario.GERENTE, PapelUsuario.ADMINISTRADOR)
                ).isPresent();
    }
}

