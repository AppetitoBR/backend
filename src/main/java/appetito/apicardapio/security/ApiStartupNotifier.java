package appetito.apicardapio.security;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ApiStartupNotifier {

    private final DiscordAlert discordAlert;

    public ApiStartupNotifier(DiscordAlert discordAlert) {
        this.discordAlert = discordAlert;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void notificarStartup() {
        String mensagem = "✅ API Online";
        discordAlert.AlertDiscord(mensagem);
    }
}