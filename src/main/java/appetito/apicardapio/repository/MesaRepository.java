package appetito.apicardapio.repository;

import appetito.apicardapio.entity.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MesaRepository extends JpaRepository<Mesa, Long> {
    Optional<Mesa> findByQrCode(String qrCode);
}