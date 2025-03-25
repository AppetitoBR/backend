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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pedido_item_id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pedido_id")
    @JsonBackReference
    private Pedido pedido;

    private Long produto_id;

    private Integer quantidade;

    private BigDecimal precoUnitario;

    private String nomeProduto;

    public PedidoItem(Pedido pedido, Produto produto, Integer quantidade) {
        this.pedido = pedido;
        this.produto_id = produto.getProduto_id();
        this.quantidade = quantidade;
        this.precoUnitario = produto.getPreco_venda();
        this.nomeProduto = produto.getNome_curto();
    }
}

