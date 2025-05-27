package appetito.apicardapio.controller;

import appetito.apicardapio.dto.cadastro.CardapioCadastro;
import appetito.apicardapio.dto.detalhamento.CardapioDetalhamento;
import appetito.apicardapio.entity.Cardapio;
import appetito.apicardapio.entity.UsuarioDashboard;
import appetito.apicardapio.service.CardapioService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/cardapio")
public class CardapioController {
    private final CardapioService cardapioService;

    public CardapioController(CardapioService cardapioService) {
        this.cardapioService = cardapioService;
    }

    // DASHBOARD
    @PostMapping("/{estabelecimentoId}")
    @PreAuthorize("@preAuthorizeService.podeGerenciarEstabelecimento(#estabelecimentoId, authentication.principal)")
    @Transactional
    public ResponseEntity<CardapioDetalhamento> cadastrarCardapioDoEstabelecimento(
            @PathVariable Long estabelecimentoId,
            @RequestBody @Valid CardapioCadastro dadosCardapio,
            UriComponentsBuilder uriBuilder,
            @AuthenticationPrincipal UsuarioDashboard usuario) {

        Cardapio cardapio = cardapioService.cadastrarCardapio(estabelecimentoId, dadosCardapio, usuario);

        var uri = uriBuilder.path("/cardapio/{id}").buildAndExpand(cardapio.getId()).toUri();
        return ResponseEntity.created(uri).body(new CardapioDetalhamento(cardapio));
    }

    @DeleteMapping("/{estabelecimentoId}/cardapio/{id}")
    @PreAuthorize("@preAuthorizeService.podeGerenciarEstabelecimento(#estabelecimentoId, principal)")
    public ResponseEntity<Void> deletarCardapio(@PathVariable Long estabelecimentoId, @PathVariable Long id, @AuthenticationPrincipal UsuarioDashboard usuario) throws AccessDeniedException {
        cardapioService.deletarCardapioDoEstabelecimento(estabelecimentoId, id, usuario);
        return ResponseEntity.noContent().build();
    }
}