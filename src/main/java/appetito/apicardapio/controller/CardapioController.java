package appetito.apicardapio.controller;

import appetito.apicardapio.dto.cadastro.CardapioCadastro;
import appetito.apicardapio.dto.detalhamento.CardapioDetalhamento;
import appetito.apicardapio.entity.Cardapio;
import appetito.apicardapio.repository.CardapioRepository;
import appetito.apicardapio.service.CardapioService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/cardapios")
public class CardapioController {

    @Autowired
    private CardapioService cardapioService;
    @Autowired
    private CardapioRepository cardapioRepository;



    @PostMapping
    @Transactional
    public ResponseEntity<CardapioDetalhamento> cadastrarCardapio(@RequestBody @Valid CardapioCadastro dadosCardapio, UriComponentsBuilder uriBuilder1) {
        var cardapio = new Cardapio(dadosCardapio);
        cardapioRepository.save(cardapio);
        var uri = uriBuilder1.path("/cardapio/{id}").buildAndExpand(cardapio.getId()).toUri();
        return ResponseEntity.created(uri).body(new CardapioDetalhamento(cardapio));
    }
    @GetMapping("/{id}")
    public ResponseEntity<CardapioDetalhamento> buscarCardapioPorId(@PathVariable Long id) {
        CardapioDetalhamento cardapioDetalhamento = cardapioService.buscarCardapioPorId(id);
        return ResponseEntity.ok(cardapioDetalhamento);
    }
    @GetMapping("/estabelecimento/{estabelecimentoId}")
    public ResponseEntity<List<CardapioDetalhamento>> listarCardapiosPorEstabelecimento(@PathVariable Long estabelecimentoId) {
        List<CardapioDetalhamento> cardapios = cardapioService.listarCardapiosPorEstabelecimento(estabelecimentoId);
        return ResponseEntity.ok(cardapios);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerCardapio(@PathVariable Long id) {
        cardapioService.deletarCardapio(id);
        return ResponseEntity.noContent().build();
    }
}