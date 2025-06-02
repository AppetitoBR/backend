package appetito.apicardapio.service;

import appetito.apicardapio.dto.cadastro.ItemPedidoCadastro;
import appetito.apicardapio.dto.cadastro.PedidoCadastro;
import appetito.apicardapio.dto.put.ItemAtualizacao;
import appetito.apicardapio.entity.*;
import appetito.apicardapio.exception.ResourceNotFoundException;
import appetito.apicardapio.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private PedidoItemRepository pedidoItemRepository;
    @Autowired
    private ProdutoRepository produtoRepository;

    private final MesaRepository mesaRepository;

    private final UsuarioEstabelecimentoRepository usuarioEstabelecimentoRepository;

    public PedidoService(MesaRepository mesaRepository, UsuarioEstabelecimentoRepository usuarioEstabelecimentoRepository) {
        this.mesaRepository = mesaRepository;
        this.usuarioEstabelecimentoRepository = usuarioEstabelecimentoRepository;
    }


    @Transactional
    public Pedido criarPedido(PedidoCadastro pedidoCadastro) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication != null ? authentication.getPrincipal() : null;

        if (pedidoCadastro.itens().isEmpty()) {
            throw new IllegalArgumentException("O pedido deve conter pelo menos um item.");
        }

        Mesa mesa = mesaRepository.findById(pedidoCadastro.mesaId())
                .orElseThrow(() -> new ResourceNotFoundException("Mesa não encontrada."));

        List<Long> produtoIds = pedidoCadastro.itens().stream()
                .map(ItemPedidoCadastro::produtoId)
                .toList();

        List<Produto> produtos = produtoRepository.findAllById(produtoIds);

        boolean produtosValidos = produtos.stream()
                .allMatch(produto -> produto.getCardapio().getEstabelecimento().getEstabelecimentoId()
                        .equals(mesa.getEstabelecimento().getEstabelecimentoId()));

        if (!produtosValidos) {
            throw new IllegalArgumentException("Alguns produtos não pertencem ao mesmo estabelecimento da mesa.");
        }

        Pedido pedido;

        if (principal instanceof Cliente cliente) {
            pedido = new Pedido(cliente, mesa);
        } else if (principal instanceof String p && p.equals("anonymousUser")) {
            pedido = new Pedido(null, mesa);
        } else {
            throw new AccessDeniedException("Acesso não autorizado.");
        }

        criarItensDoPedido(pedidoCadastro, pedido).forEach(pedido.getItens()::add);
        pedido.calcularTotal();

        if (pedido.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O total do pedido não pode ser zero ou negativo.");
        }

        return pedidoRepository.save(pedido);
    }

    public List<Pedido> listarPedidosCliente() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication != null && authentication.getPrincipal() instanceof Cliente cliente)) {
            throw new AccessDeniedException("Acesso permitido apenas para clientes autenticados.");
        }

        List<Pedido> pedidos = pedidoRepository.findByCliente(cliente);

        if (pedidos.isEmpty()) {
            throw new ResourceNotFoundException("Nenhum pedido encontrado para este cliente.");
        }

        return pedidos;
    }

    private List<PedidoItem> criarItensDoPedido(PedidoCadastro pedidoCadastro, Pedido pedido) {
        return pedidoCadastro.itens().stream()
                .map(item -> {
                    Long produtoId = item.produtoId();
                    Produto produto = produtoRepository.findById(produtoId)
                            .orElseThrow(() -> new ResourceNotFoundException("Produto com ID " + produtoId + " não encontrado."));

                    return new PedidoItem(pedido, produto, item.quantidade());
                })
                .toList();
    }

    @Transactional
    public Pedido atualizarItensPedido(Long pedidoId, List<ItemAtualizacao> itensAtualizacao) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado."));

        Cliente cliente = getClienteAutenticado();

        if (!pedido.getCliente().equals(cliente)) {
            throw new AccessDeniedException("Você não tem permissão para atualizar este pedido.");
        }

        atualizarItensDoPedidoEficiente(pedido, itensAtualizacao);
        pedido.calcularTotal();

        return pedido;
    }

    private Cliente getClienteAutenticado() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Cliente cliente)) {
            throw new AccessDeniedException("Usuário não autenticado.");
        }
        return cliente;
    }

    private void atualizarItensDoPedidoEficiente(Pedido pedido, List<ItemAtualizacao> itensAtualizacao) {
        Map<Long, Produto> produtosMap = carregarProdutosMap(itensAtualizacao);
        Map<Long, ItemAtualizacao> itensParaAtualizar = mapearItensAtualizacao(itensAtualizacao);

        atualizarOuRemoverItensExistentes(pedido, itensParaAtualizar);
        adicionarNovosItens(pedido, itensParaAtualizar, produtosMap);
    }

    private Map<Long, Produto> carregarProdutosMap(List<ItemAtualizacao> itensAtualizacao) {
        List<Long> produtoIds = itensAtualizacao.stream()
                .map(ItemAtualizacao::produto_id)
                .distinct()
                .toList();

        return produtoRepository.findAllById(produtoIds).stream()
                .collect(Collectors.toMap(Produto::getProduto_id, Function.identity()));
    }

    private Map<Long, ItemAtualizacao> mapearItensAtualizacao(List<ItemAtualizacao> itensAtualizacao) {
        return itensAtualizacao.stream()
                .collect(Collectors.toMap(
                        ItemAtualizacao::produto_id,
                        Function.identity(),
                        (existente, novo) -> novo // prioriza o último caso tenha duplicado
                ));
    }

    private void atualizarOuRemoverItensExistentes(Pedido pedido, Map<Long, ItemAtualizacao> itensParaAtualizar) {
        Iterator<PedidoItem> iterator = pedido.getItens().iterator();
        while (iterator.hasNext()) {
            PedidoItem item = iterator.next();
            ItemAtualizacao atualizacao = itensParaAtualizar.remove(item.getProduto_id());

            if (atualizacao != null) {
                item.setQuantidade(atualizacao.quantidade());
            } else {
                iterator.remove(); // remove itens que não estão na nova lista
            }
        }
    }

    private void adicionarNovosItens(Pedido pedido, Map<Long, ItemAtualizacao> itensRestantes, Map<Long, Produto> produtosMap) {
        for (Map.Entry<Long, ItemAtualizacao> entry : itensRestantes.entrySet()) {
            Produto produto = produtosMap.get(entry.getKey());
            if (produto == null) {
                throw new ResourceNotFoundException("Produto ID " + entry.getKey() + " não encontrado.");
            }
            pedido.getItens().add(new PedidoItem(pedido, produto, entry.getValue().quantidade()));
        }
    }

    public List<Pedido> listarPedidosPorEstabelecimento(Long estabelecimentoId) {
        return pedidoRepository.findByMesa_Estabelecimento_EstabelecimentoId(estabelecimentoId);
    }

}