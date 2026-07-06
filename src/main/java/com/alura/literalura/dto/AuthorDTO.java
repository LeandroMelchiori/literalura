package com.alura.literalura.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AuthorDTO(
        String name,
        Integer birthYear,
        Integer deathYear) {
}
