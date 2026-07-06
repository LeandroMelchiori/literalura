package com.alura.literalura.service;

import com.alura.literalura.exception.ExternalApiException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
public class ConsumoAPI {

    private final HttpClient client;

    public ConsumoAPI(HttpClient client) {
        this.client = client;
    }

    public String obtenerDatos(String url) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(15))
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                throw new ExternalApiException(
                        "La API de Gutendex respondió con estado " + response.statusCode(), null);
            }
            return response.body();
        } catch (IOException e) {
            throw new ExternalApiException("No se pudo conectar con la API de Gutendex.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ExternalApiException("La consulta a la API de Gutendex fue interrumpida.", e);
        }
    }
}
