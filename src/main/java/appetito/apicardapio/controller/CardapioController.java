package appetito.apicardapio.controller;
import appetito.apicardapio.dto.cadastro.CardapioCadastro;
import appetito.apicardapio.dto.detalhamento.CardapioDetalhamento;
import appetito.apicardapio.entity.Cardapio;
import appetito.apicardapio.entity.Estabelecimento;
import appetito.apicardapio.entity.UsuarioDashboard;
import appetito.apicardapio.entity.UsuarioEstabelecimento;
import appetito.apicardapio.exception.ResourceNotFoundException;
import appetito.apicardapio.repository.CardapioRepository;
import appetito.apicardapio.repository.EstabelecimentoRepository;
import appetito.apicardapio.repository.UsuarioEstabelecimentoRepository;
import appetito.apicardapio.service.CardapioService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import appetito.apicardapio.dto.GetAll.CardapioDados;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/cardapio")
public class CardapioController {
    private final CardapioService cardapioService;
    private final CardapioRepository cardapioRepository;
    private final EstabelecimentoRepository estabelecimentoRepository;

    public CardapioController(CardapioService cardapioService, CardapioRepository cardapioRepository, EstabelecimentoRepository estabelecimentoRepository) {
        this.cardapioService = cardapioService;
        this.cardapioRepository = cardapioRepository;
        this.estabelecimentoRepository = estabelecimentoRepository;
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