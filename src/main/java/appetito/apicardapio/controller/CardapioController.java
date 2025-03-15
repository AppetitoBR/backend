package appetito.apicardapio.controller;

import appetito.apicardapio.dto.CardapioCadastro;
import appetito.apicardapio.dto.CardapioDetalhamento;
import appetito.apicardapio.service.CardapioService;
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

    // Cadastrar um cardápio
    @PostMapping
    public ResponseEntity<CardapioDetalhamento> cadastrarCardapio(
            @RequestBody @Valid CardapioCadastro dadosCardapio,
            UriComponentsBuilder uriBuilder) {
        CardapioDetalhamento cardapioDetalhamento = cardapioService.cadastrarCardapio(dadosCardapio);
        var uri = uriBuilder.path("/cardapios/{id}").buildAndExpand(cardapioDetalhamento.cardapio_id()).toUri();
        return ResponseEntity.created(uri).body(cardapioDetalhamento);
    }

    // Buscar um cardápio por ID
    @GetMapping("/{id}")
    public ResponseEntity<CardapioDetalhamento> buscarCardapioPorId(@PathVariable Long id) {
        CardapioDetalhamento cardapioDetalhamento = cardapioService.buscarCardapioPorId(id);
        return ResponseEntity.ok(cardapioDetalhamento);
    }
    // Buscar cardapio por Estabelecimento
    @GetMapping("/estabelecimento/{estabelecimentoId}")
    public ResponseEntity<List<CardapioDetalhamento>> listarCardapiosPorEstabelecimento(@PathVariable Long estabelecimentoId) {
        List<CardapioDetalhamento> cardapios = cardapioService.listarCardapiosPorEstabelecimento(estabelecimentoId);
        return ResponseEntity.ok(cardapios);
    }

    // Deletar Estabelecimento
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerCardapio(@PathVariable Long id) {
        cardapioService.deletarCardapio(id);
        return ResponseEntity.noContent().build();
    }
}