package com.alura.literalura.dto;

import com.alura.literalura.model.ReservationStatus;

import java.time.LocalDate;

public record ReservationDTO(
        Long id,
        Long bookId,
        String bookTitle,
        Long memberId,
        String memberName,
        LocalDate reservationDate,
        ReservationStatus status) {
}
