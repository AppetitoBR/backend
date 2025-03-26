package appetito.apicardapio.dto.detalhamento;
import appetito.apicardapio.entity.Estabelecimento;
import java.time.LocalDateTime;

public record EstabelecimentoDetalhamento(
        Long estabelecimento_id,
        String razao_social,
        String nome_fantasia,
        String cnpj,
        String contatos,
        String endereco,
        String tipo,
        LocalDateTime data_cadastro,
        Long usuario_cadastro_id,
        LocalDateTime data_alteracao_cadastro,
        Long usuario_alteracao_id,
        String observacao,
        String logomarca,
        String url_cardapio_digital,
        String subdominio_appetito
) {
    public EstabelecimentoDetalhamento(Estabelecimento estabelecimento) {
        this(
                estabelecimento.getEstabelecimento_id(),
                estabelecimento.getRazao_social(),
                estabelecimento.getNome_fantasia(),
                estabelecimento.getCnpj(),
                estabelecimento.getContatos(),
                estabelecimento.getEndereco(),
                estabelecimento.getTipo(),
                estabelecimento.getData_cadastro(),
                estabelecimento.getUsuario_cadastro(),
                estabelecimento.getData_alteracao_cadastro(),
                estabelecimento.getUsuario_alteracao(),
                estabelecimento.getObservacao(),
                estabelecimento.getLogomarca(),
                estabelecimento.getUrl_cardapio_digital(),
                estabelecimento.getSubdominio_appetito()
        );
    }
}