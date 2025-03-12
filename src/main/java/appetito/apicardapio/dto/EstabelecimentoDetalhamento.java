package appetito.apicardapio.dto;
import appetito.apicardapio.entity.Estabelecimento;

import java.time.LocalDateTime;

public record EstabelecimentoDetalhamento(
        Long id,
        String razao_social,
        String nomeFantasia,
        String cnpj,
        String tipo,
        Boolean ativo,
        LocalDateTime data_cadastro
) {
    public EstabelecimentoDetalhamento(Estabelecimento estabelecimento) {
        this(
                estabelecimento.getId(),
                estabelecimento.getRazao_social(),
                estabelecimento.getNomeFantasia(),
                estabelecimento.getCnpj(),
                estabelecimento.getTipo(),
                estabelecimento.getAtivo(),
                estabelecimento.getData_cadastro()
        );
    }
}