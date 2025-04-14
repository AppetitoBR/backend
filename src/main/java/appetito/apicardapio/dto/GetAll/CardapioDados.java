package appetito.apicardapio.dto.GetAll;

import appetito.apicardapio.entity.Cardapio;

import java.util.List;
import java.util.stream.Collectors;


public record CardapioDados(
        Long estabelecimento_id,
        String nome,
        String descricao,
        boolean ativo,
        List<ProdutoDados> produtos
) {
    public CardapioDados(Cardapio cardapio) {
        this(
                cardapio.getEstabelecimento().getEstabelecimentoId(),
                cardapio.getNome(),
                cardapio.getDescricao(),
                cardapio.getAtivo(),
                cardapio.getProdutos().stream()
                        .map(ProdutoDados::new)
                        .collect(Collectors.toList())
        );
    }
}
