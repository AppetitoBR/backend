package appetito.apicardapio.dto.GetAll;

import appetito.apicardapio.entity.Estabelecimento;

import java.sql.Time;
import java.time.LocalTime;

public record EstabelecimentoDados(
        String razao_social,
        String segmento,
        String observacao,
        String telefone,
        String endereco,
        Float nota,
        Time abertura,
        Time fechamento,
        boolean isOpen

) {
    public EstabelecimentoDados(Estabelecimento estabelecimento) {
        this(
                estabelecimento.getRazao_social(),
                estabelecimento.getSegmento(),
                estabelecimento.getObservacao(),
                estabelecimento.getContatos(),
                estabelecimento.getEndereco(),
                estabelecimento.getNota(),
                estabelecimento.getAbertura(),
                estabelecimento.getFechamento(),
                estabelecimento.isOpen()
        );
    }
}
