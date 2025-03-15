package appetito.apicardapio.dto;

import appetito.apicardapio.entity.Produto;
import java.math.BigDecimal;

public record ProdutoDetalhamento(
        Long produto_id,
        Long cardapio_id,
        String nome_curto,
        String nome_longo,
        String categoria,
        String tamanho,
        BigDecimal preco_custo,
        BigDecimal preco_venda,
        Integer estoque,
        Integer estoque_minimo,
        Boolean ativo,
        String unidade_medida,
        String imagens
) {
    public ProdutoDetalhamento(Produto produto) {
        this(
                produto.getProduto_id(),
                produto.getCardapio().getId(),
                produto.getNome_curto(),
                produto.getNome_longo(),
                produto.getCategoria(),
                produto.getTamanho(),
                produto.getPreco_custo(),
                produto.getPreco_venda(),
                produto.getEstoque(),
                produto.getEstoque_minimo(),
                produto.getAtivo(),
                produto.getUnidade_medida(),
                produto.getImagens()
        );
    }
}