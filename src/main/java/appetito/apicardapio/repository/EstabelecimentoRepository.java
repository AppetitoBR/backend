package appetito.apicardapio.repository;

import appetito.apicardapio.entity.Cardapio;
import appetito.apicardapio.entity.Estabelecimento;
import appetito.apicardapio.entity.UsuarioDashboard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EstabelecimentoRepository extends JpaRepository<Estabelecimento, Long> {
    List<Estabelecimento> findByNomeFantasiaContainingIgnoreCase(String nomeFantasia);
    Optional<Estabelecimento> findByNomeFantasia(String nomeFantasia);
}