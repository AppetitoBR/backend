package appetito.apicardapio.service;

import appetito.apicardapio.dto.cadastro.ChamadoCadastro;
import appetito.apicardapio.entity.Chamado;
import appetito.apicardapio.entity.Mesa;
import appetito.apicardapio.entity.UsuarioDashboard;
import appetito.apicardapio.enums.StatusChamado;
import appetito.apicardapio.exception.ResourceNotFoundException;
import appetito.apicardapio.repository.ChamadoRepository;
import appetito.apicardapio.repository.MesaRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChamadoService {

    private final ChamadoRepository chamadoRepository;

    private final MesaRepository mesaRepository;

    public ChamadoService(ChamadoRepository chamadoRepository, MesaRepository mesaRepository) {
        this.chamadoRepository = chamadoRepository;
        this.mesaRepository = mesaRepository;
    }

    public Chamado solicitarChamado(ChamadoCadastro request) {
        UsuarioDashboard usuario = (UsuarioDashboard) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Mesa mesa = mesaRepository.findById(request.mesa_id())
                .orElseThrow(() -> new ResourceNotFoundException("Mesa não encontrada"));
        Chamado chamado = new Chamado();
        chamado.setMesa(mesa);
        chamado.setUsuario(usuario);
        chamado.setMensagemAdicional(request.mensagem());
        chamado.setStatus(StatusChamado.CHAMADO);

        return chamadoRepository.save(chamado);
    }

    public List<Chamado> listarChamadosPendentes() {
        return chamadoRepository.findByStatus(StatusChamado.CHAMADO);
    }

    public Chamado atenderChamado(Long chamadoId) {
        Chamado chamado = chamadoRepository.findById(chamadoId)
                .orElseThrow(() -> new ResourceNotFoundException("Chamado não encontrado"));

        chamado.setStatus(StatusChamado.ATENDIDO);
        chamado.setDataHoraAtendimento(LocalDateTime.now());

        return chamadoRepository.save(chamado);
    }

    public Chamado cancelarChamado(Long chamadoId) {
        Chamado chamado = chamadoRepository.findById(chamadoId)
                .orElseThrow(() -> new ResourceNotFoundException("Chamado não encontrado"));

        chamado.setStatus(StatusChamado.CANCELADO);
        chamado.setDataHoraFechamento(LocalDateTime.now());

        return chamadoRepository.save(chamado);
    }
}

