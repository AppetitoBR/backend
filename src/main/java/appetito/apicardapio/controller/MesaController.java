package appetito.apicardapio.controller;

import appetito.apicardapio.dto.cadastro.MesaCadastro;
import appetito.apicardapio.dto.detalhamento.MesaDetalhamento;
import appetito.apicardapio.service.MesaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/mesas")
public class MesaController {

    @Autowired
    private MesaService mesaService;


    @PostMapping
    public ResponseEntity<MesaDetalhamento> cadastrarMesa(
            @RequestBody @Valid MesaCadastro dadosMesa,
            UriComponentsBuilder uriBuilder) {
        MesaDetalhamento mesaDetalhamento = mesaService.cadastrarMesa(dadosMesa);
        var uri = uriBuilder.path("/mesas/{id}").buildAndExpand(mesaDetalhamento.mesa_id()).toUri();
        return ResponseEntity.created(uri).body(mesaDetalhamento);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MesaDetalhamento> atualizarMesa(
            @PathVariable Long id,
            @RequestBody @Valid MesaCadastro dadosMesa) {
        MesaDetalhamento mesaDetalhamento = mesaService.atualizarMesa(id, dadosMesa);
        return ResponseEntity.ok(mesaDetalhamento);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirMesa(@PathVariable Long id) {
        mesaService.excluirMesa(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<MesaDetalhamento>> listarMesas() {
        List<MesaDetalhamento> mesas = mesaService.listarMesas();
        return ResponseEntity.ok(mesas);
    }
}