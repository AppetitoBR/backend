package appetito.apicardapio.dto.GetAll;

import appetito.apicardapio.entity.Estabelecimento;

public record EstabelecimentoDados(
        String razao_social,
        String segmento,
        String observacao,
        String telefone,
        String endereco

) {
    public EstabelecimentoDados(Estabelecimento estabelecimento) {
        this(
                estabelecimento.getRazao_social(),
                estabelecimento.getSegmento(),
                estabelecimento.getObservacao(),
                estabelecimento.getContatos(),
                estabelecimento.getEndereco()
        );
    }
}
