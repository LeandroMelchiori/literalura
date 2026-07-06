package com.alura.literalura.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CopyRequest(
        @NotNull(message = "El id del título es obligatorio") Long bookId,
        @NotBlank(message = "El código de inventario es obligatorio") String inventoryCode) {
}
