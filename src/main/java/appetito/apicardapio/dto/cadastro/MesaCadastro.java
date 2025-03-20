package appetito.apicardapio.dto.cadastro;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MesaCadastro(
        @NotBlank String nome,
        @NotNull Integer capacidade,
        @NotBlank String status,
        @NotBlank String qrCode,
        @NotNull Long estabelecimento_id
) {}