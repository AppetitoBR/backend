package appetito.apicardapio.service;

import appetito.apicardapio.entity.Cliente;
import appetito.apicardapio.exception.ResourceNotFoundException;
import appetito.apicardapio.repository.ClienteRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

/**
 * Serviço para operações relacionadas ao cliente, especialmente para gerenciamento da imagem de perfil.
 */
@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    /**
     * Construtor para injeção do repositório de clientes.
     *
     * @param clienteRepository Repositório para acesso aos dados dos clientes.
     */
    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    /**
     * Salva a imagem de perfil para um cliente autenticado.
     * <p>
     * Verifica se o usuário autenticado é o mesmo cliente cujo perfil será atualizado.
     *
     * @param clienteId ID do cliente que terá a imagem atualizada.
     * @param file Arquivo contendo a nova imagem de perfil.
     * @return Cliente atualizado com a nova imagem de perfil.
     * @throws IOException Caso ocorra erro ao ler o arquivo da imagem.
     * @throws AccessDeniedException Caso o usuário não esteja autenticado ou tente alterar a imagem de outro cliente.
     * @throws ResourceNotFoundException Caso o cliente com o ID informado não exista.
     */
    public Cliente salvarImagemPerfil(Long clienteId, MultipartFile file) throws IOException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof Cliente clienteAutenticado)) {
            throw new AccessDeniedException("Você não está autenticado.");
        }

        if (!clienteAutenticado.getId().equals(clienteId)) {
            throw new AccessDeniedException("Você não tem permissão para salvar a imagem de outro cliente.");
        }

        Optional<Cliente> clienteOpt = clienteRepository.findById(clienteId);
        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            cliente.setImagemPerfil(file.getBytes());
            return clienteRepository.save(cliente);
        }

        throw new ResourceNotFoundException("Cliente não encontrado.");
    }

    /**
     * Obtém a imagem de perfil do cliente autenticado.
     * <p>
     * Verifica se o usuário autenticado é o mesmo cliente cujo perfil está sendo acessado.
     *
     * @param clienteId ID do cliente cuja imagem de perfil será retornada.
     * @return Array de bytes da imagem de perfil, ou null se o cliente não possuir imagem.
     * @throws AccessDeniedException Caso o usuário não esteja autenticado ou tente acessar a imagem de outro cliente.
     */
    public byte[] obterImagemPerfil(Long clienteId) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof Cliente clienteAutenticado)) {
            throw new AccessDeniedException("Você não está autenticado.");
        }

        if (!clienteAutenticado.getId().equals(clienteId)) {
            throw new AccessDeniedException("Você não tem permissão para ver a imagem de outro cliente.");
        }

        Optional<Cliente> clienteOpt = clienteRepository.findById(clienteId);
        return clienteOpt.map(Cliente::getImagemPerfil).orElse(null);
    }
}

