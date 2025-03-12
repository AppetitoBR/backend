package appetito.apicardapio.dto;
import appetito.apicardapio.entity.Mesa;
public record MesaDetalhamento(
        Long id,
        String nome,
        Integer capacidade,
        String status,
        String qrCode,
        Long estabelecimentoId
) {
    public MesaDetalhamento(Mesa mesa) {
        this(
                mesa.getId(),
                mesa.getNome(),
                mesa.getCapacidade(),
                mesa.getStatus(),
                mesa.getQrCode(),
                mesa.getEstabelecimento().getId()
        );
    }
}
