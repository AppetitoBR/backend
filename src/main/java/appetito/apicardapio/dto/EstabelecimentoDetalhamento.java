package appetito.apicardapio.dto;
import appetito.apicardapio.entity.Estabelecimento;

import java.time.LocalDateTime;

public record EstabelecimentoDetalhamento(
        Long estabelecimento_id,
        String razao_social,
        String nome_fantasia,
        String cnpj,
        String tipo,
        Boolean ativo,
        LocalDateTime data_cadastro
) {
    public EstabelecimentoDetalhamento(Estabelecimento estabelecimento) {
        this(
                estabelecimento.getEstabelecimento_id(),
                estabelecimento.getRazao_social(),
                estabelecimento.getNome_fantasia(),
                estabelecimento.getCnpj(),
                estabelecimento.getTipo(),
                estabelecimento.getAtivo(),
                estabelecimento.getData_cadastro()
        );
    }
}