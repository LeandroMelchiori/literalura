package com.alura.literalura.dto;

import jakarta.validation.constraints.NotNull;

public record ReservationRequest(
        @NotNull(message = "El id del título es obligatorio") Long bookId) {
}
