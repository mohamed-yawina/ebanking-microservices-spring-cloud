
package ma.emsi.aichatbotservice.controllers;

import ma.emsi.aichatbotservice.services.AiChatService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final AiChatService aiChatService;

    public ChatController(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    @GetMapping
    public String chat(@RequestParam String message) {
        return aiChatService.ask(message);
    }
}
