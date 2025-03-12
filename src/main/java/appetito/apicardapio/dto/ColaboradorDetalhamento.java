package appetito.apicardapio.dto;

import appetito.apicardapio.entity.Colaborador;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ColaboradorDetalhamento(
        Long colaborador_id,
        Long usuario_id,
        Long estabelecimento_id,
        String cargo,
        LocalDate data_contratacao,
        String calendario_trabalho,
        LocalDateTime inicio_turno,
        LocalDateTime termino_turno,
        String notificacoes
) {
    public ColaboradorDetalhamento(Colaborador colaborador) {
        this(
                colaborador.getColaborador_id(),
                colaborador.getUsuario().getUsuario_id(),
                colaborador.getEstabelecimento().getEstabelecimento_id(),
                colaborador.getCargo(),
                colaborador.getData_contratacao(),
                colaborador.getCalendario_trabalho(),
                colaborador.getInicio_turno(),
                colaborador.getTermino_turno(),
                colaborador.getNotificacoes()
        );
    }
}
