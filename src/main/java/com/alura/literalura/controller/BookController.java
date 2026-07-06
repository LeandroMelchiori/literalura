package com.alura.literalura.controller;

import com.alura.literalura.dto.BookDTO;
import com.alura.literalura.dto.StatsDTO;
import com.alura.literalura.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
@Validated
@Tag(name = "Libros", description = "Búsqueda, registro y consulta de libros")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @Operation(summary = "Busca un libro en Gutendex por título y lo registra")
    @PostMapping("/search")
    public ResponseEntity<BookDTO> buscarYRegistrar(
            @RequestParam @NotBlank(message = "El título no puede estar vacío") String title) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookService.buscarYRegistrarLibro(title));
    }

    @Operation(summary = "Lista de forma paginada los libros registrados")
    @GetMapping
    public Page<BookDTO> listarTodos(@PageableDefault(size = 20) Pageable pageable) {
        return bookService.obtenerTodosLosLibros(pageable);
    }

    @Operation(summary = "Lista libros filtrados por idioma (código ISO, ej: 'en', 'es')")
    @GetMapping("/language/{language}")
    public Page<BookDTO> listarPorIdioma(@PathVariable String language,
                                         @PageableDefault(size = 20) Pageable pageable) {
        return bookService.obtenerLibrosPorIdioma(language, pageable);
    }

    @Operation(summary = "Devuelve estadísticas agregadas de la biblioteca")
    @GetMapping("/stats")
    public StatsDTO estadisticas() {
        return bookService.obtenerEstadisticas();
    }
}
