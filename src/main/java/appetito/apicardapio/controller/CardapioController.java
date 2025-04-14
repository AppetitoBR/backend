package appetito.apicardapio.controller;
import appetito.apicardapio.dto.cadastro.CardapioCadastro;
import appetito.apicardapio.dto.detalhamento.CardapioDetalhamento;
import appetito.apicardapio.entity.Cardapio;
import appetito.apicardapio.entity.Estabelecimento;
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
@RequestMapping("/cardapio")
public class CardapioController {

    @Autowired
    private CardapioService cardapioService;

    @Autowired
    private CardapioRepository cardapioRepository;

    // DASHBOARD
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE')")
    @Transactional
    public ResponseEntity<CardapioDetalhamento> cadastrarCardapioDoEstabelecimento(@RequestBody @Valid CardapioCadastro dadosCardapio, UriComponentsBuilder uriBuilder) {
        var cardapio = new Cardapio(dadosCardapio);
        cardapioRepository.save(cardapio);
        var uri = uriBuilder.path("/cardapio/{id}").buildAndExpand(cardapio.getId()).toUri();
        return ResponseEntity.created(uri).body(new CardapioDetalhamento(cardapio));
    }

    // Apenas GERENTE pode deletar um cardápio do seu próprio estabelecimento
    // DASHBOARD
    @Transactional
    @DeleteMapping("/estabelecimento/{estabelecimentoId}/cardapio/{cardapioId}")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<Void> deletarCardapiodoEstabelecimento(
            @PathVariable Estabelecimento estabelecimento,
            @PathVariable Long cardapioId
    ) {
        boolean deletado = cardapioService.deletarSePertencerAoEstabelecimento(cardapioId, estabelecimento);
        return deletado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}