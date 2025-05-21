package appetito.apicardapio.enums;

/**
 * Enumeração que representa os possíveis status de um pedido dentro do sistema.
 *
 * <p>Controla o estado do pedido desde a abertura até a confirmação ou cancelamento.</p>
 */
public enum StatusPedido {

    /**
     * Pedido foi criado e está aberto para alterações.
     */
    ABERTO,

    /**
     * Pedido foi confirmado e está em processamento.
     */
    CONFIRMADO,

    /**
     * Pedido foi cancelado e não será processado.
     */
    CANCELADO;
}
