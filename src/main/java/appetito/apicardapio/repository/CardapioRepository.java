package appetito.apicardapio.repository;

import appetito.apicardapio.entity.Cardapio;
import appetito.apicardapio.entity.Estabelecimento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardapioRepository extends JpaRepository<Cardapio, Long> {
    Optional<Cardapio> findById(Long id);

    List<Cardapio> findByEstabelecimento(Estabelecimento estabelecimento);
    Optional<Cardapio> findByIdAndEstabelecimento(Long cardapioId, Estabelecimento estabelecimento);

    List<Cardapio> findByEstabelecimentoNomeFantasiaIgnoreCase(String nomeFantasia);

    List<Cardapio> findByEstabelecimentoNomeFantasia(String nomeFantasia);
}