package appetito.apicardapio.controller;
import appetito.apicardapio.dto.cadastro.CardapioCadastro;
import appetito.apicardapio.dto.detalhamento.CardapioDetalhamento;
import appetito.apicardapio.entity.Cardapio;
import appetito.apicardapio.exception.ResourceNotFoundException;
import appetito.apicardapio.repository.CardapioRepository;
import appetito.apicardapio.service.CardapioService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    // Somente ADMIN e GERENTE podem cadastrar um cardápio
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE')")
    @Transactional
    public ResponseEntity<CardapioDetalhamento> cadastrarCardapio(
            @RequestBody @Valid CardapioCadastro dadosCardapio,
            UriComponentsBuilder uriBuilder
    ) {
        var cardapio = new Cardapio(dadosCardapio);
        cardapioRepository.save(cardapio);
        var uri = uriBuilder.path("/cardapio/{id}").buildAndExpand(cardapio.getId()).toUri();
        return ResponseEntity.created(uri).body(new CardapioDetalhamento(cardapio));
    }

    // Apenas ADMIN pode listar todos os cardápios
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Transactional
    public ResponseEntity<List<CardapioDados>> listarCardapios() {
        List<Cardapio> cardapios = cardapioRepository.findAll();
        if (cardapios.isEmpty()) {
            throw new ResourceNotFoundException("Nenhum Cardapio encontrado");
        }
        var lista = cardapios.stream().map(CardapioDados::new).toList();
        return ResponseEntity.ok(lista);
    }

    // ADMIN e GERENTE podem buscar um cardápio específico
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE')")
    @Transactional
    public ResponseEntity<CardapioDetalhamento> buscarCardapioPorId(@PathVariable Long id) {
        CardapioDetalhamento cardapioDetalhamento = cardapioService.buscarCardapioPorId(id);
        return ResponseEntity.ok(cardapioDetalhamento);
    }

    // Todos podem listar cardápios de um estabelecimento
    @GetMapping("/estabelecimento/{estabelecimentoId}")
    @Transactional
    public ResponseEntity<List<CardapioDetalhamento>> listarCardapiosPorEstabelecimento(@PathVariable Long estabelecimentoId) {
        List<CardapioDetalhamento> cardapios = cardapioService.listarCardapiosPorEstabelecimento(estabelecimentoId);
        return ResponseEntity.ok(cardapios);
    }
    // Apenas ADMIN pode deletar qualquer cardápio
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Transactional
    public ResponseEntity<Void> removerCardapio(@PathVariable Long id) {
        cardapioService.deletarCardapio(id);
        return ResponseEntity.noContent().build();
    }

    // Apenas GERENTE pode deletar um cardápio do seu próprio estabelecimento
    @Transactional
    @DeleteMapping("/estabelecimento/{estabelecimentoId}/cardapio/{cardapioId}")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<Void> deletarCardapiodoEstabelecimento(
            @PathVariable Long estabelecimentoId,
            @PathVariable Long cardapioId
    ) {
        boolean deletado = cardapioService.deletarSePertencerAoEstabelecimento(cardapioId, estabelecimentoId);
        return deletado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}