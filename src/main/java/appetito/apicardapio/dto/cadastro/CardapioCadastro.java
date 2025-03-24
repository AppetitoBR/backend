package appetito.apicardapio.dto.cadastro;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CardapioCadastro(
        @NotBlank String nome,
        @NotNull Long estabelecimento_id,
        @NotNull Long usuario_id,
        String secao,
        String descricao,
        @NotNull LocalDate vigencia_inicio,
        @NotNull LocalDate vigencia_fim
) {}
