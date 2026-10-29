
package ma.emsi.mcpserverservice.config;

import ma.emsi.mcpserverservice.tools.BankingTools;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpToolsConfig {

    @Bean
    public ToolCallbackProvider bankingToolCallbackProvider(
            BankingTools bankingTools) {

        return MethodToolCallbackProvider.builder()
                .toolObjects(bankingTools)
                .build();
    }
}
