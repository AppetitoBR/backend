package appetito.apicardapio.dto;

import appetito.apicardapio.entity.Colaborador;
import appetito.apicardapio.entity.Estabelecimento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CardapioCadastro(
        @NotBlank String nome,
        @NotBlank Estabelecimento estabelecimento_id,
        String secao,
        String descricao,
        @NotNull Long id,
        @NotBlank Colaborador colaborador_id,
        @NotNull LocalDate vigencia_inicio,
        @NotNull LocalDate vigencia_fim
) {}
