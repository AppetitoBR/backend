package appetito.apicardapio.dto.cadastro;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChamadoCadastro(
        @NotNull Long mesa_id,
        @NotBlank String mensagem
        ) {

}
