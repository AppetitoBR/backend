package appetito.apicardapio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ColaboradorCadastro(
        @NotNull Long usuario_id,
        @NotNull Long estabelecimento_id,
        @NotBlank String cargo,
        @NotNull LocalDate data_contratacao,
        String calendario_trabalho,
        LocalDateTime inicio_turno,
        LocalDateTime termino_turno,
        String notificacoes
) {}
