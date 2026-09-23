package com.inventoryapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI inventoryApiOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Inventory API")
                        .version("0.0.1")
                        .description("API de gestion d'un inventaire de produits avec alerte de stock bas"));
    }
}
