
package ma.emsi.aichatbotservice.discord;

import jakarta.annotation.PreDestroy;
import ma.emsi.aichatbotservice.services.AiChatService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DiscordBotConfig {

    private final JDA jda;

    public DiscordBotConfig(
            AiChatService aiChatService,
            @Value("${discord.bot.token}") String token,
            @Value("${discord.bot.allowed-guild-id}") long guildId,
            @Value("${discord.bot.allowed-user-id}") long userId) {

        try {
            this.jda = JDABuilder.createDefault(token)
                    .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                    .addEventListeners(
                            new DiscordBotListener(
                                    aiChatService,
                                    guildId,
                                    userId
                            )
                    )
                    .build();

            System.out.println("Discord Bot : connexion initialisée");

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Impossible de démarrer le bot Discord", e
            );
        }
    }

    @PreDestroy
    public void shutdown() {
        if (jda != null) {
            jda.shutdown();
        }
    }
}
