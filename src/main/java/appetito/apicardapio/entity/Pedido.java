package appetito.apicardapio.entity;

import appetito.apicardapio.dto.cadastro.PedidoCadastro;
import appetito.apicardapio.dto.detalhamento.ItemDetalhamento;
import appetito.apicardapio.dto.put.PedidoAtualizacao;
import appetito.apicardapio.enums.StatusPedido;
import appetito.apicardapio.exception.ResourceNotFoundException;
import appetito.apicardapio.repository.ProdutoRepository;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

        private Long cliente_id;

        @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
        @JsonManagedReference
        private List<PedidoItem> itens = new ArrayList<>();

        private BigDecimal total = BigDecimal.ZERO;


        @Enumerated(EnumType.STRING)
        private StatusPedido status = StatusPedido.ABERTO;

    public void calcularTotal() {
            this.total = itens.stream()
                    .map(item -> item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        public Pedido(Long cliente_id) {
                this.cliente_id = cliente_id;
        }
    }

