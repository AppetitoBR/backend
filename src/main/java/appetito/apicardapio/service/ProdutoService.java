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

    /**
     * Busca um produto pelo seu ID.
     *
     * @param id o ID do produto
     * @return o produto encontrado
     * @throws ResourceNotFoundException se o produto não for encontrado
     */
    public Produto getProdutoById(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));
    }

    /**
     * Define a imagem de um produto a partir de um arquivo enviado.
     *
     * @param id             o ID do produto
     * @param arquivoImagem  o arquivo de imagem
     * @return o produto atualizado com a imagem
     * @throws IOException se ocorrer erro ao ler o arquivo
     * @throws ResourceNotFoundException se o produto não for encontrado
     */
    public Produto setImagemProduto(Long id, MultipartFile arquivoImagem) throws IOException {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        byte[] imagemBytes = arquivoImagem.getBytes();
        produto.setImagens(imagemBytes);

        return produtoRepository.save(produto);
    }

    /**
     * Cadastra um novo produto em um cardápio de um estabelecimento.
     *
     * @param estabelecimentoId o ID do estabelecimento
     * @param dadosProduto      os dados do novo produto
     * @return o produto cadastrado
     * @throws ResourceNotFoundException se o cardápio não for encontrado
     * @throws AccessDeniedException se o cardápio não pertencer ao estabelecimento
     */
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

    /**
     * Atualiza os dados de um produto.
     *
     * @param estabelecimentoId o ID do estabelecimento
     * @param dados             os novos dados do produto
     * @return o produto atualizado
     * @throws ResourceNotFoundException se o produto ou o cardápio não for encontrado
     * @throws AccessDeniedException se o produto não pertencer ao estabelecimento
     */
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

    /**
     * Exclui um produto de um estabelecimento.
     *
     * @param estabelecimentoId o ID do estabelecimento
     * @param produtoId         o ID do produto a ser excluído
     * @throws ResourceNotFoundException se o produto não for encontrado
     * @throws AccessDeniedException se o produto não pertencer ao estabelecimento
     */
    public void excluirProduto(Long estabelecimentoId, Long produtoId) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado."));

        if (!produto.getCardapio().getEstabelecimento().getEstabelecimentoId().equals(estabelecimentoId)) {
            throw new AccessDeniedException("Este produto não pertence ao estabelecimento informado.");
        }

        produtoRepository.delete(produto);
    }

    /**
     * Lista todos os produtos de um estabelecimento.
     *
     * @param estabelecimentoId o ID do estabelecimento
     * @return uma lista de detalhes dos produtos
     */
    public List<ProdutoDetalhamento> listarProdutosDoEstabelecimento(Long estabelecimentoId) {
        List<Produto> produtos = produtoRepository.findByCardapio_Estabelecimento_EstabelecimentoId(estabelecimentoId);
        return produtos.stream()
                .map(ProdutoDetalhamento::new)
                .toList();
    }
}
