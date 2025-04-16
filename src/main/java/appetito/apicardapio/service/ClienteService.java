package appetito.apicardapio.service;

import appetito.apicardapio.entity.Cliente;
import appetito.apicardapio.exception.ResourceNotFoundException;
import appetito.apicardapio.repository.ClienteRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class ClienteService {
    private final ClienteRepository clienteRepository;
    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Cliente salvarImagemPerfil(Long clienteId, MultipartFile file, HttpServletRequest request) throws IOException {
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

    public byte[] obterImagemPerfil(Long clienteId, HttpServletRequest request) {
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
