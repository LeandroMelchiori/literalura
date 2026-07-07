package com.alura.literalura.controller;

import com.alura.literalura.dto.LoanDTO;
import com.alura.literalura.dto.LoanRequest;
import com.alura.literalura.model.LoanStatus;
import com.alura.literalura.service.CurrentUserService;
import com.alura.literalura.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@Tag(name = "Préstamos", description = "Registro de préstamos y devoluciones")
public class LoanController {

    private final LoanService loanService;
    private final CurrentUserService currentUser;

    public LoanController(LoanService loanService, CurrentUserService currentUser) {
        this.loanService = loanService;
        this.currentUser = currentUser;
    }

    @Operation(summary = "Registra un préstamo de un ejemplar a un socio")
    @PostMapping
    public ResponseEntity<LoanDTO> prestar(@Valid @RequestBody LoanRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(loanService.registrarPrestamo(request.copyId(), request.memberId()));
    }

    @Operation(summary = "Registra la devolución de un préstamo")
    @PostMapping("/{id}/return")
    public LoanDTO devolver(@PathVariable Long id) {
        return loanService.registrarDevolucion(id);
    }

    @Operation(summary = "Lista préstamos, opcionalmente filtrados por estado o socio")
    @GetMapping
    public Page<LoanDTO> listar(@RequestParam(required = false) LoanStatus status,
                                @RequestParam(required = false) Long memberId,
                                @PageableDefault(size = 20) Pageable pageable) {
        if (memberId != null) {
            return loanService.obtenerPrestamosPorSocio(memberId, pageable);
        }
        return loanService.obtenerPrestamos(status, pageable);
    }

    @Operation(summary = "Lista los préstamos vencidos")
    @GetMapping("/overdue")
    public List<LoanDTO> vencidos() {
        return loanService.obtenerVencidos();
    }

    @Operation(summary = "Préstamos del socio autenticado",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/mine")
    public Page<LoanDTO> misPrestamos(Authentication auth,
                                      @PageableDefault(size = 20) Pageable pageable) {
        return loanService.obtenerPrestamosPorSocio(currentUser.currentMember(auth).getId(), pageable);
    }
}
