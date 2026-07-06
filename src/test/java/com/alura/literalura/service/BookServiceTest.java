package com.alura.literalura.service;

import com.alura.literalura.dto.BookDTO;
import com.alura.literalura.dto.StatsDTO;
import com.alura.literalura.exception.BookNotFoundException;
import com.alura.literalura.model.Author;
import com.alura.literalura.model.Book;
import com.alura.literalura.repository.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository repository;
    @Mock
    private AuthorService authorService;
    @Mock
    private ConsumoAPI consumoAPI;
    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private BookService bookService;

    private static final String JSON_ORGULLO = """
            {
              "results": [
                {
                  "title": "Pride and Prejudice",
                  "authors": [ { "name": "Austen, Jane", "birth_year": 1775, "death_year": 1817 } ],
                  "languages": ["en"],
                  "download_count": 12345
                }
              ]
            }
            """;

    private static final String JSON_VACIO = "{ \"results\": [] }";

    private Author austen;

    @BeforeEach
    void setUp() {
        austen = new Author("Austen, Jane", 1775, 1817);
    }

    @Test
    void buscarYRegistrar_libroNuevo_loGuardaYDevuelve() {
        when(consumoAPI.obtenerDatos(anyString())).thenReturn(JSON_ORGULLO);
        when(repository.findByTitle("Pride and Prejudice")).thenReturn(Optional.empty());
        when(authorService.guardarAutor(any(Author.class))).thenReturn(austen);
        when(repository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

        BookDTO result = bookService.buscarYRegistrarLibro("pride");

        assertThat(result.title()).isEqualTo("Pride and Prejudice");
        assertThat(result.author().name()).isEqualTo("Austen, Jane");
        assertThat(result.downloadCount()).isEqualTo(12345);
        verify(repository).save(any(Book.class));
    }

    @Test
    void buscarYRegistrar_sinResultados_lanzaBookNotFound() {
        when(consumoAPI.obtenerDatos(anyString())).thenReturn(JSON_VACIO);

        assertThatThrownBy(() -> bookService.buscarYRegistrarLibro("inexistente"))
                .isInstanceOf(BookNotFoundException.class);

        verify(repository, never()).save(any());
    }

    @Test
    void buscarYRegistrar_libroExistente_noGuardaDeNuevo() {
        Book existente = new Book("Pride and Prejudice", austen, "en", 12345);
        when(consumoAPI.obtenerDatos(anyString())).thenReturn(JSON_ORGULLO);
        when(repository.findByTitle("Pride and Prejudice")).thenReturn(Optional.of(existente));

        BookDTO result = bookService.buscarYRegistrarLibro("pride");

        assertThat(result.title()).isEqualTo("Pride and Prejudice");
        verify(repository, never()).save(any());
    }

    @Test
    void obtenerEstadisticas_calculaAgregados() {
        List<Book> libros = List.of(
                new Book("A", austen, "en", 100),
                new Book("B", austen, "en", 300));
        when(repository.findAll()).thenReturn(libros);
        when(authorService.contarAutores()).thenReturn(1L);

        StatsDTO stats = bookService.obtenerEstadisticas();

        assertThat(stats.totalBooks()).isEqualTo(2);
        assertThat(stats.totalAuthors()).isEqualTo(1);
        assertThat(stats.averageDownloads()).isEqualTo(200.0);
        assertThat(stats.maxDownloads()).isEqualTo(300);
        assertThat(stats.minDownloads()).isEqualTo(100);
    }

    @Test
    void obtenerEstadisticas_sinLibros_devuelveCeros() {
        when(repository.findAll()).thenReturn(List.of());
        when(authorService.contarAutores()).thenReturn(0L);

        StatsDTO stats = bookService.obtenerEstadisticas();

        assertThat(stats.totalBooks()).isZero();
        assertThat(stats.averageDownloads()).isZero();
    }
}
