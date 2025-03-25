package appetito.apicardapio.dto.put;

import appetito.apicardapio.dto.detalhamento.ItemDetalhamento;
import appetito.apicardapio.enums.StatusPedido;

import java.math.BigDecimal;
import java.util.List;

public record PedidoAtualizacao(
        Long pedido_id,
        Long usuario_id,
        List<ItemAtualizacao> itens,
        BigDecimal total,
        StatusPedido status
) {

}
