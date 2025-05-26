package com.momenty.global.config.application;

import com.momenty.global.auth.oauth.apple.domain.AppleAuthProperty;
import java.util.List;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableConfigurationProperties(AppleAuthProperty.class)
public class ApplicationConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("https://appleid.apple.com");
        configuration.addAllowedMethod("POST");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);

        CorsConfiguration swaggerCorsConfig = new CorsConfiguration();
        swaggerCorsConfig.addAllowedOriginPattern("*");  // Swagger UI 허용
        swaggerCorsConfig.setAllowedMethods(List.of("*"));
        swaggerCorsConfig.setAllowedHeaders(List.of("*"));
        swaggerCorsConfig.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/auth/apple/callback", configuration);
        source.registerCorsConfiguration("/**", swaggerCorsConfig);
        return source;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
