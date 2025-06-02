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

/**
 * Serviço responsável pelas operações relacionadas aos pedidos.
 */
@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final MesaRepository mesaRepository;

    /**
     * Construtor com injeção de dependência.
     *
     * @param mesaRepository                     repositório de mesas
     */
    public PedidoService(PedidoRepository pedidoRepository, ProdutoRepository produtoRepository, MesaRepository mesaRepository) {
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
        this.mesaRepository = mesaRepository;
    }


    /**
     * Cria um novo pedido com base nos dados informados.
     *
     * @param pedidoCadastro DTO contendo as informações do pedido
     * @return o pedido criado e persistido
     * @throws IllegalArgumentException se os itens forem inválidos ou o total for zero/negativo
     * @throws ResourceNotFoundException se a mesa ou algum produto não for encontrado
     * @throws AccessDeniedException se o usuário não for um cliente autenticado ou for um tipo não autorizado
     */
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

    /**
     * Lista os pedidos feitos pelo cliente autenticado.
     *
     * @return lista de pedidos do cliente
     * @throws AccessDeniedException se o usuário não for um cliente autenticado
     * @throws ResourceNotFoundException se o cliente não tiver pedidos registrados
     */
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
    /**
     * Exclui um pedido do cliente autenticado.
     *
     * @param pedidoId ID do pedido a ser excluído
     * @throws ResourceNotFoundException se o pedido não existir
     * @throws AccessDeniedException se o cliente não for o dono do pedido
     */
    @Transactional
    public void excluirPedido(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication != null ? authentication.getPrincipal() : null;

        if (pedido.getCliente() == null || !(principal instanceof Cliente cliente) || !pedido.getCliente().equals(cliente)) {
            throw new AccessDeniedException("Você não tem permissão para excluir este pedido.");
        }

        pedidoRepository.delete(pedido);
    }
    /**
     * Cria os itens do pedido com base no DTO recebido.
     *
     * @param pedidoCadastro DTO contendo os itens
     * @param pedido         pedido que receberá os itens
     * @return lista de itens criados
     */
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
    /**
     * Atualiza os itens de um pedido já existente, substituindo e/ou removendo conforme informado.
     *
     * @param pedidoId         ID do pedido a ser atualizado
     * @param itensAtualizacao nova lista de itens para o pedido
     * @return o pedido atualizado
     * @throws ResourceNotFoundException se o pedido não existir ou se algum produto não for encontrado
     * @throws AccessDeniedException se o cliente não for o dono do pedido
     */
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
    /**
     * Retorna o cliente autenticado na sessão atual.
     *
     * @return o cliente autenticado
     * @throws AccessDeniedException se o usuário não for um cliente autenticado
     */
    private Cliente getClienteAutenticado() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Cliente cliente)) {
            throw new AccessDeniedException("Usuário não autenticado.");
        }
        return cliente;
    }
    /**
     * Atualiza os itens do pedido de forma eficiente:
     * - Atualiza a quantidade dos itens existentes que também estão na lista de atualização.
     * - Remove os itens que não estão mais presentes na lista de atualização.
     * - Adiciona novos itens que não estavam presentes anteriormente no pedido.
     *
     * @param pedido          o pedido cujos itens serão atualizados
     * @param itensAtualizacao lista com os dados de atualização dos itens (produto_id e quantidade)
     * @throws ResourceNotFoundException se algum produto informado na lista de atualização não for encontrado no banco
     */
    private void atualizarItensDoPedidoEficiente(Pedido pedido, List<ItemAtualizacao> itensAtualizacao) {
        Map<Long, Produto> produtosMap = carregarProdutosMap(itensAtualizacao);
        Map<Long, ItemAtualizacao> itensParaAtualizar = mapearItensAtualizacao(itensAtualizacao);

        atualizarOuRemoverItensExistentes(pedido, itensParaAtualizar);
        adicionarNovosItens(pedido, itensParaAtualizar, produtosMap);
    }
    /**
     * Carrega todos os produtos necessários em um mapa indexado por ID.
     *
     * @param itensAtualizacao lista de itens a atualizar
     * @return mapa de produtos
     */
    private Map<Long, Produto> carregarProdutosMap(List<ItemAtualizacao> itensAtualizacao) {
        List<Long> produtoIds = itensAtualizacao.stream()
                .map(ItemAtualizacao::produto_id)
                .distinct()
                .toList();

        return produtoRepository.findAllById(produtoIds).stream()
                .collect(Collectors.toMap(Produto::getProduto_id, Function.identity()));
    }
    /**
     * Mapeia a lista de atualizações para um mapa por ID de produto.
     *
     * @param itensAtualizacao lista de atualizações
     * @return mapa de atualizações
     */
    private Map<Long, ItemAtualizacao> mapearItensAtualizacao(List<ItemAtualizacao> itensAtualizacao) {
        return itensAtualizacao.stream()
                .collect(Collectors.toMap(
                        ItemAtualizacao::produto_id,
                        Function.identity(),
                        (existente, novo) -> novo // prioriza o último caso tenha duplicado
                ));
    }

    /**
     * Atualiza os itens existentes ou remove os que não estão presentes na nova lista.
     *
     * @param pedido             pedido alvo
     * @param itensParaAtualizar mapa de atualizações por ID do produto
     */
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
    /**
     * Adiciona novos itens ao pedido que não estavam presentes anteriormente.
     *
     * @param pedido             pedido alvo
     * @param itensRestantes     itens restantes após a atualização dos existentes
     * @param produtosMap        mapa de produtos disponíveis
     * @throws ResourceNotFoundException se algum produto informado não for encontrado
     */
    private void adicionarNovosItens(Pedido pedido, Map<Long, ItemAtualizacao> itensRestantes, Map<Long, Produto> produtosMap) {
        for (Map.Entry<Long, ItemAtualizacao> entry : itensRestantes.entrySet()) {
            Produto produto = produtosMap.get(entry.getKey());
            if (produto == null) {
                throw new ResourceNotFoundException("Produto ID " + entry.getKey() + " não encontrado.");
            }
            pedido.getItens().add(new PedidoItem(pedido, produto, entry.getValue().quantidade()));
        }
    }
    /**
     * Lista os pedidos vinculados a um determinado estabelecimento.
     *
     * @param estabelecimentoId ID do estabelecimento
     * @return lista de pedidos do estabelecimento
     */
    public List<Pedido> listarPedidosPorEstabelecimento(Long estabelecimentoId) {
        return pedidoRepository.findByMesa_Estabelecimento_EstabelecimentoId(estabelecimentoId);
    }

}