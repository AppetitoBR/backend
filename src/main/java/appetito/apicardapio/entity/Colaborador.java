package appetito.apicardapio.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
public class Colaborador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long colaborador_id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "estabelecimento_id", nullable = false)
    private Estabelecimento estabelecimento;

    @Column(nullable = false)
    private String cargo;

    @Column(nullable = false)
    private LocalDate data_contratacao;

    @Lob
    private String calendario_trabalho;

    private LocalDateTime inicio_turno;

    private LocalDateTime termino_turno;

    @Lob
    private String notificacoes;

    public Colaborador() {}

    public Colaborador(Usuario usuario, Estabelecimento estabelecimento, String cargo, LocalDate data_contratacao) {
        this.usuario = usuario;
        this.estabelecimento = estabelecimento;
        this.cargo = cargo;
        this.data_contratacao = data_contratacao;
    }

    public void setDataContratacao(LocalDate data_contratacao) {
        this.data_contratacao = data_contratacao;
    }

    public void setCalendarioTrabalho(String segundaASexta) {
        this.calendario_trabalho = segundaASexta;
    }

    public void setInicioTurno(LocalDateTime inicio_turno) {
        this.inicio_turno = inicio_turno;
    }

    public void setTerminoTurno(LocalDateTime termino_turno) {
        this.termino_turno = termino_turno;
    }
}
