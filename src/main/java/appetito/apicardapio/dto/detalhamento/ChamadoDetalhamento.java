package appetito.apicardapio.dto.detalhamento;


import appetito.apicardapio.entity.Chamado;
import appetito.apicardapio.enums.StatusChamado;

import java.time.LocalDateTime;

public record ChamadoDetalhamento(
        Long chamadoId,
        Long mesaId,
        Long usuarioId,
        LocalDateTime dataHoraAbertura,
        Boolean clienteLeuQrcode,
        Boolean atendenteLeuQrcode,
        LocalDateTime dataHoraFechamento,
        LocalDateTime dataHoraAtendimento,
        String mensagemAdicional,
        StatusChamado status
) {
    public ChamadoDetalhamento(Chamado chamado) {
        this(
                chamado.getId(),
                chamado.getMesa().getMesa_id(),
                chamado.getUsuario().getUsuario_id(),
                chamado.getDataHoraAbertura(),
                chamado.getClienteLeuQrcode(),
                chamado.getAtendenteLeuQrcode(),
                chamado.getDataHoraFechamento(),
                chamado.getDataHoraAtendimento(),
                chamado.getMensagemAdicional(),
                chamado.getStatus()
        );
    }
}