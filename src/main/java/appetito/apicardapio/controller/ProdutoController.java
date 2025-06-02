package appetito.apicardapio.controller;

import appetito.apicardapio.dto.cadastro.ProdutoCadastro;
import appetito.apicardapio.dto.GetAll.ProdutoDados;
import appetito.apicardapio.dto.detalhamento.ProdutoDetalhamento;
import appetito.apicardapio.entity.Cardapio;
import appetito.apicardapio.entity.Estabelecimento;
import appetito.apicardapio.entity.Produto;
import appetito.apicardapio.entity.UsuarioDashboard;
import appetito.apicardapio.entity.UsuarioEstabelecimento;
import appetito.apicardapio.exception.ResourceNotFoundException;
import appetito.apicardapio.repository.CardapioRepository;
import appetito.apicardapio.repository.ProdutoRepository;
import appetito.apicardapio.repository.UsuarioEstabelecimentoRepository;
import appetito.apicardapio.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private ProdutoRepository produtoRepository;
    private final UsuarioEstabelecimentoRepository usuarioEstabelecimentoRepository;
    private final CardapioRepository cardapioRepository;

    public ProdutoController(UsuarioEstabelecimentoRepository usuarioEstabelecimentoRepository, CardapioRepository cardapioRepository) {
        this.usuarioEstabelecimentoRepository = usuarioEstabelecimentoRepository;

        this.cardapioRepository = cardapioRepository;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE')")
    @Transactional
    public ResponseEntity<ProdutoDetalhamento> cadastrarProduto(
            @RequestBody @Valid ProdutoCadastro dadosProduto,
            UriComponentsBuilder uriP) {

        Produto produto = produtoService.cadastrarProduto(dadosProduto);

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
}