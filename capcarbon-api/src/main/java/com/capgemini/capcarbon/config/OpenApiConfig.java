package com.capgemini.capcarbon.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * Configuration OpenAPI / Swagger UI.
 * Ajoute le support du token JWT directement dans l'interface Swagger
 * pour tester les endpoints sécurisés sans outil externe.
 */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI capCarbonOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CapCarbon API")
                        .description("""
                                **API REST de calcul d'empreinte carbone** pour infrastructures physiques.
                                
                                Développée dans le cadre du Hackathon Capgemini.
                                
                                ## Authentification
                                Utilisez `POST /auth/login` pour obtenir un token JWT,
                                puis cliquez sur **Authorize** et collez `Bearer <votre_token>`.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Équipe CapCarbon — Capgemini")
                                .email("capcarbon@capgemini.com")))
                // Schéma de sécurité JWT Bearer
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Entrez votre token JWT obtenu via POST /auth/login")));
    }
}
