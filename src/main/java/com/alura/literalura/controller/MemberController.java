package com.alura.literalura.controller;

import com.alura.literalura.dto.MemberDTO;
import com.alura.literalura.dto.MemberRequest;
import com.alura.literalura.model.MemberStatus;
import com.alura.literalura.service.MemberService;
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
@RequestMapping("/api/members")
@Tag(name = "Socios", description = "Alta y gestión de socios de la biblioteca")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @Operation(summary = "Registra un nuevo socio")
    @PostMapping
    public ResponseEntity<MemberDTO> registrar(@Valid @RequestBody MemberRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(memberService.registrarSocio(request));
    }

    @Operation(summary = "Lista paginada de socios, con búsqueda por nombre")
    @GetMapping
    public Page<MemberDTO> listar(@RequestParam(required = false) String search,
                                  @PageableDefault(size = 20) Pageable pageable) {
        return memberService.obtenerSocios(search, pageable);
    }

    @Operation(summary = "Obtiene un socio por id")
    @GetMapping("/{id}")
    public MemberDTO obtener(@PathVariable Long id) {
        return memberService.obtenerSocio(id);
    }

    @Operation(summary = "Cambia el estado de un socio (ACTIVE / SUSPENDED)")
    @PatchMapping("/{id}/status")
    public MemberDTO cambiarEstado(@PathVariable Long id, @RequestParam MemberStatus status) {
        return memberService.cambiarEstado(id, status);
    }
}
