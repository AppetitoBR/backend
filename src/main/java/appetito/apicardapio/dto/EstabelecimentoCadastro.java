package appetito.apicardapio.dto;

import jakarta.validation.constraints.NotBlank;

public record EstabelecimentoCadastro(
        @NotBlank String razao_social,
        @NotBlank String nome_fantasia,
        @NotBlank String cnpj,
        @NotBlank String tipo
) {}
