package appetito.apicardapio.controller;

import appetito.apicardapio.dto.GetAll.EstabelecimentoDados;
import appetito.apicardapio.dto.cadastro.CardapioCadastro;
import appetito.apicardapio.dto.detalhamento.CardapioDetalhamento;
import appetito.apicardapio.entity.Cardapio;
import appetito.apicardapio.entity.Estabelecimento;
import appetito.apicardapio.exception.ResourceNotFoundException;
import appetito.apicardapio.repository.CardapioRepository;
import appetito.apicardapio.service.CardapioService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import appetito.apicardapio.dto.GetAll.CardapioDados;
import java.util.List;

@RestController
@RequestMapping("/cardapios")
public class CardapioController {

    @Autowired
    private CardapioService cardapioService;
    @Autowired
    private CardapioRepository cardapioRepository;



    //funcao admin e gerente
    @PostMapping
    @Transactional
    public ResponseEntity<CardapioDetalhamento> cadastrarCardapio(@RequestBody @Valid CardapioCadastro dadosCardapio, UriComponentsBuilder uriBuilder1) {
        var cardapio = new Cardapio(dadosCardapio);
        cardapioRepository.save(cardapio);
        var uri = uriBuilder1.path("/cardapio/{id}").buildAndExpand(cardapio.getId()).toUri();
        return ResponseEntity.created(uri).body(new CardapioDetalhamento(cardapio));
    }
    // Admin e irei ver como posso aplicar para fazer um painel, deixar como admin mesmo
    @GetMapping("/{id}")
    public ResponseEntity<CardapioDetalhamento> buscarCardapioPorId(@PathVariable Long id) {
        CardapioDetalhamento cardapioDetalhamento = cardapioService.buscarCardapioPorId(id);
        return ResponseEntity.ok(cardapioDetalhamento);
    }
    //Permi all com jwt
    @GetMapping("/estabelecimento/{estabelecimentoId}")
    public ResponseEntity<List<CardapioDetalhamento>> listarCardapiosPorEstabelecimento(@PathVariable Long estabelecimentoId) {
        List<CardapioDetalhamento> cardapios = cardapioService.listarCardapiosPorEstabelecimento(estabelecimentoId);
        return ResponseEntity.ok(cardapios);
    }

    // listar todos os cardapios,irei ver como posso aplicar para fazer um painel, funcao ADMIN
    @GetMapping
    @Transactional
    public ResponseEntity<List<CardapioDados>> listarCardapios() {

        List<Cardapio> cardapios = cardapioRepository.findAll();
        if (cardapios.isEmpty()) {
            throw new ResourceNotFoundException("Nenhum Cardapio encontrado");
        }
        var lista =  cardapioRepository.findAll().stream().map(CardapioDados::new).toList();
        return ResponseEntity.ok(lista);
    }
    // funcao admin, irei fazer para caso a pessoa for gerente e etc
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerCardapio(@PathVariable Long id) {
        cardapioService.deletarCardapio(id);
        return ResponseEntity.noContent().build();
    }
    // funcao gerente
    @DeleteMapping("/estabelecimento/{estabelecimentoId}/cardapio/{cardapioId}")
    public ResponseEntity<Void> deletarCardapio(
            @PathVariable Long estabelecimentoId,
            @PathVariable Long cardapioId) {

        boolean deletado = cardapioService.deletarSePertencerAoEstabelecimento(cardapioId, estabelecimentoId);

        if (deletado) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}