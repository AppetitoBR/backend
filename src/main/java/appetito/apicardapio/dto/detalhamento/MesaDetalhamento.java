package appetito.apicardapio.dto.detalhamento;
import appetito.apicardapio.entity.Mesa;

public record MesaDetalhamento(
        Long mesa_id,
        String nome,
        Integer capacidade,
        String status,
        byte[] qrcode,
        Long estabelecimento_id
) {
    public MesaDetalhamento(Mesa mesa) {
        this(
                mesa.getId(),
                mesa.getNome(),
                mesa.getCapacidade(),
                mesa.getStatus(),
                mesa.getQrcode(),
                mesa.getEstabelecimento().getEstabelecimento_id()
        );
    }
}
