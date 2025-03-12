package appetito.apicardapio.repository;

import appetito.apicardapio.entity.Colaborador;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ColaboradorRepository extends JpaRepository<Colaborador, Long> {
    List<Colaborador> findByEstabelecimento_Estabelecimento_id(Long estabelecimentoId);
}