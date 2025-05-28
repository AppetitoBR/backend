package appetito.apicardapio.service;

import appetito.apicardapio.dto.cadastro.ChamadoCadastro;
import appetito.apicardapio.entity.*;
import appetito.apicardapio.enums.StatusChamado;
import appetito.apicardapio.exception.ResourceNotFoundException;
import appetito.apicardapio.repository.ChamadoRepository;
import appetito.apicardapio.repository.EstabelecimentoRepository;
import appetito.apicardapio.repository.MesaRepository;
import appetito.apicardapio.repository.UsuarioEstabelecimentoRepository;
import appetito.apicardapio.security.DiscordAlert;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.file.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Serviço responsável pelo gerenciamento dos chamados,
 * incluindo solicitação, listagem, atendimento e cancelamento.
 */
@Service
public class ChamadoService {

    private final ChamadoRepository chamadoRepository;
    private final MesaRepository mesaRepository;
    private final EstabelecimentoRepository estabelecimentoRepository;

    /**
     * Construtor para injeção das dependências dos repositórios.
     *
     * @param chamadoRepository Repositório para acesso a dados dos chamados.
     * @param mesaRepository Repositório para acesso a dados das mesas.
     * @param estabelecimentoRepository Repositório para acesso a dados dos estabelecimentos.
     */
    public ChamadoService(ChamadoRepository chamadoRepository, MesaRepository mesaRepository, EstabelecimentoRepository estabelecimentoRepository) {
        this.chamadoRepository = chamadoRepository;
        this.mesaRepository = mesaRepository;
        this.estabelecimentoRepository = estabelecimentoRepository;
    }

    /**
     * Solicita a abertura de um novo chamado para uma mesa específica.
     * Permite que clientes autenticados ou visitantes realizem a solicitação.
     * Caso o solicitante seja visitante, um alerta é enviado via Discord.
     *
     * @param dadosChamado Dados para cadastro do chamado, incluindo ID da mesa e mensagem adicional.
     * @param request Objeto HTTP request para obter informações do cliente (ex: IP).
     * @return O chamado criado e salvo no banco de dados.
     * @throws AccessDeniedException Se houver problema de autorização.
     * @throws ResourceNotFoundException Se a mesa não for encontrada.
     */
    public Chamado solicitarChamado(ChamadoCadastro dadosChamado, HttpServletRequest request) throws AccessDeniedException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Cliente cliente = null;

        if (principal instanceof Cliente c) {
            cliente = c;
        }

        Mesa mesa = mesaRepository.findById(dadosChamado.mesa_id())
                .orElseThrow(() -> new ResourceNotFoundException("Mesa não encontrada"));

        validarMesa(mesa, mesa.getEstabelecimento());

        Chamado chamado = new Chamado();
        chamado.setMesa(mesa);
        chamado.setMensagemAdicional(dadosChamado.mensagem());
        chamado.setStatus(StatusChamado.CHAMADO);
        chamado.setCliente(cliente);

        if (cliente == null) {
            String ip = Optional.ofNullable(request.getHeader("X-Forwarded-For"))
                    .orElse(request.getRemoteAddr());
            Logger log = LoggerFactory.getLogger(getClass());
            log.warn("Chamado solicitado por visitante. IP: {}", ip);
            new DiscordAlert().AlertDiscord("📢 Chamado realizado por visitante - IP: " + ip);
        }

