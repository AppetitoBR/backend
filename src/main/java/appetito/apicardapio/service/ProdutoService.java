package appetito.apicardapio.service;

import appetito.apicardapio.dto.cadastro.ProdutoCadastro;
import appetito.apicardapio.dto.detalhamento.ProdutoDetalhamento;
import appetito.apicardapio.dto.put.ProdutoAtualizacao;
import appetito.apicardapio.entity.Cardapio;
import appetito.apicardapio.entity.Produto;
import appetito.apicardapio.exception.ResourceNotFoundException;
import appetito.apicardapio.repository.CardapioRepository;
import appetito.apicardapio.repository.ProdutoRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final CardapioRepository cardapioRepository;

    public ProdutoService(ProdutoRepository produtoRepository, CardapioRepository cardapioRepository) {
        this.produtoRepository = produtoRepository;
        this.cardapioRepository = cardapioRepository;
    }

    public Produto getProdutoById(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));
    }


    public Produto setImagemProduto(Long id, MultipartFile arquivoImagem) throws IOException {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        byte[] imagemBytes = arquivoImagem.getBytes();
        produto.setImagens(imagemBytes);

        return produtoRepository.save(produto);
    }

    public Produto cadastrarProduto(Long estabelecimentoId, ProdutoCadastro dadosProduto) {
        Cardapio cardapio = cardapioRepository.findById(dadosProduto.cardapio())
                .orElseThrow(() -> new ResourceNotFoundException("Cardápio não encontrado."));

        if (!cardapio.getEstabelecimento().getEstabelecimentoId().equals(estabelecimentoId)) {
            throw new AccessDeniedException("Você não pode adicionar produtos a este cardápio.");
        }

        Produto produto = new Produto(dadosProduto);
        produto.setCardapio(cardapio);

        return produtoRepository.save(produto);
    }
    @Transactional
    public Produto atualizarProduto(Long estabelecimentoId, ProdutoAtualizacao dados) {
        Produto produto = produtoRepository.findById(dados.produtoId())
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        if (!produto.getCardapio().getEstabelecimento().getEstabelecimentoId().equals(estabelecimentoId)) {
            throw new AccessDeniedException("Produto não pertence a este estabelecimento.");
        }

        Cardapio cardapio = cardapioRepository.findById(dados.cardapioId())
                .orElseThrow(() -> new ResourceNotFoundException("Cardápio não encontrado"));

        produto.setCardapio(cardapio);
        produto.setNome_curto(dados.nomeCurto());
        produto.setNome_longo(dados.nomeLongo());
        produto.setCategoria(dados.categoria());
        produto.setTamanho(dados.tamanho());
        produto.setPreco_custo(dados.precoCusto());
        produto.setPreco_venda(dados.precoVenda());
        produto.setEstoque(dados.estoque());
        produto.setEstoque_minimo(dados.estoqueMinimo());
        produto.setAtivo(dados.ativo());
        produto.setUnidade_medida(dados.unidadeMedida());
        produto.setImagens(dados.imagens());

        return produtoRepository.save(produto);
    }
    public void excluirProduto(Long estabelecimentoId, Long produtoId) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado."));

        if (!produto.getCardapio().getEstabelecimento().getEstabelecimentoId().equals(estabelecimentoId)) {
            throw new AccessDeniedException("Este produto não pertence ao estabelecimento informado.");
        }

        produtoRepository.delete(produto);
    }
    public List<ProdutoDetalhamento> listarProdutosDoEstabelecimento(Long estabelecimentoId) {
        List<Produto> produtos = produtoRepository.findByCardapio_Estabelecimento_EstabelecimentoId(estabelecimentoId);
        return produtos.stream()
                .map(ProdutoDetalhamento::new)
                .toList();
    }

}
