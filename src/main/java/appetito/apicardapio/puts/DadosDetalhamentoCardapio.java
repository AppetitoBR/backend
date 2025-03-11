package appetito.apicardapio.puts;

import appetito.apicardapio.entity.Cardapio;

import java.time.LocalDate;

public record DadosDetalhamentoCardapio(
        Long id,
        int estabelecimento_id,
        String nome,
        String secao,
        String descricao,
        int colaborador_id,
        LocalDate vigencia_inicio,
        LocalDate vigencia_fim,
        boolean ativo
) {
    public DadosDetalhamentoCardapio(Cardapio cardapio) {
        this(
                cardapio.getId(),
                cardapio.getEstabelecimento_id(),
                cardapio.getNome(),
                cardapio.getSecao(),
                cardapio.getDescricao(),
                cardapio.getColaborador_id(),
                cardapio.getVigencia_inicio(),
                cardapio.getVigencia_fim(),
                cardapio.isAtivo()
        );
    }


}
