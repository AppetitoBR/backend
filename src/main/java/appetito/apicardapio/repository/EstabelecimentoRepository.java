package appetito.apicardapio.repository;

import appetito.apicardapio.entity.Estabelecimento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EstabelecimentoRepository extends JpaRepository<Estabelecimento, Long> {
  //  Optional<Estabelecimento> findByCnpj(String cnpj);

    boolean existsById(Long id);

    Optional<Estabelecimento> findByNomeFantasia(String nomeFantasia);

}