package appetito.apicardapio.service;

import appetito.apicardapio.dto.cadastro.ProdutoCadastro;
import appetito.apicardapio.entity.Cardapio;
import appetito.apicardapio.entity.Produto;
import appetito.apicardapio.exception.ResourceNotFoundException;
import appetito.apicardapio.repository.CardapioRepository;
import appetito.apicardapio.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private CardapioRepository cardapioRepository;

    public Produto getProdutoById(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));
    }

    public Produto updateProduto(Long id, ProdutoCadastro produtoCadastro) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        Cardapio cardapio = cardapioRepository.findById(produtoCadastro.cardapio())
                .orElseThrow(() -> new ResourceNotFoundException("Cardápio não encontrado"));

        produto.setCardapio(cardapio);
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
        produtoRepository.save(produto);
        return produto;
    }

    public void deleteProduto(Long id) {
        produtoRepository.deleteById(id);
    }


    public Produto setImagemProduto(Long id, MultipartFile arquivoImagem) throws IOException {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        byte[] imagemBytes = arquivoImagem.getBytes();
        produto.setImagens(imagemBytes);
        produtoRepository.save(produto);

        return produto;
    }
}