        return chamadoRepository.save(chamado);
    }

    /**
     * Lista os chamados pendentes de um estabelecimento específico.
     *
     * @param estabelecimentoId ID do estabelecimento para filtrar os chamados.
     * @return Lista de chamados com status 'CHAMADO' relacionados ao estabelecimento.
     * @throws ResourceNotFoundException Se o estabelecimento não for encontrado.
     */
    public List<Chamado> listarChamadosPendentes(Long estabelecimentoId) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(estabelecimentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Estabelecimento não encontrado."));

        return chamadoRepository.findByStatusAndMesa_Estabelecimento(StatusChamado.CHAMADO, estabelecimento);
    }

    /**
     * Marca um chamado como atendido, validando se o chamado pertence ao estabelecimento informado.
     *
     * @param estabelecimentoId ID do estabelecimento que atenderá o chamado.
     * @param chamadoId ID do chamado a ser atendido.
     * @return O chamado atualizado com status 'ATENDIDO' e data/hora de atendimento.
     * @throws AccessDeniedException Se o chamado não pertencer ao estabelecimento.
     * @throws ResourceNotFoundException Se estabelecimento ou chamado não forem encontrados.
     */
    public Chamado atenderChamado(Long estabelecimentoId, Long chamadoId) throws AccessDeniedException {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(estabelecimentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Estabelecimento não encontrado."));

        Chamado chamado = chamadoRepository.findById(chamadoId)
                .orElseThrow(() -> new ResourceNotFoundException("Chamado não encontrado."));

        validarMesa(chamado.getMesa(), estabelecimento);

        chamado.setStatus(StatusChamado.ATENDIDO);
        chamado.setDataHoraAtendimento(LocalDateTime.now());

        return chamadoRepository.save(chamado);
    }

    /**
     * Cancela um chamado, validando se o usuário atual (cliente ou usuário do dashboard) tem permissão para tal.
     *
     * @param estabelecimentoId ID do estabelecimento vinculado ao chamado.
     * @param chamadoId ID do chamado a ser cancelado.
     * @return O chamado atualizado com status 'CANCELADO' e data/hora de fechamento.
     * @throws AccessDeniedException Se o usuário não tiver permissão para cancelar o chamado.
     * @throws ResourceNotFoundException Se o chamado ou estabelecimento não forem encontrados.
     */
    public Chamado cancelarChamado(Long estabelecimentoId, Long chamadoId) throws AccessDeniedException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Chamado chamado = chamadoRepository.findById(chamadoId)
                .orElseThrow(() -> new ResourceNotFoundException("Chamado não encontrado"));

        if (principal instanceof Cliente cliente) {
            if (!chamado.getCliente().getId().equals(cliente.getId())) {
                throw new AccessDeniedException("Você não pode cancelar chamados de outros clientes.");
            }
        } else if (principal instanceof UsuarioDashboard usuario) {
            Estabelecimento estabelecimento = estabelecimentoRepository.findById(estabelecimentoId)
                    .orElseThrow(() -> new ResourceNotFoundException("Estabelecimento não encontrado"));

            validarMesa(chamado.getMesa(), estabelecimento);
        }

        chamado.setStatus(StatusChamado.CANCELADO);
        chamado.setDataHoraFechamento(LocalDateTime.now());
        return chamadoRepository.save(chamado);
    }

    /**
     * Valida se a mesa pertence ao estabelecimento informado.
     *
     * @param mesa Mesa a ser validada.
     * @param estabelecimento Estabelecimento que deve ser o proprietário da mesa.
     * @throws AccessDeniedException Caso a mesa não pertença ao estabelecimento.
     */
    private void validarMesa(Mesa mesa, Estabelecimento estabelecimento) throws AccessDeniedException {
        if (!mesa.getEstabelecimento().getEstabelecimentoId().equals(estabelecimento.getEstabelecimentoId())) {
            throw new AccessDeniedException("Chamado não pertence a este estabelecimento.");
        }
    }

    /**
     * Lista todos os chamados realizados pelo cliente autenticado.
     *
     * @return Lista de chamados associados ao cliente autenticado.
     * @throws AccessDeniedException Caso o usuário autenticado não seja um cliente.
     */
    public List<Chamado> listarChamadosDoCliente() throws AccessDeniedException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof Cliente cliente)) {
            throw new AccessDeniedException("Acesso restrito a clientes.");
        }

        return chamadoRepository.findByCliente(cliente);
    }

}

