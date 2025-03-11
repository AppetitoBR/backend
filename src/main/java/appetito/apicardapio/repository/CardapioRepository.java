package appetito.apicardapio.repository;

import appetito.apicardapio.entity.Cardapio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CardapioRepository extends JpaRepository<Cardapio, Long> {

}