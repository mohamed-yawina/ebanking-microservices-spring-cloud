
package ma.emsi.aichatbotservice.services;

import io.modelcontextprotocol.client.McpSyncClient;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiChatService {

    private final ChatClient chatClient;

    public AiChatService(
            ChatClient.Builder builder,
            List<McpSyncClient> mcpClients) {

        SyncMcpToolCallbackProvider provider =
                new SyncMcpToolCallbackProvider(mcpClients);

        ToolCallback[] tools = provider.getToolCallbacks();

        System.out.println("===== MCP TOOLS =====");
        System.out.println("Nombre d'outils MCP : " + tools.length);

        for (ToolCallback tool : tools) {
            System.out.println("Outil : " + tool.getToolDefinition().name());
        }

        this.chatClient = builder
                .defaultSystem("""
                    Tu es un assistant bancaire intelligent
                    pour une application E-Banking.
                    Réponds toujours en français.
                    Utilise les outils MCP pour obtenir les informations
                    sur les clients et les comptes bancaires.
                    N'invente jamais de clients, de comptes ou de soldes.
                    Ne suppose pas la devise si elle n'est pas précisée.
                    Tu ne peux pas effectuer de transactions.
                    """)
                .defaultToolCallbacks(tools)
                .build();
    }

    public String ask(String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }
}
