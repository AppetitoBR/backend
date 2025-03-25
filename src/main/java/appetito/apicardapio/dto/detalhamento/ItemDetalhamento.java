package appetito.apicardapio.dto.detalhamento;

import appetito.apicardapio.entity.PedidoItem;

import java.math.BigDecimal;

public record ItemDetalhamento(
        Long pedido_item_id,
        Long produto_id,
        Integer quantidade,
        BigDecimal precoUnitario,
        String nomeProduto
) {
    public ItemDetalhamento(PedidoItem item) {
        this(
                item.getPedido_item_id(),
                item.getProduto_id(),
                item.getQuantidade(),
                item.getPrecoUnitario(),
                item.getNomeProduto()
        );
    }
}