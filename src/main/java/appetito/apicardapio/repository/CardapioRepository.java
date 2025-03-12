package appetito.apicardapio.repository;

import appetito.apicardapio.entity.Cardapio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CardapioRepository extends JpaRepository<Cardapio, Long> {
    List<Cardapio> findByEstabelecimentoId(Long id); // Corrigido
}