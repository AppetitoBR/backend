package appetito.apicardapio.repository;

import appetito.apicardapio.entity.Usuario;
import appetito.apicardapio.entity.UsuarioEstabelecimento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsuarioEstabelecimentoRepository extends JpaRepository<UsuarioEstabelecimento, Long> {
    UsuarioEstabelecimento findByUsuario(Usuario usuario);
}
