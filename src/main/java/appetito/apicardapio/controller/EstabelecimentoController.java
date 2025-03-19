package appetito.apicardapio.controller;

import appetito.apicardapio.dto.EstabelecimentoCadastro;
import appetito.apicardapio.dto.EstabelecimentoDetalhamento;
import appetito.apicardapio.dto.forGet.EstabelecimentoDados;
import appetito.apicardapio.entity.Estabelecimento;
import appetito.apicardapio.exception.ResourceNotFoundException;
import appetito.apicardapio.repository.EstabelecimentoRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/estabelecimentos")
public class EstabelecimentoController {
    private final EstabelecimentoRepository estabelecimentoRepository;

    public EstabelecimentoController(EstabelecimentoRepository estabelecimentoRepository) {
        this.estabelecimentoRepository = estabelecimentoRepository;
    }

    @PostMapping
    public ResponseEntity<EstabelecimentoDetalhamento> cadastrarEstabelecimento(@RequestBody @Valid EstabelecimentoCadastro dadosEstabelecimento, UriComponentsBuilder uriE){
            var estabelecimento = new Estabelecimento(dadosEstabelecimento);
        estabelecimentoRepository.save(new Estabelecimento(dadosEstabelecimento));
    var uri = uriE.path("/endereco/{id}").buildAndExpand(estabelecimento.getId()).toUri();
        return ResponseEntity.created(uri).body(new EstabelecimentoDetalhamento(estabelecimento));
    }
    @GetMapping("/{id}")
    public ResponseEntity<EstabelecimentoDetalhamento> obterEstabelecimento(@PathVariable Long id) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estabelecimento não encontrado"));
        return ResponseEntity.ok(new EstabelecimentoDetalhamento(estabelecimento));
    }

    @GetMapping
    @Transactional
    public ResponseEntity<List<EstabelecimentoDados>> listarEstabelecimentos() {

        List<Estabelecimento> estabelecimentos = estabelecimentoRepository.findAll();
        if (estabelecimentos.isEmpty()) {
            throw new ResourceNotFoundException("Nenhum estabelecimento encontrado");
        }
        var lista =  estabelecimentoRepository.findAll().stream().map(EstabelecimentoDados::new).toList();
        return ResponseEntity.ok(lista);
    }

}
