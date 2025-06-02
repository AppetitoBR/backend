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

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

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

    @GetMapping("/{estabelecimentoId}")
    @PreAuthorize("@preAuthorizeService.podeGerenciarEstabelecimento(#estabelecimentoId, authentication.principal)")
    public ResponseEntity<List<ProdutoDetalhamento>> listarProdutos(
            @PathVariable Long estabelecimentoId
    ) {
        List<ProdutoDetalhamento> produtos = produtoService.listarProdutosDoEstabelecimento(estabelecimentoId);
        return ResponseEntity.ok(produtos);
    }


}