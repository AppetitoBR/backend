package appetito.apicardapio.entity;

import appetito.apicardapio.enums.StatusChamado;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Representa um chamado feito por um cliente em uma mesa do estabelecimento.
 * Contém informações sobre o cliente, mesa, status e horários relacionados ao atendimento.
 */
@Entity
@Table(name = "chamado")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Chamado {

    /**
     * Identificador único do chamado.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chamado_id")
    private Long id;

    /**
     * Mesa associada ao chamado.
     */
    @ManyToOne
    @JoinColumn(name = "mesa_id", nullable = false)
    private Mesa mesa;

    /**
     * Cliente que realizou o chamado.
     */
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    /**
     * Data e hora em que o chamado foi aberto.
     * Inicializado com o momento da criação da entidade e não é atualizável.
     */
    @Column(name = "data_hora_abertura", updatable = false)
    private LocalDateTime dataHoraAbertura = LocalDateTime.now();

    /**
     * Indica se o cliente leu o QR Code associado ao chamado.
     */
    @Column(name = "cliente_leu_qrcode")
    private Boolean clienteLeuQrcode = false;

    /**
     * Indica se o atendente leu o QR Code associado ao chamado.
     */
    @Column(name = "atendente_leu_qrcode")
    private Boolean atendenteLeuQrcode = false;

    /**
     * Data e hora em que o chamado foi fechado.
     */
    @Column(name = "data_hora_fechamento")
    private LocalDateTime dataHoraFechamento;

    /**
     * Data e hora em que o atendimento começou.
     */
    @Column(name = "data_hora_atendimento")
    private LocalDateTime dataHoraAtendimento;

    /**
     * Mensagem adicional informada pelo cliente no chamado.
     */
    @Column(name = "mensagem_adicional")
    private String mensagemAdicional;

    /**
     * Status atual do chamado, que pode ser CHAMADO, ATENDIDO ou CANCELADO.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusChamado status = StatusChamado.CHAMADO;

}
