package appetito.apicardapio.repository;

import appetito.apicardapio.entity.Estabelecimento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EstabelecimentoRepository extends JpaRepository<Estabelecimento, Long> {

    // Busca um estabelecimento pelo CNPJ
    Optional<Estabelecimento> findByCnpj(String cnpj);

    // Verifica se um estabelecimento existe pelo ID
    boolean existsById(Long id);

    // Busca um estabelecimento pelo nome fantasia (opcional)
    Optional<Estabelecimento> findByNomeFantasia(String nomeFantasia);
}