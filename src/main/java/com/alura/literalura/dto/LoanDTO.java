package com.alura.literalura.dto;

import com.alura.literalura.model.LoanStatus;

import java.time.LocalDate;

public record LoanDTO(
        Long id,
        Long copyId,
        String inventoryCode,
        String bookTitle,
        Long memberId,
        String memberName,
        LocalDate loanDate,
        LocalDate dueDate,
        LocalDate returnDate,
        LoanStatus status,
        boolean overdue,
        int renewals) {
}
