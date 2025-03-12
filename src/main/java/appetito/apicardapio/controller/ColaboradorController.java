package appetito.apicardapio.controller;

import appetito.apicardapio.dto.ColaboradorCadastro;
import appetito.apicardapio.dto.ColaboradorDetalhamento;
import appetito.apicardapio.service.ColaboradorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/colaboradores")
public class ColaboradorController {

    @Autowired
    private ColaboradorService colaboradorService;

    // Cadastrar um colaborador
    @PostMapping
    public ResponseEntity<ColaboradorDetalhamento> cadastrarColaborador(
            @RequestBody @Valid ColaboradorCadastro dadosColaborador,
            UriComponentsBuilder uriBuilder) {
        ColaboradorDetalhamento colaboradorDetalhamento = colaboradorService.cadastrarColaborador(dadosColaborador);
        var uri = uriBuilder.path("/colaboradores/{id}").buildAndExpand(colaboradorDetalhamento.colaborador_id()).toUri();
        return ResponseEntity.created(uri).body(colaboradorDetalhamento);
    }

    // Buscar um colaborador por ID
    @GetMapping("/{id}")
    public ResponseEntity<ColaboradorDetalhamento> buscarColaboradorPorId(@PathVariable Long id) {
        ColaboradorDetalhamento colaboradorDetalhamento = colaboradorService.buscarColaboradorPorId(id);
        return ResponseEntity.ok(colaboradorDetalhamento);
    }

    // Listar colaboradores por estabelecimento
    @GetMapping("/estabelecimento/{estabelecimentoId}")
    public ResponseEntity<List<ColaboradorDetalhamento>> listarColaboradoresPorEstabelecimento(
            @PathVariable Long estabelecimentoId) {
        List<ColaboradorDetalhamento> colaboradores = colaboradorService.listarColaboradoresPorEstabelecimento(estabelecimentoId);
        return ResponseEntity.ok(colaboradores);
    }
}
