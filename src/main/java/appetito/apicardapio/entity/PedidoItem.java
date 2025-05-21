package appetito.apicardapio.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PedidoItem {

    /**
     * Identificador único do item do pedido.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pedido_item_id;

    /**
     * Pedido ao qual esse item pertence.
     * Fetch EAGER para carregar junto com o item.
     * JsonBackReference para evitar recursão na serialização JSON.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pedido_id")
    @JsonBackReference
    private Pedido pedido;

    /**
     * Identificador do produto associado ao item do pedido.
     */
    private Long produto_id;

    /**
     * Quantidade do produto nesse item do pedido.
     */
    private Integer quantidade;

    /**
     * Preço unitário do produto no momento do pedido.
     */
    private BigDecimal precoUnitario;

    /**
     * Construtor para criar um PedidoItem a partir do pedido, produto e quantidade.
     * Preço unitário é obtido do produto.
     *
     * @param pedido Pedido ao qual o item pertence.
     * @param produto Produto associado ao item.
     * @param quantidade Quantidade do produto.
     */
    public PedidoItem(Pedido pedido, Produto produto, Integer quantidade) {
        this.pedido = pedido;
        this.produto_id = produto.getProduto_id();
        this.quantidade = quantidade;
        this.precoUnitario = produto.getPreco_venda();
    }
}
