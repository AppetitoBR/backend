package appetito.apicardapio.dto.GetAll;

import appetito.apicardapio.entity.Pedido;
import appetito.apicardapio.enums.StatusPedido;

import java.math.BigDecimal;

public record PedidoDados(
        Long pedido_id,
        StatusPedido status,
        BigDecimal total
) {
    public PedidoDados(Pedido pedido) {
        this(
                pedido.getPedido_id(),
                pedido.getStatus(),
                pedido.getTotal()
        );
    }
}
