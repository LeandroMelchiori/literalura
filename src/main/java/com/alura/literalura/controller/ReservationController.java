package com.alura.literalura.controller;

import com.alura.literalura.dto.LoanDTO;
import com.alura.literalura.dto.ReservationDTO;
import com.alura.literalura.dto.ReservationRequest;
import com.alura.literalura.service.CurrentUserService;
import com.alura.literalura.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Reservas", description = "Reservas de títulos por parte de los socios")
public class ReservationController {

    private final ReservationService reservationService;
    private final CurrentUserService currentUser;

    public ReservationController(ReservationService reservationService, CurrentUserService currentUser) {
        this.reservationService = reservationService;
        this.currentUser = currentUser;
    }

    @Operation(summary = "El socio reserva un título",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public ResponseEntity<ReservationDTO> reservar(@Valid @RequestBody ReservationRequest request,
                                                   Authentication auth) {
        ReservationDTO dto = reservationService.reservar(currentUser.currentMember(auth), request.bookId());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @Operation(summary = "Reservas del socio autenticado",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/mine")
    public List<ReservationDTO> misReservas(Authentication auth) {
        return reservationService.misReservas(currentUser.currentMember(auth).getId());
    }

    @Operation(summary = "El socio cancela una reserva propia",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/{id}/cancel")
    public ReservationDTO cancelar(@PathVariable Long id, Authentication auth) {
        return reservationService.cancelar(id, currentUser.currentMember(auth));
    }

    @Operation(summary = "Lista las reservas pendientes (personal)",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping
    public List<ReservationDTO> pendientes() {
        return reservationService.pendientes();
    }

    @Operation(summary = "El personal cumple una reserva prestando un ejemplar",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/{id}/fulfill")
    public ResponseEntity<LoanDTO> cumplir(@PathVariable Long id, @RequestParam Long copyId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reservationService.cumplir(id, copyId));
    }
}
