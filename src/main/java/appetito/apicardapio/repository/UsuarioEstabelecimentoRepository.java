package appetito.apicardapio.repository;
import appetito.apicardapio.entity.Estabelecimento;
import appetito.apicardapio.entity.UsuarioDashboard;
import appetito.apicardapio.entity.UsuarioEstabelecimento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface UsuarioEstabelecimentoRepository extends JpaRepository<UsuarioEstabelecimento, Long> {
    List<UsuarioEstabelecimento> findAllByUsuario(UsuarioDashboard usuario);
    boolean existsByUsuario(UsuarioDashboard usuarioDashboard);
    boolean existsByUsuarioAndEstabelecimento(UsuarioDashboard usuario, Estabelecimento estabelecimento);
    List<UsuarioEstabelecimento> findAllByEstabelecimento(Estabelecimento est);
}
