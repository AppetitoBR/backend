package appetito.apicardapio.enums;

/**
 * Enumeração que representa os possíveis status de um chamado ou solicitação no sistema.
 *
 * <p>Define o ciclo de vida de um chamado desde sua abertura até seu encerramento ou cancelamento.</p>
 */
public enum StatusChamado {

    /**
     * Chamado foi aberto e está aguardando atendimento.
     */
    CHAMADO,

    /**
     * Chamado já foi atendido.
     */
    ATENDIDO,

    /**
     * Chamado foi cancelado e não será mais atendido.
     */
    CANCELADO;
}