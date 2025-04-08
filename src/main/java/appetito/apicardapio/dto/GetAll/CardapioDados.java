package appetito.apicardapio.dto.GetAll;

import appetito.apicardapio.entity.Cardapio;

public record CardapioDados(
        Long estabelecimento_id,
        String nome,
        String descricao,
        boolean ativo
) {
    public CardapioDados(Cardapio cardapio) {
        this(
                cardapio.getEstabelecimento().getEstabelecimentoId(),
                cardapio.getNome(),
                cardapio.getDescricao(),
                cardapio.getAtivo()
        );
    }

}
