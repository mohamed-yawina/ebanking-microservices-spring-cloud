
package ma.emsi.ebankservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ebankOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("EBank Service API")
                        .version("1.0")
                        .description(
                                "API REST pour la gestion des comptes bancaires"
                        ));
    }
}
