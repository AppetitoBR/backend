package appetito.apicardapio.repository;

import appetito.apicardapio.entity.Cliente;
import appetito.apicardapio.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByCliente(Cliente cliente);
    List<Pedido> findByMesa_Estabelecimento_EstabelecimentoId(Long estabelecimentoId);
}
