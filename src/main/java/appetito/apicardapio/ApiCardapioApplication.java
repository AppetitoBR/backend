package appetito.apicardapio;

import appetito.apicardapio.entity.UsuarioDashboard;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
public class ApiCardapioApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiCardapioApplication.class, args);
    }
}
