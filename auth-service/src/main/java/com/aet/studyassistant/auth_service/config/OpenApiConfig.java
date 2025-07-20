package com.aet.studyassistant.auth_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.ExternalDocumentation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI authServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Auth Service API")
                        .description("Authentication and User Management Service for Study Assistant Platform. " +
                                    "This service handles user registration, login, JWT token management, and user profile operations.")
                        .version("1.0.0")
                        .termsOfService("https://studyassistant.com/terms")
                        .contact(new Contact()
                                .name("Study Assistant Team")
                                .email("support@studyassistant.com")
                                .url("https://studyassistant.com/contact"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .externalDocs(new ExternalDocumentation()
                        .description("Complete API Documentation")
                        .url("https://github.com/your-org/study-assistant/wiki"))
                .servers(List.of(
                        new Server().url("http://localhost:8083").description("Development server"),
                        new Server().url("https://auth-service:8083").description("Docker Compose server"),
                        new Server().url("https://api.studyassistant.com").description("Production server")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT Authorization header using the Bearer scheme. " +
                                           "Example: \"Authorization: Bearer {token}\".\n\n" +
                                           "To obtain a token:\n" +
                                           "1. Call POST /api/auth/login with valid credentials\n" +
                                           "2. Copy the 'token' from the response\n" +
                                           "3. Click 'Authorize' and enter: Bearer {your-token}")));
    }
}
