package appetito.apicardapio.service;

import appetito.apicardapio.dto.cadastro.PedidoCadastro;
import appetito.apicardapio.dto.put.ItemAtualizacao;
import appetito.apicardapio.entity.*;
import appetito.apicardapio.exception.ResourceNotFoundException;
import appetito.apicardapio.repository.PedidoItemRepository;
import appetito.apicardapio.repository.PedidoRepository;
import appetito.apicardapio.repository.ProdutoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    public Pedido criarPedido(PedidoCadastro pedidoCadastro) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof UsuarioDashboard usuario)) {
            throw new ResourceNotFoundException("Usuário não autenticado.");
        }

        if (pedidoCadastro.itens().isEmpty()) {
            throw new IllegalArgumentException("O pedido deve conter pelo menos um item.");
        }

        Pedido pedido = new Pedido(usuario.getUsuario_dashboard_id());
        criarItensDoPedido(pedidoCadastro, pedido).forEach(pedido.getItens()::add);
        pedido.calcularTotal();

        if (pedido.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O total do pedido não pode ser zero ou negativo.");
        }

        return pedidoRepository.save(pedido);
    }

    public Pedido buscarPedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof UsuarioDashboard usuario) ||
                !pedido.getCliente_id().equals(usuario.getUsuario_dashboard_id())) {
            throw new AccessDeniedException("Você não tem permissão para acessar esse pedido.");
        }
        return pedido;
    }

    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }

    public void excluirPedido(Long id) {
        Pedido pedido = buscarPedido(id);
        pedidoRepository.delete(pedido);
    }
    private List<PedidoItem> criarItensDoPedido(PedidoCadastro pedidoCadastro, Pedido pedido) {
        return pedidoCadastro.itens().stream()
                .map(itemCadastro -> {
                    Produto produto = produtoRepository.findById(itemCadastro.produtoId())
                            .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

                    return new PedidoItem(pedido, produto, itemCadastro.quantidade());
                })
                .collect(Collectors.toList());
    }
    @Transactional
    public Pedido atualizarItensPedido(Long pedidoId, List<ItemAtualizacao> itensAtualizacao) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof UsuarioDashboard usuario) ||
                !pedido.getCliente_id().equals(usuario.getUsuario_dashboard_id())) {
            throw new ResourceNotFoundException("Operação não permitida");
        }
        atualizarItensDoPedidoEficiente(pedido, itensAtualizacao);
        pedido.calcularTotal();

        return pedido;
    }

    private void atualizarItensDoPedidoEficiente(Pedido pedido, List<ItemAtualizacao> itensAtualizacao) {
        List<Long> produtoIds = itensAtualizacao.stream()
                .map(ItemAtualizacao::produto_id)
                .distinct()
                .toList();
        Map<Long, Produto> produtosMap = produtoRepository.findAllById(produtoIds)
                .stream()
                .collect(Collectors.toMap(Produto::getProduto_id, Function.identity()));
        Map<Long, ItemAtualizacao> itensParaAtualizar = itensAtualizacao.stream()
                .collect(Collectors.toMap(
                        ItemAtualizacao::produto_id,
                        Function.identity(),
                        (existente, novo) -> novo
                ));
        Iterator<PedidoItem> iterator = pedido.getItens().iterator();
        while (iterator.hasNext()) {
            PedidoItem item = iterator.next();
            ItemAtualizacao atualizacao = itensParaAtualizar.get(item.getProduto_id());

            if (atualizacao != null) {
                item.setQuantidade(atualizacao.quantidade());
                itensParaAtualizar.remove(item.getProduto_id());
            } else {
                iterator.remove();
            }
        }
        itensParaAtualizar.forEach((produtoId, itemAtualizacao) -> {
            Produto produto = produtosMap.get(produtoId);
            if (produto == null) {
                throw new ResourceNotFoundException("Produto ID " + produtoId + " não encontrado");
            }
            pedido.getItens().add(new PedidoItem(
                    pedido,
                    produto,
                    itemAtualizacao.quantidade()
            ));
        });
    }
}