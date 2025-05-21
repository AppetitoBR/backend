package appetito.apicardapio.service;

import appetito.apicardapio.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Serviço responsável por carregar os dados do cliente para autenticação no Spring Security.
 *
 * Esta implementação da interface {@link UserDetailsService} é utilizada pelo Spring Security
 * durante o processo de login para buscar o usuário com base no e-mail (username).
 *
 * No seu projeto, este serviço está configurado para autenticar usuários do tipo Cliente.
 */
@Service
public class AppUserDetailsService implements UserDetailsService {

    /**
     * Repositório que acessa os dados dos clientes cadastrados no sistema.
     */
    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * Método responsável por buscar o cliente pelo e-mail (username) fornecido durante o login.
     *
     * Este método é chamado automaticamente pelo Spring Security.
     * Se o cliente for encontrado, os dados serão usados para autenticação e autorização.
     * Caso contrário, será lançada uma exceção.
     *
     * @param username e-mail do cliente (utilizado como login)
     * @return um objeto {@link UserDetails} com as informações do cliente autenticado
     * @throws UsernameNotFoundException se o cliente não for encontrado no banco
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return clienteRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Cliente não encontrado"));
    }
}
