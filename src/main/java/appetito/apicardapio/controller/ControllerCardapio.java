package appetito.apicardapio.controller;

import appetito.apicardapio.entity.Cardapio;
import appetito.apicardapio.posts.CardapioCadastro;
import appetito.apicardapio.puts.DadosDetalhamentoCardapio;
import appetito.apicardapio.repository.CardapioRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/cardapio")
public class ControllerCardapio {
    @Autowired
    private CardapioRepository cardapioRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<DadosDetalhamentoCardapio> cadastrarCardapio(@RequestBody @Valid CardapioCadastro dadosCardapio, UriComponentsBuilder uriBuilder ) {
        var cardapio = new Cardapio(dadosCardapio);
        cardapioRepository.save(cardapio);
        var uri = uriBuilder.path("/cardapio/{id}").buildAndExpand(cardapio.getId()).toUri();
        return ResponseEntity.created(uri).body(new DadosDetalhamentoCardapio(cardapio));
    }

}
