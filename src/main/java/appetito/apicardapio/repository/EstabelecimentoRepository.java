package appetito.apicardapio.repository;

import appetito.apicardapio.entity.Estabelecimento;
import appetito.apicardapio.entity.UsuarioDashboard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EstabelecimentoRepository extends JpaRepository<Estabelecimento, Long> {
    List<Estabelecimento> findAllByUsuarioCadastro(UsuarioDashboard usuarioCadastro);

    List<Estabelecimento> findByNomeFantasiaContainingIgnoreCase(String nomeFantasia);

    boolean existsByUsuarioCadastro(UsuarioDashboard usuarioDashboard);

    List<Estabelecimento> findByEstabelecimentoId(Long estabelecimento_id);
}