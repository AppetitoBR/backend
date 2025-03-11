package appetito.apicardapio.posts;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CardapioCadastro(
        int id,
        int estabelecimento_id,
        @NotBlank @Size(min = 3, max = 30) String nome,
        @NotBlank String secao,
        @NotBlank String descricao,
        int colaborador_id,
        @FutureOrPresent
        LocalDate vigencia_inicio,
        @Future
        LocalDate vigencia_fim,
        boolean ativo) {

}
