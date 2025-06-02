package appetito.apicardapio.dto.put;

import java.math.BigDecimal;
public record ProdutoAtualizacao(
        Long produtoId,
        Long cardapioId,
        String nomeCurto,
        String nomeLongo,
        String categoria,
        String tamanho,
        BigDecimal precoCusto,
        BigDecimal precoVenda,
        Integer estoque,
        Integer estoqueMinimo,
        Boolean ativo,
        String unidadeMedida,
        byte[] imagens
) {
}