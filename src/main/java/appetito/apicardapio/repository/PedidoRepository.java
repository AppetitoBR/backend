package appetito.apicardapio.repository;

import appetito.apicardapio.entity.Pedido;
import appetito.apicardapio.enums.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
