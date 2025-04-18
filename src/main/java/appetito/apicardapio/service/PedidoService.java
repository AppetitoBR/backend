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

        if (!(authentication.getPrincipal() instanceof Cliente cliente)) {
            throw new ResourceNotFoundException("Usuário não autenticado.");
        }

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

        Pedido pedido = new Pedido(cliente, mesa);
        criarItensDoPedido(pedidoCadastro, pedido).forEach(pedido.getItens()::add);
        pedido.calcularTotal();

        if (pedido.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O total do pedido não pode ser zero ou negativo.");
        }
        return pedidoRepository.save(pedido);
    }

    public List<Pedido> listarPedidosCliente() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof Cliente cliente)) {
            throw new AccessDeniedException("Usuário não autenticado ou inválido.");
        }
        List<Pedido> pedidos = pedidoRepository.findByCliente(cliente);
        if (pedidos.isEmpty()) {
            throw new ResourceNotFoundException("Não há pedidos encontrados para este cliente.");
        }
        return pedidos;
    }

    public List<Pedido> listarPedidos() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof UsuarioDashboard usuario)) {
            throw new ResourceNotFoundException("Usuário não autenticado.");
        }

        List<Long> estabelecimentoIds = usuarioEstabelecimentoRepository.findByUsuario(usuario)
                .stream()
                .map(vinculo -> vinculo.getEstabelecimento().getEstabelecimentoId())
                .toList();

        return pedidoRepository.findAll().stream()
                .filter(pedido -> pedido.getMesa() != null &&
                        pedido.getMesa().getEstabelecimento() != null &&
                        estabelecimentoIds.contains(pedido.getMesa().getEstabelecimento().getEstabelecimentoId()))
                .toList();
    }

   // public void excluirPedido(Long id) {
     //   Pedido pedido = listarPedidosCliente();
     //   pedidoRepository.delete(pedido);
   // }
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
        if (!(authentication.getPrincipal() instanceof Cliente cliente)) {
            throw new ResourceNotFoundException("Operação não permitida. Usuário não autenticado.");
        }

        if (!pedido.getCliente().equals(cliente)) {
            throw new ResourceNotFoundException("Operação não permitida. O cliente não pode atualizar pedido de outro cliente.");
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
            pedido.getItens().add(new PedidoItem(pedido, produto, itemAtualizacao.quantidade()));
        });
    }
}