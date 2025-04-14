package appetito.apicardapio.service;

import appetito.apicardapio.dto.cadastro.ChamadoCadastro;
import appetito.apicardapio.entity.Chamado;
import appetito.apicardapio.entity.Cliente;
import appetito.apicardapio.entity.Mesa;
import appetito.apicardapio.entity.UsuarioDashboard;
import appetito.apicardapio.enums.StatusChamado;
import appetito.apicardapio.exception.ResourceNotFoundException;
import appetito.apicardapio.repository.ChamadoRepository;
import appetito.apicardapio.repository.MesaRepository;
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

@Service
public class ChamadoService {

    private final ChamadoRepository chamadoRepository;

    private final MesaRepository mesaRepository;

    public ChamadoService(ChamadoRepository chamadoRepository, MesaRepository mesaRepository) {
        this.chamadoRepository = chamadoRepository;
        this.mesaRepository = mesaRepository;
    }

    public Chamado solicitarChamado(ChamadoCadastro dadosChamado, HttpServletRequest request) throws AccessDeniedException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof UsuarioDashboard usuarioDashboard)) {
            String ip = Optional.ofNullable(request.getHeader("X-Forwarded-For"))
                    .orElse(request.getRemoteAddr());
            ch.qos.logback.classic.Logger log = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(getClass());
            log.warn("🍯 HONEYPOT ALERT: Tentativa de criar estabelecimento sem ser UsuarioDashboard. IP: {}, Tipo: {}",
                    ip,
                    principal.getClass().getSimpleName());
            new DiscordAlert().AlertDiscord("❌ Tentativa indevida da API em chamado/pendentes - IP: " + ip);
            throw new AccessDeniedException("Honey Pot");
        }
        Cliente cliente = (Cliente) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Mesa mesa = mesaRepository.findById(dadosChamado.mesa_id())
                .orElseThrow(() -> new ResourceNotFoundException("Mesa não encontrada"));
        Chamado chamado = new Chamado();
        chamado.setMesa(mesa);
        chamado.setCliente(cliente);
        chamado.setMensagemAdicional(dadosChamado.mensagem());
        chamado.setStatus(StatusChamado.CHAMADO);

        return chamadoRepository.save(chamado);
    }

    public List<Chamado> listarChamadosPendentes(HttpServletRequest request) throws AccessDeniedException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof UsuarioDashboard)) {
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.isEmpty()) {
                ip = request.getRemoteAddr();
            }
            Logger log = LoggerFactory.getLogger(getClass());
            log.warn("Token indevido, tentou acessar endpoint de Dashboard, IP: {}, Tipo: {}, Endpoint: chamado/pendentes", ip, principal.getClass().getSimpleName());
            new DiscordAlert().AlertDiscord("❌ Tentativa indevida da API em chamado/pendentes - IP: " + ip);
            throw new AccessDeniedException("Honey Pot");
        }
        return chamadoRepository.findByStatus(StatusChamado.CHAMADO);
    }
    public Chamado atenderChamado(Long chamadoId, HttpServletRequest request) throws AccessDeniedException  {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof UsuarioDashboard)) {
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.isEmpty()) {
                ip = request.getRemoteAddr();
            }
            Logger log = LoggerFactory.getLogger(getClass());
            log.warn("Token indevido, tentou acessar endpoint de Dashboard, IP: {}, Tipo: {}, Endpoint: chamado/atender/", ip, principal.getClass().getSimpleName());
            new DiscordAlert().AlertDiscord("❌ Tentativa indevida da API em chamado/atender/ - IP: " + ip);
            throw new AccessDeniedException("Honey Pot");
        }
        Chamado chamado = chamadoRepository.findById(chamadoId)
                .orElseThrow(() -> new ResourceNotFoundException("Chamado não encontrado"));
        chamado.setStatus(StatusChamado.ATENDIDO);
        chamado.setDataHoraAtendimento(LocalDateTime.now());
        return chamadoRepository.save(chamado);
    }

    public Chamado cancelarChamado(Long chamadoId, HttpServletRequest request) throws AccessDeniedException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof Cliente)) {
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.isEmpty()) {
                ip = request.getRemoteAddr();
            }
            Logger log = LoggerFactory.getLogger(getClass());
            log.warn("Token indevido, tentou acessar endpoint de Dashboard, IP: {}, Tipo: {}, Endpoint: chamado/cancelar", ip, principal.getClass().getSimpleName());
            new DiscordAlert().AlertDiscord("❌ Tentativa indevida da API em chamado/cancelar/ - IP: " + ip);
            throw new AccessDeniedException("Honey Pot");
        }
        Chamado chamado = chamadoRepository.findById(chamadoId)
                .orElseThrow(() -> new ResourceNotFoundException("Chamado não encontrado"));
        chamado.setStatus(StatusChamado.CANCELADO);
        chamado.setDataHoraFechamento(LocalDateTime.now());
        return chamadoRepository.save(chamado);
    }

}

