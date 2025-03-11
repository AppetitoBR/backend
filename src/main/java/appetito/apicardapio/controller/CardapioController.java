package appetito.apicardapio.controller;

import appetito.apicardapio.entity.Cardapio;
import appetito.apicardapio.dto.CardapioCadastro;
import appetito.apicardapio.dto.DadosDetalhamentoCardapio;
import appetito.apicardapio.repository.CardapioRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/cardapio")
public class CardapioController {
    @Autowired
    private CardapioRepository cardapioRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<DadosDetalhamentoCardapio> cadastrarCardapio(@RequestBody @Valid CardapioCadastro dadosCardapio, UriComponentsBuilder uriBuilder ) {
        var cardapio = new Cardapio(dadosCardapio);
        cardapioRepository.save(cardapio);
        var uri = uriBuilder.path("/cardapio/{id}").buildAndExpand(cardapio.getCardapio_id()).toUri();
        return ResponseEntity.created(uri).body(new DadosDetalhamentoCardapio(cardapio));
    }

    @GetMapping
    public ResponseEntity<List<Cardapio>> listarCardapio(
            @RequestParam(required = false) Boolean ativo,
            @RequestParam(required = false) String restricao) {

        List<Cardapio> itens;

        if (ativo != null) {
            itens = cardapioRepository.findByAtivo(ativo);
        } else if (restricao != null) {
            itens = cardapioRepository.findByRestricoesAlimentaresContaining(restricao);
        } else {
            itens = cardapioRepository.findAll();
        }

        return ResponseEntity.ok(itens);
    }


}
