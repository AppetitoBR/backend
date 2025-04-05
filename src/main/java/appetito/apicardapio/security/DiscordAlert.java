package appetito.apicardapio.security;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class DiscordAlert {
    public void AlertDiscord(String mensagem) {
        try {
            String webhookUrl = "https://discord.com/api/webhooks/1357876872247120064/Fb4qOOlj5AVQa-AwZDb5whMog0EWrhl8PyFF76cE2Gqw-G3EX1hp_AevsFdEKOt2BgyL";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(webhookUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString("{\"content\": \"" + mensagem + "\"}"))
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.discarding());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
