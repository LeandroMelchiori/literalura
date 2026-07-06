package com.alura.literalura.dto;

import jakarta.validation.constraints.NotNull;

public record LoanRequest(
        @NotNull(message = "El id del ejemplar es obligatorio") Long copyId,
        @NotNull(message = "El id del socio es obligatorio") Long memberId) {
}
