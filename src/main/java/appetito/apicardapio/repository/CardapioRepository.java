package appetito.apicardapio.repository;

import appetito.apicardapio.entity.Cardapio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardapioRepository extends JpaRepository<Cardapio, Long> {
    Optional<Cardapio> findById(Long id);
    List<Cardapio> findByEstabelecimento(Long estabelecimentoId);
    Optional<Cardapio> findByIdAndEstabelecimento(Long cardapioId, Long estabelecimentoId);
}