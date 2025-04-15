package appetito.apicardapio.dto.cadastro;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ProdutoCadastro(
        @NotNull Long cardapio,
        @NotBlank String nome_curto,
        @NotBlank String nome_longo,
        @NotBlank String categoria,
        @NotBlank String tamanho,
        @NotNull BigDecimal preco_custo,
        @NotNull BigDecimal preco_venda,
        @NotNull Integer estoque,
        @NotNull Integer estoque_minimo,
        @NotNull Boolean ativo,
        @NotBlank String unidade_medida
) {}