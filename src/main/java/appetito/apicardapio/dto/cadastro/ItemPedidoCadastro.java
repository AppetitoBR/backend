package appetito.apicardapio.dto.cadastro;

import appetito.apicardapio.entity.Produto;
import jakarta.validation.constraints.NotNull;

public record ItemPedidoCadastro(
        @NotNull Long produtoId,
        @NotNull Integer quantidade
) {}