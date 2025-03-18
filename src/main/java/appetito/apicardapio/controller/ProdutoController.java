package appetito.apicardapio.controller;

import appetito.apicardapio.dto.ProdutoCadastro;
import appetito.apicardapio.dto.forGet.ProdutoDados;
import appetito.apicardapio.entity.Produto;
import appetito.apicardapio.repository.ProdutoRepository;
import appetito.apicardapio.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public Produto createProduto(@RequestBody ProdutoCadastro produtoCadastro) {
        return produtoService.createProduto(produtoCadastro);
    }

    @PutMapping("/{id}")
    public Produto updateProduto(@PathVariable Long id, @RequestBody ProdutoCadastro produtoCadastro) {
        return produtoService.updateProduto(id, produtoCadastro);
    }

    @DeleteMapping("/{id}")
    public void deleteProduto(@PathVariable Long id) {
        produtoService.deleteProduto(id);
    }
}