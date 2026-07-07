package com.alura.literalura.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Alta de socio realizada por el personal: incluye las credenciales de acceso
 * del cliente, que el bibliotecario define al validar a la persona.
 */
public record MemberRequest(
        @NotBlank(message = "El nombre es obligatorio") String name,
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no es válido") String email,
        @NotBlank(message = "El documento es obligatorio") String documentId,
        @NotBlank(message = "El usuario es obligatorio") String username,
        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres") String password) {
}
