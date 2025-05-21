package appetito.apicardapio.exception;
/**
 * Exceção customizada lançada quando um recurso solicitado não é encontrado.
 *
 * <p>Utilizada para indicar situações onde a busca por um recurso específico falhou,
 * resultando em um erro do tipo "Not Found" (404).</p>
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Construtor que aceita uma mensagem detalhada para o erro.
     *
     * @param message Mensagem explicativa sobre a exceção.
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}