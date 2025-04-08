package appetito.apicardapio.controller;

import appetito.apicardapio.dto.cadastro.ChamadoCadastro;
import appetito.apicardapio.dto.detalhamento.ChamadoDetalhamento;
import appetito.apicardapio.entity.Chamado;
import appetito.apicardapio.service.ChamadoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/chamado")
public class ChamadoController {

    @Autowired
    private ChamadoService chamadoService;

    @PostMapping("/solicitar")
    public ResponseEntity<ChamadoDetalhamento> solicitarChamado(@RequestBody @Valid ChamadoCadastro dadosCadastro, HttpServletRequest request) throws AccessDeniedException {

        Chamado chamado = chamadoService.solicitarChamado(dadosCadastro, request);
        return ResponseEntity.ok(new ChamadoDetalhamento(chamado));
    }
    @GetMapping("/pendentes")
    public ResponseEntity<List<ChamadoDetalhamento>> listarChamadosPendentes(HttpServletRequest request) throws AccessDeniedException {
        List<ChamadoDetalhamento> chamados = chamadoService.listarChamadosPendentes(request)
                .stream()
                .map(ChamadoDetalhamento::new)
                .toList();
        return ResponseEntity.ok(chamados);
    }
    @PutMapping("/atender/{id}")
    public ResponseEntity<ChamadoDetalhamento> atenderChamado(@PathVariable Long id, HttpServletRequest request) throws AccessDeniedException {
        Chamado chamado = chamadoService.atenderChamado(id,request);
        return ResponseEntity.ok(new ChamadoDetalhamento(chamado));
    }

    @PutMapping("/cancelar/{id}")
    public ResponseEntity<ChamadoDetalhamento> cancelarChamado(@PathVariable Long id, HttpServletRequest request) throws AccessDeniedException {
        Chamado chamado = chamadoService.cancelarChamado(id, request);
        return ResponseEntity.ok(new ChamadoDetalhamento(chamado));
    }

}