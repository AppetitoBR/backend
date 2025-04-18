package appetito.apicardapio.dto.detalhamento;
import appetito.apicardapio.entity.Pedido;
import appetito.apicardapio.enums.StatusPedido;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public record PedidoDetalhamento(
        Long pedido_id,
        Long usuario_id,
        List<ItemDetalhamento> itens,
        BigDecimal total,
        StatusPedido status
) {
    public PedidoDetalhamento(Pedido pedido) {
        this(
                pedido.getPedido_id(),
                pedido.getCliente().getId(),
                pedido.getItens().stream().map(ItemDetalhamento::new).toList(),
                pedido.getTotal(),
                pedido.getStatus()
        );
    }
}
