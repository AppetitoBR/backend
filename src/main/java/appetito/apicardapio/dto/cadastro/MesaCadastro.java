package appetito.apicardapio.dto.cadastro;

import appetito.apicardapio.entity.Estabelecimento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MesaCadastro(
        @NotBlank String nome,
        @NotNull Integer capacidade
) {}