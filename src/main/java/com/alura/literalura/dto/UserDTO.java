package com.alura.literalura.dto;

import com.alura.literalura.model.UserRole;

// Nunca expone la contraseña, ni siquiera hasheada.
public record UserDTO(
        Long id,
        String username,
        UserRole role) {
}
