package com.alura.literalura.controller;

import com.alura.literalura.dto.AuthorDTO;
import com.alura.literalura.service.AuthorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/authors")
@Tag(name = "Autores", description = "Consulta de autores registrados")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @Operation(summary = "Lista todos los autores registrados")
    @GetMapping
    public List<AuthorDTO> listarTodos() {
        return authorService.obtenerTodosLosAutores();
    }

    @Operation(summary = "Lista autores vivos hasta un año determinado")
    @GetMapping("/alive")
    public List<AuthorDTO> vivosHasta(@RequestParam int year) {
        return authorService.obtenerAutoresVivosHasta(year);
    }
}
