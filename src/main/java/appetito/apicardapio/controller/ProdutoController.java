package appetito.apicardapio.controller;

import appetito.apicardapio.dto.cadastro.ProdutoCadastro;
import appetito.apicardapio.dto.detalhamento.ProdutoDetalhamento;
import appetito.apicardapio.dto.put.ProdutoAtualizacao;
import appetito.apicardapio.entity.Produto;
import appetito.apicardapio.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import java.io.IOException;
import java.util.List;

/**
 * Controlador responsável por gerenciar as operações relacionadas aos produtos de um estabelecimento.
 */
@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    /**
     * Construtor da classe ProdutoController.
     *
     * @param produtoService serviço responsável pela lógica de negócios dos produtos
     */
    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    /**
     * Cadastra um novo produto vinculado a um cardápio de um estabelecimento específico.
     *
     * @param estabelecimentoId ID do estabelecimento ao qual o produto será vinculado
     * @param dadosProduto       dados do produto a ser cadastrado
     * @param uriP               builder para criação da URI do recurso criado
     * @return ResponseEntity contendo os detalhes do produto criado e a URI do recurso
     */
    @PostMapping("/{estabelecimentoId}")
    @PreAuthorize("@preAuthorizeService.podeGerenciarEstabelecimento(#estabelecimentoId, authentication.principal)")
    @Transactional
    public ResponseEntity<ProdutoDetalhamento> cadastrarProduto(
            @PathVariable Long estabelecimentoId,
            @RequestBody @Valid ProdutoCadastro dadosProduto,
            UriComponentsBuilder uriP) {

        Produto produto = produtoService.cadastrarProduto(estabelecimentoId, dadosProduto);
        var uri = uriP.path("/produto/{id}").buildAndExpand(produto.getProduto_id()).toUri();
        return ResponseEntity.created(uri).body(new ProdutoDetalhamento(produto));
    }

    /**
     * Atualiza a imagem de um produto.
     *
     * @param id     ID do produto cuja imagem será atualizada
     * @param imagem arquivo de imagem a ser associado ao produto
     * @return ResponseEntity contendo os detalhes do produto atualizado
     * @throws IOException caso ocorra erro ao processar a imagem
     */
    @PutMapping(value = "/{id}/imagem", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Atualiza a imagem do produto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Imagem atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<ProdutoDetalhamento> setImagemProduto(
            @PathVariable Long id,
            @Parameter(description = "Imagem do produto", required = true,
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestParam("imagem") MultipartFile imagem
    ) throws IOException {

        Produto produto = produtoService.setImagemProduto(id, imagem);
        ProdutoDetalhamento detalhamento = new ProdutoDetalhamento(produto);

        return ResponseEntity.ok(detalhamento);
    }

    /**
     * Recupera a imagem associada a um produto.
     *
     * @param id ID do produto
     * @return ResponseEntity contendo o array de bytes da imagem ou 404 se não existir
     */
    @GetMapping("/{id}/imagem")
    public ResponseEntity<byte[]> getImagemProduto(@PathVariable Long id) {
        Produto produto = produtoService.getProdutoById(id);

        if (produto.getImagens() != null) {
            return ResponseEntity.ok()
                    .header("Content-Type", "image/jpeg")
                    .body(produto.getImagens());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Atualiza os dados de um produto de um estabelecimento específico.
     *
     * @param estabelecimentoId ID do estabelecimento
     * @param produtoId         ID do produto a ser atualizado
     * @param dadosProduto      dados atualizados do produto
     * @return ResponseEntity contendo os detalhes do produto atualizado
     */
    @PutMapping("/{estabelecimentoId}/{produtoId}")
    @PreAuthorize("@preAuthorizeService.podeGerenciarEstabelecimento(#estabelecimentoId, authentication.principal)")
    @Transactional
    public ResponseEntity<ProdutoDetalhamento> atualizarProduto(@PathVariable Long estabelecimentoId, @PathVariable Long produtoId, @RequestBody @Valid ProdutoAtualizacao dadosProduto) {
        if (!produtoId.equals(dadosProduto.produtoId())) {
            throw new IllegalArgumentException("ID do produto no caminho não confere com o corpo da requisição.");
        }
        Produto produtoAtualizado = produtoService.atualizarProduto(estabelecimentoId, dadosProduto);
        return ResponseEntity.ok(new ProdutoDetalhamento(produtoAtualizado));
    }

    /**
     * Exclui um produto de um estabelecimento específico.
     *
     * @param estabelecimentoId ID do estabelecimento
     * @param produtoId         ID do produto a ser excluído
     * @return ResponseEntity sem conteúdo (204) se a exclusão for bem-sucedida
     */
    @DeleteMapping("/{estabelecimentoId}/{produtoId}")
    @PreAuthorize("@preAuthorizeService.podeGerenciarEstabelecimento(#estabelecimentoId, authentication.principal)")
    @Transactional
    public ResponseEntity<Void> excluirProduto(
            @PathVariable Long estabelecimentoId,
            @PathVariable Long produtoId
    ) {
        produtoService.excluirProduto(estabelecimentoId, produtoId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Lista todos os produtos de um estabelecimento específico.
     *
     * @param estabelecimentoId ID do estabelecimento
     * @return ResponseEntity contendo a lista de produtos detalhados do estabelecimento
     */
    @GetMapping("/{estabelecimentoId}")
    @PreAuthorize("@preAuthorizeService.podeGerenciarEstabelecimento(#estabelecimentoId, authentication.principal)")
    public ResponseEntity<List<ProdutoDetalhamento>> listarProdutos(
            @PathVariable Long estabelecimentoId
    ) {
        List<ProdutoDetalhamento> produtos = produtoService.listarProdutosDoEstabelecimento(estabelecimentoId);
        return ResponseEntity.ok(produtos);
    }

}
