package appetito.apicardapio.repository;
import appetito.apicardapio.entity.Estabelecimento;
import appetito.apicardapio.entity.UsuarioDashboard;
import appetito.apicardapio.entity.UsuarioEstabelecimento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface UsuarioEstabelecimentoRepository extends JpaRepository<UsuarioEstabelecimento, Long> {
    List<Estabelecimento> findByUsuario(UsuarioDashboard usuario);
    boolean existsByUsuario(UsuarioDashboard usuarioDashboard);
}
