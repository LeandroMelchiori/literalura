package com.alura.literalura.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class AppConfig {

    /**
     * Cliente HTTP compartido. Se crea una sola vez como bean en lugar de
     * instanciarlo en cada llamada a la API externa.
     */
    @Bean
    public HttpClient httpClient() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                // Respeta la configuración de proxy del sistema (http.proxyHost/https.proxyHost);
                // usa conexión directa cuando no hay proxy definido.
                .proxy(ProxySelector.getDefault())
                .build();
    }

    @Bean
    public OpenAPI literaluraOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("LiteraLura API")
                        .description("API de gestión de biblioteca: catálogo del Proyecto "
                                + "Gutenberg (vía Gutendex), ejemplares, socios y préstamos. "
                                + "Los endpoints protegidos requieren JWT (login en /api/auth/login).")
                        .version("2.0.0")
                        .license(new License().name("MIT")))
                // Habilita el botón "Authorize" de Swagger UI para pegar el JWT.
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
