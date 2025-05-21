package appetito.apicardapio.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Representa a estrutura padrão da resposta de erro enviada pela API.
 *
 * <p>Contém informações básicas sobre o erro ocorrido, como o tipo, mensagem descritiva e código HTTP.</p>
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    /**
     * Tipo ou título do erro ocorrido (ex: "Resource Not Found", "Access Denied").
     */
    private String error;

    /**
     * Mensagem detalhada explicando o motivo do erro.
     */
    private String message;

    /**
     * Código HTTP correspondente ao erro (ex: 404, 403, 500).
     */
    private int status;
}
