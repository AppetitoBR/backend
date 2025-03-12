package appetito.apicardapio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CardapioCadastro(
        @NotBlank String nome,
        String secao,
        String descricao,
        @NotNull Long estabelecimento_id,
        Long colaborador_id,
        @NotNull LocalDate vigencia_inicio,
        @NotNull LocalDate vigencia_fim
) {}