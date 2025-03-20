package appetito.apicardapio.dto.GetAll;

import appetito.apicardapio.entity.Estabelecimento;

public record EstabelecimentoDados(
        String nome_fantasia,
        String cnpj,
        String observacao
) {
    public EstabelecimentoDados(Estabelecimento estabelecimento) {
        this(
                estabelecimento.getNome_fantasia(),
                estabelecimento.getCnpj(),
                estabelecimento.getObservacao()
        );
    }
}
