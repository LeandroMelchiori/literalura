package com.alura.literalura.dto.gutendex;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** Respuesta paginada de la API de Gutendex. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record GutendexResponse(List<GutendexBook> results) {
}
