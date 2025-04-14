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

        atualizarDadosProduto(produto, produtoCadastro, cardapio);
        return produtoRepository.save(produto);
    }
    private void atualizarDadosProduto(Produto produto, ProdutoCadastro dto, Cardapio cardapio) {
        produto.setCardapio(cardapio);
        produto.setNome_curto(dto.nome_curto());
        produto.setNome_longo(dto.nome_longo());
        produto.setCategoria(dto.categoria());
        produto.setTamanho(dto.tamanho());
        produto.setPreco_custo(dto.preco_custo());
        produto.setPreco_venda(dto.preco_venda());
        produto.setEstoque(dto.estoque());
        produto.setEstoque_minimo(dto.estoque_minimo());
        produto.setAtivo(dto.ativo());
        produto.setUnidade_medida(dto.unidade_medida());
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
