package appetito.apicardapio.service;

import appetito.apicardapio.dto.cadastro.ProdutoCadastro;
import appetito.apicardapio.entity.Produto;
import appetito.apicardapio.exception.ResourceNotFoundException;
import appetito.apicardapio.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    // Buscar produto por ID
    public Produto getProdutoById(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));
    }
    public Produto createProduto(ProdutoCadastro produtoCadastro) {
        Produto produto = new Produto();
        produto.setCardapio(produtoCadastro.cardapio_id());
        produto.setNome_curto(produtoCadastro.nome_curto());
        produto.setNome_longo(produtoCadastro.nome_longo());
        produto.setCategoria(produtoCadastro.categoria());
        produto.setTamanho(produtoCadastro.tamanho());
        produto.setPreco_custo(produtoCadastro.preco_custo());
        produto.setPreco_venda(produtoCadastro.preco_venda());
        produto.setEstoque(produtoCadastro.estoque());
        produto.setEstoque_minimo(produtoCadastro.estoque_minimo());
        produto.setAtivo(produtoCadastro.ativo());
        produto.setUnidade_medida(produtoCadastro.unidade_medida());
        produto.setImagens(produtoCadastro.imagens());
        return produtoRepository.save(produto);
    }

    // Atualizar produto
    public Produto updateProduto(Long id, ProdutoCadastro produtoCadastro) {
        Produto produto = produtoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));
        produto.setCardapio(produtoCadastro.cardapio_id());
        produto.setNome_curto(produtoCadastro.nome_curto());
        produto.setNome_longo(produtoCadastro.nome_longo());
        produto.setCategoria(produtoCadastro.categoria());
        produto.setTamanho(produtoCadastro.tamanho());
        produto.setPreco_custo(produtoCadastro.preco_custo());
        produto.setPreco_venda(produtoCadastro.preco_venda());
        produto.setEstoque(produtoCadastro.estoque());
        produto.setEstoque_minimo(produtoCadastro.estoque_minimo());
        produto.setAtivo(produtoCadastro.ativo());
        produto.setUnidade_medida(produtoCadastro.unidade_medida());
        produto.setImagens(produtoCadastro.imagens());
        return produtoRepository.save(produto);
    }

    // Deletar produto
    public void deleteProduto(Long id) {
        produtoRepository.deleteById(id);
    }
}
