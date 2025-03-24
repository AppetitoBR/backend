package appetito.apicardapio.service;

import appetito.apicardapio.dto.cadastro.PedidoCadastro;
import appetito.apicardapio.entity.*;
import appetito.apicardapio.enums.StatusPedido;
import appetito.apicardapio.exception.ResourceNotFoundException;
import appetito.apicardapio.repository.PedidoItemRepository;
import appetito.apicardapio.repository.PedidoRepository;
import appetito.apicardapio.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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
        Pedido pedido = new Pedido();
        Long usuarioId = pedidoCadastro.usuarioId();
        pedido.setUsuario_id(usuarioId);


        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        List<PedidoItem> itens = pedidoCadastro.itens().stream().map(itemCadastro -> {
            Produto produto = produtoRepository.findById(itemCadastro.produtoId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            PedidoItem pedidoItem = new PedidoItem();
            pedidoItem.setPedido(pedidoSalvo);
            pedidoItem.setProduto_id(produto.getProduto_id());
            pedidoItem.setQuantidade(itemCadastro.quantidade());
            pedidoItem.setPrecoUnitario(produto.getPreco_venda());
            pedidoItem.setNomeProduto(produto.getNome_curto());

            return pedidoItem;
        }).collect(Collectors.toList());

        pedidoItemRepository.saveAll(itens);

        pedidoSalvo.setItens(itens);
        pedidoSalvo.calcularTotal();

        return pedidoSalvo;
    }

    public Pedido atualizarPedido(Long id, PedidoCadastro pedidoCadastro) {
        Pedido pedidoExistente = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));
        Long usuarioId = pedidoCadastro.usuarioId();
        pedidoExistente.setUsuario_id(usuarioId);

        List<PedidoItem> novosItens = pedidoCadastro.itens().stream().map(itemCadastro -> {
            Produto produto = produtoRepository.findById(itemCadastro.produtoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));
            PedidoItem pedidoItem = new PedidoItem();
            pedidoItem.setPedido(pedidoExistente);
            pedidoItem.setProduto_id(produto.getProduto_id());
            pedidoItem.setQuantidade(itemCadastro.quantidade());
            pedidoItem.setPrecoUnitario(produto.getPreco_venda());
            pedidoItem.setNomeProduto(produto.getNome_curto());

            return pedidoItem;
        }).collect(Collectors.toList());
        pedidoExistente.setItens(novosItens);
        pedidoExistente.calcularTotal();
        return pedidoRepository.save(pedidoExistente);
    }
    public Pedido buscarPedido(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));
    }
    public List<Pedido> listarPedidos() {
        List<Pedido> pedidos = pedidoRepository.findAll();
        if (pedidos.isEmpty()) {
            throw new ResourceNotFoundException("Nenhum Pedido encontrado");
        }
        return pedidos;
    }
    public void excluirPedido(Long id) {
        Pedido pedidoExistente = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));
        pedidoRepository.delete(pedidoExistente);
    }
}


