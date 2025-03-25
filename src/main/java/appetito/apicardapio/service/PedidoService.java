package appetito.apicardapio.service;

import appetito.apicardapio.dto.cadastro.PedidoCadastro;
import appetito.apicardapio.entity.*;
import appetito.apicardapio.enums.StatusPedido;
import appetito.apicardapio.exception.ResourceNotFoundException;
import appetito.apicardapio.repository.PedidoItemRepository;
import appetito.apicardapio.repository.PedidoRepository;
import appetito.apicardapio.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

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
        if (!(authentication.getPrincipal() instanceof Usuario usuario)) {
            throw new ResourceNotFoundException("Usuário não autenticado.");
        }
        Long usuarioId = usuario.getUsuario_id();
        Pedido pedido = new Pedido(usuarioId);
        Pedido pedidoSalvo = pedidoRepository.save(pedido);
        List<PedidoItem> itens = criarItensDoPedido(pedidoCadastro, pedidoSalvo);
        pedidoItemRepository.saveAll(itens);
        pedidoSalvo.setItens(itens);
        pedidoSalvo.calcularTotal();

        return pedidoRepository.save(pedidoSalvo);
    }

    public Pedido atualizarPedido(Long id, PedidoCadastro pedidoCadastro) {
        Pedido pedidoExistente = buscarPedido(id);
        pedidoExistente.setUsuario_id(pedidoCadastro.usuarioId());

        List<PedidoItem> novosItens = criarItensDoPedido(pedidoCadastro, pedidoExistente);
        pedidoExistente.setItens(novosItens);
        pedidoExistente.calcularTotal();

        return pedidoRepository.save(pedidoExistente);
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
        return pedidoCadastro.itens().stream().map(itemCadastro -> {
            Produto produto = produtoRepository.findById(itemCadastro.produtoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));
            return new PedidoItem(pedido, produto, itemCadastro.quantidade());
        }).toList();
    }
}
