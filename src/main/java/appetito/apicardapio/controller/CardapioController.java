package appetito.apicardapio.controller;

import appetito.apicardapio.dto.CardapioCadastro;
import appetito.apicardapio.dto.CardapioDetalhamento;
import appetito.apicardapio.dto.UsuarioCadastro;
import appetito.apicardapio.dto.UsuarioDetalhamento;
import appetito.apicardapio.entity.Cardapio;
import appetito.apicardapio.entity.Colaborador;
import appetito.apicardapio.entity.Estabelecimento;
import appetito.apicardapio.entity.Usuario;
import appetito.apicardapio.exception.ResourceNotFoundException;
import appetito.apicardapio.repository.CardapioRepository;
import appetito.apicardapio.repository.ColaboradorRepository;
import appetito.apicardapio.repository.EstabelecimentoRepository;
import appetito.apicardapio.service.CardapioService;
import appetito.apicardapio.service.ColaboradorService;
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

    @Autowired
    private EstabelecimentoRepository estabelecimentoRepository;
    @Autowired
    private ColaboradorRepository colaboradorRepository;

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