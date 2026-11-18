package com.tienda.tienda_online.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI tiendaOnlineOpenAPI() {

        final String esquemaSeguridad = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("API Tienda Online")
                        .version("1.0")
                        .description("API REST para la gestión de una tienda online"))
                .addSecurityItem(new SecurityRequirement()
                        .addList(esquemaSeguridad))
                .components(new Components()
                        .addSecuritySchemes(esquemaSeguridad,
                                new SecurityScheme()
                                        .name(esquemaSeguridad)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
