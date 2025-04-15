package appetito.apicardapio.dto.GetAll;

import appetito.apicardapio.entity.Produto;

import java.math.BigDecimal;


public record ProdutoDados(
        Long produto_id,
        String nome_curto,
        String categoria,
        String tamanho,
        BigDecimal preco_custo,
        boolean ativo,
        String imagemUrl
) {
    public ProdutoDados(Produto produto) {
        this(
                produto.getProduto_id(),
                produto.getNome_curto(),
                produto.getCategoria(),
                produto.getTamanho(),
                produto.getPreco_custo(),
                produto.getAtivo(),
                "/produtos/" + produto.getProduto_id() + "/imagem"
        );
    }
}

