package appetito.apicardapio.repository;

import appetito.apicardapio.entity.Cardapio;
import appetito.apicardapio.entity.Estabelecimento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardapioRepository extends JpaRepository<Cardapio, Long> {

    List<Cardapio> findByEstabelecimentoNomeFantasia(String nomeFantasia);


}