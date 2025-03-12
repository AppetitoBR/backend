package appetito.apicardapio.dto;

import jakarta.validation.constraints.NotBlank;

public record EstabelecimentoCadastro(
        @NotBlank String razaoSocial,
        @NotBlank String nomeFantasia,
        @NotBlank String cnpj,
        @NotBlank String tipo
) {}
