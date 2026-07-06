package com.alura.literalura.dto;

/**
 * Estadísticas agregadas sobre los libros registrados en la base de datos.
 */
public record StatsDTO(
        long totalBooks,
        long totalAuthors,
        double averageDownloads,
        long maxDownloads,
        long minDownloads) {
}
