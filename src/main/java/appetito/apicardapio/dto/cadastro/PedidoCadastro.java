package appetito.apicardapio.dto.cadastro;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PedidoCadastro(
        @NotNull List<ItemPedidoCadastro> itens
) {}