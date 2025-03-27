package appetito.apicardapio.controller;

import appetito.apicardapio.dto.cadastro.ChamadoCadastro;
import appetito.apicardapio.dto.detalhamento.ChamadoDetalhamento;
import appetito.apicardapio.entity.Chamado;
import appetito.apicardapio.service.ChamadoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chamado")
public class ChamadoController {

    @Autowired
    private ChamadoService chamadoService;

    @PostMapping("/solicitar")
    public ResponseEntity<ChamadoDetalhamento> solicitarChamado(@RequestBody @Valid ChamadoCadastro request) {

        Chamado chamado = chamadoService.solicitarChamado(request);
        return ResponseEntity.ok(new ChamadoDetalhamento(chamado));
    }
    @GetMapping("/pendentes")
    public ResponseEntity<List<ChamadoDetalhamento>> listarChamadosPendentes() {
        List<ChamadoDetalhamento> chamados = chamadoService.listarChamadosPendentes()
                .stream()
                .map(ChamadoDetalhamento::new)
                .toList();
        return ResponseEntity.ok(chamados);
    }
    @PutMapping("/atender/{id}")
    public ResponseEntity<ChamadoDetalhamento> atenderChamado(@PathVariable Long id) {
        Chamado chamado = chamadoService.atenderChamado(id);
        return ResponseEntity.ok(new ChamadoDetalhamento(chamado));
    }

    @PutMapping("/cancelar/{id}")
    public ResponseEntity<ChamadoDetalhamento> cancelarChamado(@PathVariable Long id) {
        Chamado chamado = chamadoService.cancelarChamado(id);
        return ResponseEntity.ok(new ChamadoDetalhamento(chamado));
    }
}