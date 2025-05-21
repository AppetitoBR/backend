package appetito.apicardapio.service;

import appetito.apicardapio.repository.UsuarioDashboardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Serviço responsável por carregar os dados do usuário do dashboard para autenticação no Spring Security.
 *
 * Esta implementação da interface {@link UserDetailsService} é utilizada pelo Spring Security
 * durante o processo de login para buscar o usuário com base no e-mail (username).
 *
 * No seu sistema, este serviço é usado para autenticar os usuários administrativos
 * que acessam o painel de controle (dashboard), como administradores, gerentes e atendentes.
 */
@Service
public class DashboardUserDetailsService implements UserDetailsService {

    /**
     * Repositório que acessa os dados dos usuários do dashboard cadastrados no sistema.
     */
    @Autowired
    private UsuarioDashboardRepository usuarioDashboardRepository;

    /**
     * Método responsável por buscar o usuário do dashboard pelo e-mail fornecido durante o login.
     *
     * Este método é chamado automaticamente pelo Spring Security.
     * Se o usuário for encontrado, os dados serão usados para autenticação e autorização.
     * Caso contrário, será lançada uma exceção.
     *
     * @param username e-mail do usuário do dashboard (utilizado como login)
     * @return um objeto {@link UserDetails} com as informações do usuário autenticado
     * @throws UsernameNotFoundException se o usuário não for encontrado no banco de dados
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioDashboardRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }
}