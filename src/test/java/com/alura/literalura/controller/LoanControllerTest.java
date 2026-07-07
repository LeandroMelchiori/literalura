package com.alura.literalura.controller;

import com.alura.literalura.dto.LoanDTO;
import com.alura.literalura.exception.BusinessRuleException;
import com.alura.literalura.model.LoanStatus;
import com.alura.literalura.service.LoanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Sin filtros de seguridad: este test cubre la capa web; la seguridad se prueba en AuthFlowIntegrationTest.
@WebMvcTest(LoanController.class)
@AutoConfigureMockMvc(addFilters = false)
class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoanService loanService;

    @MockBean
    private com.alura.literalura.service.CurrentUserService currentUserService;

    private LoanDTO sampleLoan() {
        return new LoanDTO(1L, 10L, "A-001", "Pride and Prejudice",
                2L, "Ana Díaz", LocalDate.now(), LocalDate.now().plusDays(14),
                null, LoanStatus.ACTIVE, false);
    }

    @Test
    void prestar_conDatosValidos_devuelve201() throws Exception {
        when(loanService.registrarPrestamo(anyLong(), anyLong())).thenReturn(sampleLoan());

        mockMvc.perform(post("/api/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"copyId\":10,\"memberId\":2}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.bookTitle").value("Pride and Prejudice"));
    }

    @Test
    void prestar_conReglaDeNegocioViolada_devuelve409() throws Exception {
        when(loanService.registrarPrestamo(anyLong(), anyLong()))
                .thenThrow(new BusinessRuleException("El ejemplar no está disponible para préstamo."));

        mockMvc.perform(post("/api/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"copyId\":10,\"memberId\":2}"))
                .andExpect(status().isConflict());
    }

    @Test
    void prestar_sinCamposObligatorios_devuelve400() throws Exception {
        mockMvc.perform(post("/api/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
