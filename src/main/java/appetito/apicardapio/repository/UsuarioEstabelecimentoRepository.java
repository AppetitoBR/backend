package appetito.apicardapio.repository;
import appetito.apicardapio.entity.Estabelecimento;
import appetito.apicardapio.entity.UsuarioDashboard;
import appetito.apicardapio.entity.UsuarioEstabelecimento;
import appetito.apicardapio.enums.PapelUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


public interface UsuarioEstabelecimentoRepository extends JpaRepository<UsuarioEstabelecimento, Long> {
    List<UsuarioEstabelecimento> findAllByUsuario(UsuarioDashboard usuario);
    boolean existsByUsuario(UsuarioDashboard usuarioDashboard);
    boolean existsByUsuarioAndEstabelecimento(UsuarioDashboard usuario, Estabelecimento estabelecimento);
    Optional<UsuarioEstabelecimento> findByUsuarioAndEstabelecimento(UsuarioDashboard funcionario, Estabelecimento estabelecimento);
    List<UsuarioEstabelecimento> findAllByEstabelecimento(Estabelecimento estabelecimento);
}
