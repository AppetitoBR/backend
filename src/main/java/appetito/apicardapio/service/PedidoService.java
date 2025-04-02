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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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
        Pedido pedido = new Pedido(usuario.getUsuario_dashboard_id());
        criarItensDoPedido(pedidoCadastro, pedido).forEach(pedido.getItens()::add);
        pedido.calcularTotal();
        return pedidoRepository.save(pedido);
    }

    public Pedido buscarPedido(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));
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

        // Verificação de segurança
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof UsuarioDashboard usuario) ||
                !pedido.getUsuario_id().equals(usuario.getUsuario_dashboard_id())) {
            throw new ResourceNotFoundException("Operação não permitida");
        }

        // Atualização eficiente dos itens
        atualizarItensDoPedidoEficiente(pedido, itensAtualizacao);

        // Recalcula o total automaticamente
        pedido.calcularTotal();

        return pedido;
    }

    private void atualizarItensDoPedidoEficiente(Pedido pedido, List<ItemAtualizacao> itensAtualizacao) {
        // 1. Obter todos os IDs de produtos de uma vez
        List<Long> produtoIds = itensAtualizacao.stream()
                .map(ItemAtualizacao::produto_id)
                .distinct()
                .toList();

        // 2. Buscar produtos em lote (otimizado)
        Map<Long, Produto> produtosMap = produtoRepository.findAllById(produtoIds)
                .stream()
                .collect(Collectors.toMap(Produto::getProduto_id, Function.identity()));

        // 3. Criar mapa de atualizações
        Map<Long, ItemAtualizacao> itensParaAtualizar = itensAtualizacao.stream()
                .collect(Collectors.toMap(
                        ItemAtualizacao::produto_id,
                        Function.identity(),
                        (existente, novo) -> novo // Resolver conflitos (manter o último)
                ));

        // 4. Processar itens existentes
        Iterator<PedidoItem> iterator = pedido.getItens().iterator();
        while (iterator.hasNext()) {
            PedidoItem item = iterator.next();
            ItemAtualizacao atualizacao = itensParaAtualizar.get(item.getProduto_id());

            if (atualizacao != null) {
                // Atualizar apenas a quantidade do item existente
                item.setQuantidade(atualizacao.quantidade());
                itensParaAtualizar.remove(item.getProduto_id());
            } else {
                // Remover item que não está na lista de atualização
                iterator.remove();
            }
        }

        // 5. Adicionar novos itens
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