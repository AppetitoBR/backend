package appetito.apicardapio.dto;
import appetito.apicardapio.entity.Estabelecimento;

public record EstabelecimentoDetalhamento(
        Long id,
        String razaoSocial,
        String nomeFantasia,
        String cnpj,
        String tipo,
        Boolean ativo
) {
    public EstabelecimentoDetalhamento(Estabelecimento estabelecimento) {
        this(
                estabelecimento.getId(),
                estabelecimento.getRazaoSocial(),
                estabelecimento.getNomeFantasia(),
                estabelecimento.getCnpj(),
                estabelecimento.getTipo(),
                estabelecimento.getAtivo()
        );
    }
}