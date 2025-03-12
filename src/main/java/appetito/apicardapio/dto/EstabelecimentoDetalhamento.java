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
        Boolean bloqueado,
        LocalDateTime data_cadastro,
        String segmento,
        Long usuario_cadastro_id,
        Long usuario_alteracao_id,
        String observacao,
        String logomarca,
        String url_cardapio_digital,
        String subdominio_appetito
) {
    public EstabelecimentoDetalhamento(Estabelecimento estabelecimento) {
        this(
                estabelecimento.getId(),
                estabelecimento.getRazao_social(),
                estabelecimento.getNome_fantasia(),
                estabelecimento.getCnpj(),
                estabelecimento.getTipo(),
                estabelecimento.getAtivo(),
                estabelecimento.getBloqueado(),
                estabelecimento.getData_cadastro(),
                estabelecimento.getSegmento(),
                estabelecimento.getUsuario_cadastro().getUsuario_id(),
                estabelecimento.getUsuario_alteracao() != null ? estabelecimento.getUsuario_alteracao().getUsuario_id() : null,
                estabelecimento.getObservacao(),
                estabelecimento.getLogomarca(),
                estabelecimento.getUrl_cardapio_digital(),
                estabelecimento.getSubdominio_appetito()
        );
    }
}