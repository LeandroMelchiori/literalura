package com.alura.literalura.controller;

import com.alura.literalura.dto.FineDTO;
import com.alura.literalura.service.CurrentUserService;
import com.alura.literalura.service.FineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fines")
@Tag(name = "Multas", description = "Multas por devolución fuera de plazo")
public class FineController {

    private final FineService fineService;
    private final CurrentUserService currentUser;

    public FineController(FineService fineService, CurrentUserService currentUser) {
        this.fineService = fineService;
        this.currentUser = currentUser;
    }

    @Operation(summary = "Multas del socio autenticado",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/mine")
    public List<FineDTO> misMultas(Authentication auth) {
        return fineService.misMultas(currentUser.currentMember(auth).getId());
    }

    @Operation(summary = "Lista las multas impagas (personal)",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping
    public List<FineDTO> impagas() {
        return fineService.impagas();
    }

    @Operation(summary = "El personal registra el pago de una multa",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/{id}/pay")
    public FineDTO pagar(@PathVariable Long id) {
        return fineService.registrarPago(id);
    }
}
