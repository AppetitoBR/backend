package appetito.apicardapio.dto;

import appetito.apicardapio.entity.Cardapio;

import java.time.LocalDate;

public record CardapioDetalhamento(
        Long cardapio_id,
        String nome,
        String secao,
        String descricao,
        Long estabelecimento_id,
        Long colaborador_id,
        LocalDate vigencia_inicio,
        LocalDate vigencia_fim,
        Boolean ativo
) {
    public CardapioDetalhamento(Cardapio cardapio) {
        this(
                cardapio.getCardapio_id(),
                cardapio.getNome(),
                cardapio.getSecao(),
                cardapio.getDescricao(),
                cardapio.getEstabelecimento().getId(),
                cardapio.getColaborador() != null ? cardapio.getColaborador().getColaborador_id() : null,
                cardapio.getVigencia_inicio(),
                cardapio.getVigencia_fim(),
                cardapio.getAtivo()
        );
    }
}