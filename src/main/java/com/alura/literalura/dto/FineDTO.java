package com.alura.literalura.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FineDTO(
        Long id,
        Long memberId,
        String memberName,
        Long loanId,
        String bookTitle,
        BigDecimal amount,
        int daysLate,
        LocalDate createdAt,
        boolean paid) {
}
