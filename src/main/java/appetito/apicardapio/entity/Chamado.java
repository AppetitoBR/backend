package appetito.apicardapio.entity;

import appetito.apicardapio.enums.StatusChamado;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "chamado")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Chamado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chamado_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "mesa_id", nullable = false)
    private Mesa mesa;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "data_hora_abertura", updatable = false)
    private LocalDateTime dataHoraAbertura = LocalDateTime.now();

    @Column(name = "cliente_leu_qrcode")
    private Boolean clienteLeuQrcode = false;

    @Column(name = "atendente_leu_qrcode")
    private Boolean atendenteLeuQrcode = false;

    @Column(name = "data_hora_fechamento")
    private LocalDateTime dataHoraFechamento;

    @Column(name = "data_hora_atendimento")
    private LocalDateTime dataHoraAtendimento;

    @Column(name = "mensagem_adicional")
    private String mensagemAdicional;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusChamado status = StatusChamado.CHAMADO;
}