
package ma.emsi.aichatbotservice.discord;

import ma.emsi.aichatbotservice.services.AiChatService;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DiscordBotListener extends ListenerAdapter {

    private final AiChatService aiChatService;
    private final long allowedGuildId;
    private final long allowedUserId;

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    public DiscordBotListener(
            AiChatService aiChatService,
            long allowedGuildId,
            long allowedUserId) {

        this.aiChatService = aiChatService;
        this.allowedGuildId = allowedGuildId;
        this.allowedUserId = allowedUserId;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        if (event.getAuthor().isBot()) {
            return;
        }

        if (!event.isFromGuild()) {
            return;
        }

        if (event.getGuild().getIdLong() != allowedGuildId) {
            return;
        }

        if (event.getAuthor().getIdLong() != allowedUserId) {
            return;
        }

        String content = event.getMessage().getContentRaw();

        if (!content.startsWith("!bank ")) {
            return;
        }

        String question = content.substring(6).trim();

        if (question.isBlank()) {
            return;
        }

        executor.submit(() -> {
            try {
                String answer = aiChatService.ask(question);

                if (answer == null || answer.isBlank()) {
                    answer = "Aucune réponse disponible.";
                }

                // Discord limite un message à 2000 caractères.
                if (answer.length() > 1900) {
                    answer = answer.substring(0, 1900)
                            + "\n...(réponse tronquée)";
                }

                event.getChannel()
                        .sendMessage(answer)
                        .queue();

            } catch (Exception e) {
                System.err.println(
                        "Erreur chatbot Discord : " + e.getMessage()
                );

                event.getChannel()
                        .sendMessage(
                                "Une erreur est survenue lors du traitement."
                        )
                        .queue();
            }
        });
    }
}
