package appetito.apicardapio.exception;

import appetito.apicardapio.security.DiscordAlert;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.validation.FieldError;

/**
 * Manipulador global de exceções para a API, que intercepta erros lançados durante
 * o processamento das requisições HTTP e retorna respostas apropriadas para o cliente.
 *
 * <p>Também envia alertas para um canal do Discord em caso de erros inesperados.</p>
 */
@RestControllerAdvice
public class CustomExceptionHandler {

    private final DiscordAlert discordAlert;

    /**
     * Construtor que injeta a dependência do serviço de alertas no Discord.
     *
     * @param discordAlert serviço para envio de mensagens de alerta no Discord
     */
    public CustomExceptionHandler(DiscordAlert discordAlert) {
        this.discordAlert = discordAlert;
    }

    /**
     * Trata exceções do tipo {@link ResourceNotFoundException}, retornando
     * status HTTP 404 (Not Found) com uma mensagem detalhada.
     *
     * @param ex a exceção lançada indicando que um recurso não foi encontrado
     * @param request contexto da requisição web que causou a exceção
     * @return resposta HTTP com status 404 e detalhes do erro
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public final ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse("Resource Not Found", ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Trata exceções do tipo {@link AccessDeniedException}, retornando
     * status HTTP 403 (Forbidden) quando o acesso ao recurso é negado.
     *
     * @param ex a exceção lançada indicando falta de permissão
     * @param request contexto da requisição web que causou a exceção
     * @return resposta HTTP com status 403 e detalhes do erro
     */
    @ExceptionHandler(AccessDeniedException.class)
    public final ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse("Access Denied", ex.getMessage(), HttpStatus.FORBIDDEN.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    /**
     * Trata exceções de validação de argumentos do método, normalmente
     * disparadas quando parâmetros anotados com {@code @Valid} falham na validação.
     *
     * @param ex a exceção contendo erros de validação dos campos
     * @return resposta HTTP com status 400 (Bad Request) e mapa de campos com mensagens de erro
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * Trata todas as outras exceções não específicas, enviando um alerta para o Discord
     * e retornando uma resposta HTTP 500 (Internal Server Error) com detalhes do erro.
     *
     * @param ex a exceção inesperada lançada durante o processamento da requisição
     * @param request contexto da requisição web que causou a exceção
     * @return resposta HTTP com status 500 e detalhes do erro
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex, WebRequest request) {
        String mensagemErro = "🚨 Erro inesperado na API:\n" + ex.getClass().getSimpleName() + ": " + ex.getMessage() + "\n";
        discordAlert.AlertDiscord(mensagemErro);

        ErrorResponse errorResponse = new ErrorResponse("Erro interno", ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}