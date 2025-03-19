package appetito.apicardapio.dto.forGet;

import appetito.apicardapio.entity.Produto;

import java.math.BigDecimal;

public record ProdutoDados(
        Long produto_id,
        String nome_curto,
        String categoria,
        String tamanho,
        BigDecimal preco_custo,
        boolean ativo,
        String imagens
                           ) {
    public ProdutoDados(Produto produto) {
      this(
              produto.getProduto_id(),
              produto.getNome_curto(),
              produto.getCategoria(),
              produto.getTamanho(),
              produto.getPreco_custo(),
              produto.getAtivo(),
              produto.getImagens());
    }
}
