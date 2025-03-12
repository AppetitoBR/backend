package appetito.apicardapio.dto;
import appetito.apicardapio.entity.Mesa;

import java.time.LocalDateTime;

public record MesaDetalhamento(
        Long mesa_id,
        String nome,
        Integer capacidade,
        String status,
        String qrCode,
        Long estabelecimento_id
) {
    public MesaDetalhamento(Mesa mesa) {
        this(
                mesa.getMesa_id(),
                mesa.getNome(),
                mesa.getCapacidade(),
                mesa.getStatus(),
                mesa.getQrCode(),
                mesa.getEstabelecimento().getId()
        );
    }
}
