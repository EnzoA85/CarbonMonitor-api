package com.capgemini.capcarbon.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Configuration CORS globale.
 * Autorise le frontend Angular (localhost:4200) et l'app mobile Expo dev (localhost:8081)
 * à dialoguer avec l'API sans être bloqués par le navigateur.
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Origines autorisées : Angular dev, Angular Docker, Expo Web dev
        config.setAllowedOrigins(List.of(
                "http://localhost:4200",   // Angular (dev local)
                "http://localhost:80",     // Angular (Docker)
                "http://localhost:8081",   // Expo Web (dev)
                "http://localhost:19006"   // Expo Web (autre port)
        ));

        // Méthodes HTTP autorisées
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // Headers autorisés (Authorization pour le JWT)
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "X-Requested-With"));

        // Expose le header Authorization pour que le client JS puisse le lire
        config.setExposedHeaders(List.of("Authorization", "Content-Disposition"));

        // Autorise l'envoi de cookies / credentials
        config.setAllowCredentials(true);

        // Durée de mise en cache du pre-flight (OPTIONS) en secondes
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
