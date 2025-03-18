package appetito.apicardapio.entity;

import appetito.apicardapio.dto.ColaboradorCadastro;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity(name = "Colaboradores")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Colaborador")
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

    public Colaborador(ColaboradorCadastro dadosColaborador) {
        this.usuario = dadosColaborador.usuario_id();
        this.estabelecimento = dadosColaborador.estabelecimento();
        this.cargo = dadosColaborador.cargo();
        this.data_contratacao = dadosColaborador.data_contratacao();
        this.calendario_trabalho = dadosColaborador.calendario_trabalho();
        this.inicio_turno = dadosColaborador.inicio_turno();
        this.termino_turno = dadosColaborador.termino_turno();
        this.notificacoes = dadosColaborador.notificacoes();

    }


    public void setDataContratacao(LocalDate now) {
    }

    public void setCalendarioTrabalho(String segundaASexta) {
    }

    public void setInicioTurno(LocalDateTime now) {
    }

    public void setTerminoTurno(LocalDateTime localDateTime) {

    }

    public void setNome(String colaboradorTeste) {

    }
}
