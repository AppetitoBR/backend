package appetito.apicardapio.dto.detalhamento;
import appetito.apicardapio.entity.Estabelecimento;
import java.time.LocalDateTime;

public record EstabelecimentoDetalhamento(
        String razao_social,
        String nome_fantasia,
        String cnpj,
        String contatos,
        String endereco,
        String tipo,
        LocalDateTime data_cadastro,
        Long usuario_cadastro_id,
        LocalDateTime data_alteracao_cadastro,
        String observacao,
        String url_cardapio_digital,
        String subdominio_appetito
) {
    public EstabelecimentoDetalhamento(Estabelecimento estabelecimento) {
        this(
                estabelecimento.getRazao_social(),
                estabelecimento.getNomeFantasia(),
                estabelecimento.getCnpj(),
                estabelecimento.getContatos(),
                estabelecimento.getEndereco(),
                estabelecimento.getTipo(),
                estabelecimento.getData_cadastro(),
                estabelecimento.getUsuarioCadastro().getUsuario_dashboard_id(),
                estabelecimento.getData_alteracao_cadastro(),
                estabelecimento.getObservacao(),
                estabelecimento.getUrl_cardapio_digital(),
                estabelecimento.getSubdominio_appetito()
        );
    }
}