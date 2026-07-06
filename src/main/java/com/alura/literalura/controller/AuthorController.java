package com.alura.literalura.controller;

import com.alura.literalura.dto.AuthorDTO;
import com.alura.literalura.service.AuthorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/authors")
@Tag(name = "Autores", description = "Consulta de autores registrados")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @Operation(summary = "Lista de forma paginada los autores registrados")
    @GetMapping
    public Page<AuthorDTO> listarTodos(@PageableDefault(size = 20) Pageable pageable) {
        return authorService.obtenerTodosLosAutores(pageable);
    }

    @Operation(summary = "Lista autores vivos hasta un año determinado")
    @GetMapping("/alive")
    public Page<AuthorDTO> vivosHasta(@RequestParam int year,
                                      @PageableDefault(size = 20) Pageable pageable) {
        return authorService.obtenerAutoresVivosHasta(year, pageable);
    }
}
