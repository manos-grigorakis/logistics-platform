package com.manosgrigorakis.logisticsplatform.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes("Bearer Authentication", bearerAuthScheme())
                );
    }

    private Info apiInfo() {
        return new Info()
                .title("Logistics Platform API")
                .version("1.0")
                .description("API Documentation for Logistics Platform")
                .contact(new Contact()
                        .name("Manos Grigorakis")
                        .url("https://manosgrigorakis.com")
                        .email("contact@manosgrigorakis.com"));
    }

    private SecurityScheme bearerAuthScheme() {
        return new SecurityScheme()
                .name("Bearer Authentication")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");
    }
}
