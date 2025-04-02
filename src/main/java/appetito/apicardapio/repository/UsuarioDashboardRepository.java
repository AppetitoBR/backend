package appetito.apicardapio.repository;

import appetito.apicardapio.entity.UsuarioDashboard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioDashboardRepository extends JpaRepository<UsuarioDashboard, Long> {
    Optional<UsuarioDashboard> findByEmail(String email);

}

