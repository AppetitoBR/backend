package appetito.apicardapio.dto;

import appetito.apicardapio.entity.Cardapio;
import java.util.List;
import java.time.LocalDate;

public record DadosDetalhamentoCardapio(
        Long cardapio_id,
        int estabelecimento_id,
        String nome,
        String secao,
        String descricao,
        int colaborador_id,
        LocalDate vigencia_inicio,
        LocalDate vigencia_fim,
        boolean ativo,
        List<String> restricoesAlimentares
) {
    public DadosDetalhamentoCardapio(Cardapio cardapio) {
        this(
                cardapio.getCardapio_id(),
                cardapio.getEstabelecimento_id(),
                cardapio.getNome(),
                cardapio.getSecao(),
                cardapio.getDescricao(),
                cardapio.getColaborador_id(),
                cardapio.getVigencia_inicio(),
                cardapio.getVigencia_fim(),
                cardapio.isAtivo(),
                cardapio.getRestricoesAlimentares()
        );
    }
}
