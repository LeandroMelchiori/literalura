package com.alura.literalura.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BookDTO(
        Long id,
        String title,
        AuthorDTO author,
        List<String> languages,
        int downloadCount,
        long availableCopies) {
}
