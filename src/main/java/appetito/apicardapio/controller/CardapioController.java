package appetito.apicardapio.controller;
import appetito.apicardapio.dto.cadastro.CardapioCadastro;
import appetito.apicardapio.dto.detalhamento.CardapioDetalhamento;
import appetito.apicardapio.entity.Cardapio;
import appetito.apicardapio.entity.Estabelecimento;
import appetito.apicardapio.entity.UsuarioDashboard;
import appetito.apicardapio.entity.UsuarioEstabelecimento;
import appetito.apicardapio.exception.ResourceNotFoundException;
import appetito.apicardapio.repository.CardapioRepository;
import appetito.apicardapio.repository.UsuarioEstabelecimentoRepository;
import appetito.apicardapio.service.CardapioService;
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

    @Autowired
    private CardapioService cardapioService;

    @Autowired
    private CardapioRepository cardapioRepository;
    private final UsuarioEstabelecimentoRepository usuarioEstabelecimentoRepository;

    public CardapioController(UsuarioEstabelecimentoRepository usuarioEstabelecimentoRepository) {
        this.usuarioEstabelecimentoRepository = usuarioEstabelecimentoRepository;
    }

    // DASHBOARD
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE')")
    @Transactional
    public ResponseEntity<CardapioDetalhamento> cadastrarCardapioDoEstabelecimento(
            @RequestBody @Valid CardapioCadastro dadosCardapio,
            UriComponentsBuilder uriBuilder) throws AccessDeniedException {

        UsuarioDashboard usuario = (UsuarioDashboard) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Estabelecimento estabelecimento = usuarioEstabelecimentoRepository
                .findAllByUsuario(usuario)
                .stream()
                .map(UsuarioEstabelecimento::getEstabelecimento)
                .findFirst()
                .orElseThrow(() -> new AccessDeniedException("Você não está vinculado a um estabelecimento."));

        Cardapio cardapio = new Cardapio(dadosCardapio);
        cardapio.setEstabelecimento(estabelecimento);
        cardapioRepository.save(cardapio);

        var uri = uriBuilder.path("/cardapio/{id}").buildAndExpand(cardapio.getId()).toUri();
        return ResponseEntity.created(uri).body(new CardapioDetalhamento(cardapio));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE')")
    @DeleteMapping("/cardapio/{id}")
    public ResponseEntity<Void> deletarCardapio(@PathVariable Long id, @AuthenticationPrincipal UsuarioDashboard usuario) throws AccessDeniedException {
        cardapioService.deletarCardapio(id, usuario);
        return ResponseEntity.noContent().build();
    }
}