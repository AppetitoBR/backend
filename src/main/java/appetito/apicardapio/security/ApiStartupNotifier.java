package appetito.apicardapio.security;

import jakarta.annotation.PreDestroy;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Componente responsável por notificar eventos de ciclo de vida da API,
 * como inicialização e desligamento, através de mensagens no Discord.
 *
 * <p>
 * Utiliza o serviço {@link DiscordAlert} para enviar as notificações via webhook.
 * Isso permite monitoramento simples da disponibilidade da API em tempo real.
 * </p>
 */
@Component
public class ApiStartupNotifier {

    private final DiscordAlert discordAlert;

    /**
     * Construtor que injeta o serviço de alerta do Discord.
     *
     * @param discordAlert serviço responsável por enviar mensagens ao Discord
     */
    public ApiStartupNotifier(DiscordAlert discordAlert) {
        this.discordAlert = discordAlert;
    }

    /**
     * Método executado automaticamente quando a aplicação estiver pronta
     * (evento {@link org.springframework.boot.context.event.ApplicationReadyEvent}).
     * Envia uma notificação informando que a API está online.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void notificarStartup() {
        String mensagem = "✅ API Online";
        discordAlert.AlertDiscord(mensagem);
    }

    /**
     * Método executado automaticamente antes do encerramento da aplicação.
     * Envia uma notificação informando que a API está sendo desligada.
     */
    @PreDestroy
    public void notificarStop() {
        String mensagem = "❎ API Desligada";
        discordAlert.AlertDiscord(mensagem);
    }
}