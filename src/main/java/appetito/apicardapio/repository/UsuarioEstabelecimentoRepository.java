package appetito.apicardapio.repository;

import appetito.apicardapio.entity.UsuarioDashboard;
import appetito.apicardapio.entity.UsuarioEstabelecimento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioEstabelecimentoRepository extends JpaRepository<UsuarioEstabelecimento, Long> {
    UsuarioEstabelecimento findByUsuario(UsuarioDashboard usuario);
}
