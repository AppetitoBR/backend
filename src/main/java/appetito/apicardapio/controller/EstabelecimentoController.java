package appetito.apicardapio.controller;

import appetito.apicardapio.dto.EstabelecimentoCadastro;
import appetito.apicardapio.dto.EstabelecimentoDetalhamento;
import appetito.apicardapio.service.EstabelecimentoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/estabelecimentos")
public class EstabelecimentoController {

    @Autowired
    private EstabelecimentoService estabelecimentoService;

    // Cadastrar um estabelecimento
    @PostMapping
    public ResponseEntity<EstabelecimentoDetalhamento> cadastrarEstabelecimento(
            @Valid @RequestBody EstabelecimentoCadastro dadosEstabelecimento,
            UriComponentsBuilder uriBuilder) {
        EstabelecimentoDetalhamento estabelecimento = estabelecimentoService.cadastrarEstabelecimento(dadosEstabelecimento);
        var uri = uriBuilder.path("/estabelecimentos/{id}").buildAndExpand(estabelecimento.estabelecimento_id()).toUri();
        return ResponseEntity.created(uri).body(estabelecimento);
    }

    // Buscar um estabelecimento por ID
    @GetMapping("/{id}")
    public ResponseEntity<EstabelecimentoDetalhamento> buscarEstabelecimentoPorId(@PathVariable Long id) {
        EstabelecimentoDetalhamento estabelecimento = estabelecimentoService.buscarEstabelecimentoPorId(id);
        return ResponseEntity.ok(estabelecimento);
    }

    // Listar todos os estabelecimentos
    @GetMapping
    public ResponseEntity<List<EstabelecimentoDetalhamento>> listarEstabelecimentos() {
        List<EstabelecimentoDetalhamento> estabelecimentos = estabelecimentoService.listarEstabelecimentos();
        return ResponseEntity.ok(estabelecimentos);
    }

    // Atualizar um estabelecimento
    @PutMapping("/{id}")
    public ResponseEntity<EstabelecimentoDetalhamento> atualizarEstabelecimento(
            @PathVariable Long id,
            @Valid @RequestBody EstabelecimentoCadastro dadosAtualizados) {
        EstabelecimentoDetalhamento estabelecimentoAtualizado = estabelecimentoService.atualizarEstabelecimento(id, dadosAtualizados);
        return ResponseEntity.ok(estabelecimentoAtualizado);
    }

    // Deletar um estabelecimento (soft delete - apenas desativando)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativarEstabelecimento(@PathVariable Long id) {
        estabelecimentoService.desativarEstabelecimento(id);
        return ResponseEntity.noContent().build();
    }
}
