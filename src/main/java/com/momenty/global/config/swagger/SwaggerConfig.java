package com.momenty.global.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        String accessTokenScheme = "AccessToken";
        String refreshTokenScheme = "RefreshToken";

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList(accessTokenScheme)
                .addList(refreshTokenScheme);

        Components components = new Components()
                .addSecuritySchemes(accessTokenScheme, new SecurityScheme()
                        .name("access_token")
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.COOKIE)
                )
                .addSecuritySchemes(refreshTokenScheme, new SecurityScheme()
                        .name("refresh_token")
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.COOKIE)
                );

        return new OpenAPI()
                .components(components)
                .addSecurityItem(securityRequirement)
                .info(new Info()
                        .title("Momenty")
                        .description("Swagger UI 설정 - JWT 쿠키 방식")
                        .version("1.0.0"));
    }
}
