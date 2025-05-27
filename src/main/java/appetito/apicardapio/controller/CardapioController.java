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

/**
 * Controller responsável por gerenciar os endpoints relacionados a cardápios no painel do estabelecimento (Dashboard).
 * Fornece operações para cadastrar e excluir cardápios.
 */
@RestController
@RequestMapping("/cardapio")
public class CardapioController {

    private final CardapioService cardapioService;

    /**
     * Construtor da controller com injeção do serviço de cardápio.
     *
     * @param cardapioService serviço que contém as regras de negócio para cardápios
     */
    public CardapioController(CardapioService cardapioService) {
        this.cardapioService = cardapioService;
    }

    /**
     * Endpoint responsável por cadastrar um novo cardápio para um estabelecimento.
     * Apenas usuários autorizados (com permissão de gerenciamento) podem executar essa operação.
     *
     * @param estabelecimentoId ID do estabelecimento ao qual o cardápio será vinculado
     * @param dadosCardapio     dados enviados no corpo da requisição para criação do cardápio
     * @param uriBuilder        utilitário para construção da URI de resposta
     * @param usuario           usuário autenticado que está realizando a operação
     * @return ResponseEntity contendo os dados detalhados do cardápio criado e o status HTTP 201
     */
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

    /**
     * Endpoint responsável por excluir um cardápio de um estabelecimento específico.
     * Apenas usuários com permissão de gerenciamento podem realizar a exclusão.
     *
     * @param estabelecimentoId ID do estabelecimento dono do cardápio
     * @param id                ID do cardápio a ser removido
     * @param usuario           usuário autenticado que está solicitando a exclusão
     * @return ResponseEntity com status HTTP 204 (No Content) em caso de sucesso
     * @throws AccessDeniedException se o cardápio não pertencer ao estabelecimento ou o usuário não tiver permissão
     */
    @DeleteMapping("/{estabelecimentoId}/cardapio/{id}")
    @PreAuthorize("@preAuthorizeService.podeGerenciarEstabelecimento(#estabelecimentoId, principal)")
    public ResponseEntity<Void> deletarCardapio(@PathVariable Long estabelecimentoId, @PathVariable Long id, @AuthenticationPrincipal UsuarioDashboard usuario) throws AccessDeniedException {
        cardapioService.deletarCardapioDoEstabelecimento(estabelecimentoId, id, usuario);
        return ResponseEntity.noContent().build();
    }
}