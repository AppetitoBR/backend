package appetito.apicardapio.dto.cadastro;

import jakarta.validation.constraints.NotBlank;

public record ClienteCadastro(
        @NotBlank String nome,
        @NotBlank String email,
        @NotBlank String senha
) {

}
