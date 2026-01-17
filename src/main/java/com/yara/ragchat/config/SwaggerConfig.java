package com.yara.ragchat.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    public static final String API_KEY_NAME = "X-API-KEY";

    @Bean
    public OpenAPI openAPI() {

        SecurityScheme apiKeyScheme = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name(API_KEY_NAME);

        return new OpenAPI()
                .info(new Info()
                        .title("RAG Chat Storage API")
                        .version("1.0")
                        .description("Chat session and message APIs"))
                .addSecurityItem(new SecurityRequirement().addList(API_KEY_NAME))
                .components(new Components()
                        .addSecuritySchemes(API_KEY_NAME, apiKeyScheme));
    }
}

