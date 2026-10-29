
package ma.emsi.aichatbotservice.telegram;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class TelegramBotService {

    private final RestClient restClient;

    public TelegramBotService(
            @Value("${telegram.bot.token}") String token) {

        this.restClient = RestClient.builder()
                .baseUrl("https://api.telegram.org/bot" + token)
                .build();
    }

    public void sendMessage(Long chatId, String message) {
        restClient.post()
                .uri("/sendMessage")
                .body(Map.of(
                        "chat_id", chatId,
                        "text", message
                ))
                .retrieve()
                .toBodilessEntity();
    }
}
