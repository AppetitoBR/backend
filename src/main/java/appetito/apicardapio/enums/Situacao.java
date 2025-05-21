package appetito.apicardapio.enums;
/**
 * Enumeração que representa os possíveis estados ou situações de um recurso ou entidade no sistema.
 *
 * <p>Utilizado para indicar se um objeto está ativo, inativo ou bloqueado.</p>
 */
public enum Situacao {

    /**
     * Indica que o recurso está ativo e em uso normal.
     */
    ATIVO,

    /**
     * Indica que o recurso está inativo, não disponível para uso.
     */
    INATIVO,

    /**
     * Indica que o recurso está bloqueado, geralmente impedindo operações até desbloqueio.
     */
    BLOQUEADO
}