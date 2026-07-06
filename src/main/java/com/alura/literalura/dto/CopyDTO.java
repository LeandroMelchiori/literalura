package com.alura.literalura.dto;

import com.alura.literalura.model.CopyStatus;

public record CopyDTO(
        Long id,
        Long bookId,
        String bookTitle,
        String inventoryCode,
        CopyStatus status) {
}
