package appetito.apicardapio.dto.put;

import appetito.apicardapio.dto.detalhamento.ItemDetalhamento;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ItemAtualizacao(
        @NotNull
        Long produto_id,
        @NotNull
        @Positive
        Integer quantidade
) {
}
