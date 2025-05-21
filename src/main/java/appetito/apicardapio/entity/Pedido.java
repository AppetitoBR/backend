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

/**
 * Representa um pedido realizado por um cliente em uma mesa de um estabelecimento.
 * Contém informações do cliente, mesa, itens do pedido, status e total.
 */
@Table(name = "pedido")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Pedido {

        /**
         * Identificador único do pedido.
         */
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long pedido_id;

        /**
         * Cliente que realizou o pedido. Obrigatório.
         */
        @ManyToOne
        @JoinColumn(name = "cliente_id", nullable = false)
        private Cliente cliente;

        /**
         * Usuário do dashboard que pode estar associado ao pedido (ex: atendente).
         */
        @ManyToOne
        @JoinColumn(name = "usuario_dashboard_id")
        private UsuarioDashboard usuarioDashboard;

        /**
         * Lista de itens pertencentes a esse pedido.
         * Cascade ALL garante que operações no pedido reflitam nos itens.
         * OrphanRemoval remove itens órfãos ao serem removidos da lista.
         */
        @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
        @JsonManagedReference
        private List<PedidoItem> itens = new ArrayList<>();

        /**
         * Valor total acumulado do pedido.
         */
        private BigDecimal total = BigDecimal.ZERO;

        /**
         * Mesa na qual o pedido foi realizado. Obrigatório.
         */
        @ManyToOne
        @JoinColumn(name = "mesa_id", nullable = false)
        private Mesa mesa;

        /**
         * Status atual do pedido (ex: ABERTO, CONFIRMADO, CANCELADO).
         */
        @Enumerated(EnumType.STRING)
        private StatusPedido status;

        /**
         * Construtor para criar um pedido novo, vinculando cliente e mesa.
         * Inicializa o status como ABERTO e uma lista vazia de itens.
         *
         * @param cliente Cliente que fez o pedido.
         * @param mesa Mesa onde o pedido foi realizado.
         */
        public Pedido(Cliente cliente, Mesa mesa) {
                this.cliente = cliente;
                this.mesa = mesa;
                this.status = StatusPedido.ABERTO;
                this.itens = new ArrayList<>();
        }

        /**
         * Calcula e atualiza o valor total do pedido somando preço unitário * quantidade de cada item.
         */
        public void calcularTotal() {
                this.total = itens.stream()
                        .map(item -> item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
}

