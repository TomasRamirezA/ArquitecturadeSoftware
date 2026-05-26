package edu.eci.arsw.parallelism.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;

/**
 * OpenAPI configuration that customizes the generated API information shown
 * in Swagger UI (title, version, description, contact).
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI piOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Pi Digits API")
                        .version("1.0.0")
                        .description("API para calcular dígitos hexadecimales de Pi. Endpoint: /api/v1/pi/digits")
                        .contact(new Contact().name("Equipo ARSW").email("arsw@example.com"))
                );
    }

}
