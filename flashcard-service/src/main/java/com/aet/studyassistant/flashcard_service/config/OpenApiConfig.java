package com.aet.studyassistant.flashcard_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI flashcardServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Flashcard Service API")
                        .description("Comprehensive flashcard and deck management service for the Study Assistant Platform. " +
                                    "This service enables users to create, organize, and manage flashcard decks for effective learning. " +
                                    "Features include deck creation, flashcard CRUD operations, user-specific content management, " +
                                    "and template-based default deck setup for new users.")
                        .version("1.0.0")
                        .termsOfService("https://studyassistant.com/terms")
                        .contact(new Contact()
                                .name("Study Assistant Team")
                                .email("support@studyassistant.com")
                                .url("https://studyassistant.com/contact"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8082").description("Development server"),
                        new Server().url("https://flashcard-service:8082").description("Docker Compose server"),
                        new Server().url("https://api.studyassistant.com").description("Production server")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT Authorization header using the Bearer scheme. Example: \"Authorization: Bearer {token}\"")));
    }
}
