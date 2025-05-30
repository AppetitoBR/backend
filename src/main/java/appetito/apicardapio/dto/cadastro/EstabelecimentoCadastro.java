package appetito.apicardapio.dto.cadastro;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EstabelecimentoCadastro(
        @NotBlank String razao_social,
        @NotBlank String nome_fantasia,
        String cnpj,
        String endereco,
        @NotBlank String tipo,
        @NotBlank String segmento
) {}
