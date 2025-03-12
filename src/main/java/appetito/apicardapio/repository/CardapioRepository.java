package appetito.apicardapio.repository;

import appetito.apicardapio.entity.Cardapio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CardapioRepository extends JpaRepository<Cardapio, Long> {
    @Query("SELECT c FROM Cardapio c WHERE c.estabelecimento.id = :estabelecimentoId")
    List<Cardapio> findByEstabelecimentoId(@Param("estabelecimento_id") Long estabelecimentoId);
}
