
package ma.emsi.mcpserverservice.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class BankingTools {

    private final RestClient restClient;

    public BankingTools(
            @Value("${banking.gateway.url:http://localhost:8080}")
            String gatewayUrl) {

        this.restClient = RestClient.builder()
                .baseUrl(gatewayUrl)
                .build();
    }

    @Tool(description = "Afficher la liste de tous les clients bancaires")
    public String getAllCustomers() {
        return restClient.get()
                .uri("/customers")
                .retrieve()
                .body(String.class);
    }

    @Tool(description = "Rechercher un client bancaire par son identifiant")
    public String getCustomerById(
            @ToolParam(description = "Identifiant du client")
            Long customerId) {

        return restClient.get()
                .uri("/customers/{id}", customerId)
                .retrieve()
                .body(String.class);
    }

    @Tool(description = "Afficher tous les comptes bancaires")
    public String getAllAccounts() {
        return restClient.get()
                .uri("/accounts")
                .retrieve()
                .body(String.class);
    }

    @Tool(description = "Afficher les comptes bancaires d'un client")
    public String getAccountsByCustomerId(
            @ToolParam(description = "Identifiant du client")
            Long customerId) {

        return restClient.get()
                .uri("/accounts/customer/{id}", customerId)
                .retrieve()
                .body(String.class);
    }

    @Tool(description = "Afficher les informations d'un compte bancaire avec les informations de son client")
    public String getAccountDetails(
            @ToolParam(description = "Identifiant du compte bancaire")
            String accountId) {

        return restClient.get()
                .uri("/accounts/{id}/details", accountId)
                .retrieve()
                .body(String.class);
    }
}
