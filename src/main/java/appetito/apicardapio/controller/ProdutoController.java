package appetito.apicardapio.controller;

import appetito.apicardapio.dto.cadastro.ProdutoCadastro;
import appetito.apicardapio.dto.GetAll.ProdutoDados;
import appetito.apicardapio.dto.detalhamento.ProdutoDetalhamento;
import appetito.apicardapio.entity.Produto;
import appetito.apicardapio.repository.ProdutoRepository;
import appetito.apicardapio.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private ProdutoRepository produtoRepository;

    @GetMapping
    public ResponseEntity<List<ProdutoDados>> getAllProdutosAtivos() {
        var lista = produtoRepository.findAllByAtivoTrue().stream().map(ProdutoDados::new).toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public Produto getProdutoById(@PathVariable Long id) {
        return produtoService.getProdutoById(id);
    }

    @PostMapping
    public ResponseEntity<ProdutoDetalhamento> cadastrarProduto(@RequestBody @Valid ProdutoCadastro dadosProduto, UriComponentsBuilder uriP){
        var produto = new Produto(dadosProduto);
        produtoRepository.save(new Produto(dadosProduto));
        var uri = uriP.path("/produto/{id}").buildAndExpand(produto.getProduto_id()).toUri();
        return ResponseEntity.created(uri).body(new ProdutoDetalhamento(produto));
    }

    @PutMapping("/{id}")
    public Produto updateProduto(@PathVariable Long id, @RequestBody ProdutoCadastro produtoCadastro) {
        return produtoService.updateProduto(id, produtoCadastro);
    }

    @DeleteMapping("/{id}")
    public void deleteProduto(@PathVariable Long id) {
        produtoService.deleteProduto(id);
    }

    @GetMapping("/cardapio/{id}")
    public ResponseEntity<List<ProdutoDados>> getAllProdutosbyCardapio(@PathVariable Long id) {
        var lista = produtoRepository.findAllByCardapio(id)
                .stream()
                .map(ProdutoDados::new)
                .toList();
        return ResponseEntity.ok(lista);
    }

 //   @Operation(summary = "Upload da imagem de perfil do usuário")
 //   @PostMapping(value = "/{id}/upload-imagem", consumes = "multipart/form-data")
 //   public ResponseEntity<String> uploadImagemProduto(
 //           @PathVariable Long id,
 //           @RequestPart("file") MultipartFile file
  //  ) {
 //       try {
 //           Produto produto = produtoService.salvarImagemPerfil(id, file);
  //          if (produto != null) {
   //             return ResponseEntity.ok("Imagem do produto foi salva");
  //          } else {
  //              return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto não encontrado");
 //           }
  //      } catch (IOException e) {
  //          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao salvar imagem");
   //     }
 //   }
}