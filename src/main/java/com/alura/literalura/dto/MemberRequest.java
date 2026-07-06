package com.alura.literalura.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record MemberRequest(
        @NotBlank(message = "El nombre es obligatorio") String name,
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no es válido") String email,
        @NotBlank(message = "El documento es obligatorio") String documentId) {
}
