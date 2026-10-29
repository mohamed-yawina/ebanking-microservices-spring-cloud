
package ma.emsi.aichatbotservice.telegram;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import ma.emsi.aichatbotservice.services.AiChatService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class TelegramPollingService {

    private final RestClient restClient;
    private final TelegramBotService telegramBotService;
    private final AiChatService aiChatService;
    private final ObjectMapper objectMapper;
    private final Long allowedUserId;

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    private volatile boolean running = true;
    private long offset = 0;

    public TelegramPollingService(
            @Value("${telegram.bot.token}") String token,
            @Value("${telegram.bot.allowed-user-id}") Long allowedUserId,
            TelegramBotService telegramBotService,
            AiChatService aiChatService,
            ObjectMapper objectMapper) {

        this.restClient = RestClient.builder()
                .baseUrl("https://api.telegram.org/bot" + token)
                .build();

        this.allowedUserId = allowedUserId;
        this.telegramBotService = telegramBotService;
        this.aiChatService = aiChatService;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void start() {
        executor.submit(this::pollMessages);
    }

    private void pollMessages() {

        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                String response = restClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/getUpdates")
                                .queryParam("offset", offset)
                                .queryParam("timeout", 20)
                                .build())
                        .retrieve()
                        .body(String.class);

                JsonNode root = objectMapper.readTree(response);
                JsonNode updates = root.path("result");

                if (!updates.isArray()) {
                    continue;
                }

                for (JsonNode update : updates) {
                    offset = update.path("update_id").asLong() + 1;

                    JsonNode message = update.path("message");

                    if (message.isMissingNode()) {
                        continue;
                    }

                    long userId = message.path("from")
                            .path("id").asLong();

                    long chatId = message.path("chat")
                            .path("id").asLong();

                    String text = message.path("text").asText("");

                    if (userId != allowedUserId) {
                        telegramBotService.sendMessage(
                                chatId,
                                "Accès non autorisé."
                        );
                        continue;
                    }

                    if (text.isBlank()) {
                        continue;
                    }

                    if (text.equals("/start")) {
                        telegramBotService.sendMessage(
                                chatId,
                                "Bienvenue dans EBanking AI ! " +
                                        "Pose-moi une question sur les clients " +
                                        "ou les comptes bancaires."
                        );
                        continue;
                    }

                    try {
                        String answer = aiChatService.ask(text);

                        telegramBotService.sendMessage(
                                chatId,
                                answer == null || answer.isBlank()
                                        ? "Aucune réponse disponible."
                                        : answer
                        );

                    } catch (Exception e) {
                        System.err.println(
                                "Erreur chatbot Telegram : " +
                                        e.getMessage()
                        );

                        telegramBotService.sendMessage(
                                chatId,
                                "Une erreur est survenue lors du " +
                                        "traitement de ta demande."
                        );
                    }
                }

            } catch (Exception e) {
                if (!running) {
                    break;
                }

                System.err.println(
                        "Erreur Telegram polling : " + e.getMessage()
                );

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException interrupted) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    @PreDestroy
    public void stop() {
        running = false;
        executor.shutdownNow();
    }
}
