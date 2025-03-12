package appetito.apicardapio.controller;

import appetito.apicardapio.dto.EstabelecimentoCadastro;
import appetito.apicardapio.dto.EstabelecimentoDetalhamento;
import appetito.apicardapio.entity.Estabelecimento;
import appetito.apicardapio.repository.EstabelecimentoRepository;
import appetito.apicardapio.service.EstabelecimentoService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/estabelecimentos")
public class EstabelecimentoController {

    @Autowired
    private EstabelecimentoService estabelecimentoService;

    private final EstabelecimentoRepository estabelecimentoRepository;

    public EstabelecimentoController(EstabelecimentoRepository estabelecimentoRepository) {
        this.estabelecimentoRepository = estabelecimentoRepository;
    }

    @PostMapping
    public ResponseEntity<EstabelecimentoDetalhamento> cadastrarEstabelecimento(
            @RequestBody @Valid EstabelecimentoCadastro dadosEstabelecimento,
            UriComponentsBuilder uriBuilder) {
        EstabelecimentoDetalhamento estabelecimentoDetalhamento = estabelecimentoService.cadastrarEstabelecimento(dadosEstabelecimento);
        var uri = uriBuilder.path("/estabelecimentos/{id}").buildAndExpand(estabelecimentoDetalhamento.estabelecimento_id()).toUri();
        return ResponseEntity.created(uri).body(estabelecimentoDetalhamento);
    }
    @GetMapping("/{id}")
    public Estabelecimento obterEstabelecimento(@PathVariable Long id) {
        return estabelecimentoRepository.findById(id).orElse(null);
    }

}
