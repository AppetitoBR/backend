package appetito.apicardapio.controller;

import appetito.apicardapio.dto.cadastro.EstabelecimentoCadastro;
import appetito.apicardapio.dto.detalhamento.EstabelecimentoDetalhamento;
import appetito.apicardapio.dto.GetAll.EstabelecimentoDados;
import appetito.apicardapio.entity.Estabelecimento;
import appetito.apicardapio.exception.ResourceNotFoundException;
import appetito.apicardapio.repository.EstabelecimentoRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CLIENTE')")
    @PostMapping
    @Transactional
    public ResponseEntity<EstabelecimentoDetalhamento> cadastrarEstabelecimento(@RequestBody @Valid EstabelecimentoCadastro dadosEstabelecimento, UriComponentsBuilder uriE){
            var estabelecimento = new Estabelecimento(dadosEstabelecimento);
        estabelecimentoRepository.save(new Estabelecimento(dadosEstabelecimento));
    var uri = uriE.path("/estabelecimento/{id}").buildAndExpand(estabelecimento.getEstabelecimento_id()).toUri();
        return ResponseEntity.created(uri).body(new EstabelecimentoDetalhamento(estabelecimento));
    }

    @GetMapping("/{id}")
    @Transactional
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
    @DeleteMapping("/{id}")
    @Transactional
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> deletarEstabelecimento(@PathVariable Long id) {
        if(estabelecimentoRepository.existsById(id)) {
            estabelecimentoRepository.deleteById(id);
        }
       return ResponseEntity.noContent().build();
    }

}
