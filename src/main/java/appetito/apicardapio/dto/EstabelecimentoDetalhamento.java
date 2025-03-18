package appetito.apicardapio.dto;
import appetito.apicardapio.entity.Estabelecimento;

public record EstabelecimentoDetalhamento(
        Long estabelecimento_id,
        String razao_social
) {
    public EstabelecimentoDetalhamento(Estabelecimento estabelecimento) {
        this(
                estabelecimento.getId(),
                estabelecimento.getRazao_social()
        );
    }
}