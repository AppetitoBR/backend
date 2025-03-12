package appetito.apicardapio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EstabelecimentoCadastro(
        @NotBlank String razao_social,
        @NotBlank String nome_fantasia,
        @NotBlank String cnpj,
        @NotBlank String tipo,
        @NotBlank String segmento,
        @NotNull Long usuario_cadastro_id
) {}
