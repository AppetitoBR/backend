package appetito.apicardapio.repository;

import appetito.apicardapio.entity.Colaborador;
import java.util.List;

import appetito.apicardapio.entity.Estabelecimento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ColaboradorRepository extends JpaRepository<Colaborador, Long> {
    List<Colaborador> findByEstabelecimento(Estabelecimento estabelecimento);

}