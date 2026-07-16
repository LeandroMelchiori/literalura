package com.alura.literalura.controller;

import com.alura.literalura.dto.CopyDTO;
import com.alura.literalura.dto.CopyRequest;
import com.alura.literalura.model.CopyStatus;
import com.alura.literalura.service.CopyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/copies")
@Tag(name = "Ejemplares", description = "Copias físicas de los títulos del catálogo")
public class CopyController {

    private final CopyService copyService;

    public CopyController(CopyService copyService) {
        this.copyService = copyService;
    }

    @Operation(summary = "Registra un ejemplar físico para un título del catálogo")
    @PostMapping
    public ResponseEntity<CopyDTO> registrar(@Valid @RequestBody CopyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(copyService.registrarEjemplar(request));
    }

    @Operation(summary = "Lista ejemplares, opcionalmente filtrados por estado y/o título")
    @GetMapping
    public Page<CopyDTO> listar(@RequestParam(required = false) CopyStatus status,
                                @RequestParam(required = false) Long bookId,
                                @PageableDefault(size = 20) Pageable pageable) {
        return copyService.obtenerEjemplares(status, bookId, pageable);
    }

    @Operation(summary = "Obtiene un ejemplar por id")
    @GetMapping("/{id}")
    public CopyDTO obtener(@PathVariable Long id) {
        return copyService.obtenerEjemplar(id);
    }
}
