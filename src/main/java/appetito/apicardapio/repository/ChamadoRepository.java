package appetito.apicardapio.repository;

import appetito.apicardapio.entity.Chamado;
import appetito.apicardapio.entity.Cliente;
import appetito.apicardapio.entity.Estabelecimento;
import appetito.apicardapio.enums.StatusChamado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChamadoRepository extends JpaRepository<Chamado, Long> {
    List<Chamado> findByStatusAndMesa_Estabelecimento(StatusChamado statusChamado, Estabelecimento estabelecimento);

    List<Chamado> findByCliente(Cliente cliente);
}
