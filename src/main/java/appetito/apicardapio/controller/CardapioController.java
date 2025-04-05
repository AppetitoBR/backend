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

    // APP
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

    // VOU MUDAR, DASHBOARD
    // ADMIN e GERENTE podem buscar um cardápio específico
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE')")
    @Transactional
    public ResponseEntity<CardapioDetalhamento> buscarCardapioPorId(@PathVariable Long id) {
        CardapioDetalhamento cardapioDetalhamento = cardapioService.buscarCardapioPorId(id);
        return ResponseEntity.ok(cardapioDetalhamento);
    } // ACHO DESNECESSARIO, MAS PODEMOS VER UMA FORMA QUE PEGUE O ID DO ESTABELECIMENTO E DPS EU FAÇO UM FINDBYID

    // Todos podem listar cardápios de um estabelecimento
    // APP
    @GetMapping("/estabelecimento/{estabelecimentoId}")
    @Transactional
    public ResponseEntity<List<CardapioDetalhamento>> listarCardapiosPorEstabelecimento(@PathVariable Estabelecimento estabelecimento) {
        List<CardapioDetalhamento> cardapios = cardapioService.listarCardapiosPorEstabelecimento(estabelecimento);
        return ResponseEntity.ok(cardapios);
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