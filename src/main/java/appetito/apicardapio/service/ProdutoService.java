package appetito.apicardapio.service;

import appetito.apicardapio.dto.cadastro.ProdutoCadastro;
import appetito.apicardapio.entity.Cardapio;
import appetito.apicardapio.entity.Estabelecimento;
import appetito.apicardapio.entity.Produto;
import appetito.apicardapio.entity.UsuarioDashboard;
import appetito.apicardapio.exception.ResourceNotFoundException;
import appetito.apicardapio.entity.UsuarioEstabelecimento;
import appetito.apicardapio.repository.CardapioRepository;
import appetito.apicardapio.repository.ProdutoRepository;
import appetito.apicardapio.repository.UsuarioEstabelecimentoRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final UsuarioEstabelecimentoRepository usuarioEstabelecimentoRepository;
    private final CardapioRepository cardapioRepository;

    public ProdutoService(ProdutoRepository produtoRepository, UsuarioEstabelecimentoRepository usuarioEstabelecimentoRepository, CardapioRepository cardapioRepository) {
        this.produtoRepository = produtoRepository;
        this.usuarioEstabelecimentoRepository = usuarioEstabelecimentoRepository;
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

    public Produto cadastrarProduto(ProdutoCadastro dadosProduto) {
        UsuarioDashboard usuario = (UsuarioDashboard) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Estabelecimento estabelecimento = usuarioEstabelecimentoRepository
                .findAllByUsuario(usuario)
                .stream()
                .map(UsuarioEstabelecimento::getEstabelecimento)
                .findFirst()
                .orElseThrow(() -> new AccessDeniedException("Você não está vinculado a um estabelecimento."));

        Cardapio cardapio = cardapioRepository.findById(dadosProduto.cardapio())
                .orElseThrow(() -> new ResourceNotFoundException("Cardápio não encontrado."));

        if (!cardapio.getEstabelecimento().equals(estabelecimento)) {
            throw new AccessDeniedException("Você não pode adicionar produtos a este cardápio.");
        }

        Produto produto = new Produto(dadosProduto);
        produto.setCardapio(cardapio);

        return produtoRepository.save(produto);
    }

}
