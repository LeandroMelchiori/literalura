package com.alura.literalura.dto;

import com.alura.literalura.model.MemberStatus;

import java.time.LocalDate;

public record MemberDTO(
        Long id,
        String name,
        String email,
        String documentId,
        MemberStatus status,
        LocalDate registeredAt) {
}
