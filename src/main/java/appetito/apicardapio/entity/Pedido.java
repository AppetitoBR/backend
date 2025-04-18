package appetito.apicardapio.entity;

import appetito.apicardapio.enums.StatusPedido;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Table(name = "pedido")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Pedido {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long pedido_id;

        @ManyToOne
        @JoinColumn(name = "cliente_id", nullable = false)
        private Cliente cliente;

        @ManyToOne
        @JoinColumn(name = "usuario_dashboard_id")
        private UsuarioDashboard usuarioDashboard;

        @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
        @JsonManagedReference
        private List<PedidoItem> itens = new ArrayList<>();

        private BigDecimal total = BigDecimal.ZERO;

        @ManyToOne
        @JoinColumn(name = "mesa_id", nullable = false)
        private Mesa mesa;

        @Enumerated(EnumType.STRING)
        private StatusPedido status;

        public Pedido(Cliente cliente, Mesa mesa) {
                this.cliente = cliente;
                this.mesa = mesa;
                this.status = StatusPedido.ABERTO;
                this.itens = new ArrayList<>();
        }


        public void calcularTotal() {
            this.total = itens.stream()
                    .map(item -> item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
    }

