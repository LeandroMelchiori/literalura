package com.alura.literalura.controller;

import com.alura.literalura.dto.AuthorDTO;
import com.alura.literalura.dto.BookDTO;
import com.alura.literalura.exception.BookNotFoundException;
import com.alura.literalura.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Sin filtros de seguridad: este test cubre la capa web; la seguridad se prueba en AuthFlowIntegrationTest.
@WebMvcTest(BookController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Test
    void searchDevuelve201YElLibro() throws Exception {
        BookDTO dto = new BookDTO(1L, "Pride and Prejudice",
                new AuthorDTO("Austen, Jane", 1775, 1817), List.of("en"), 12345, 0);
        when(bookService.buscarYRegistrarLibro(anyString())).thenReturn(dto);

        mockMvc.perform(post("/api/books/search").param("title", "pride"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Pride and Prejudice"))
                .andExpect(jsonPath("$.author.name").value("Austen, Jane"));
    }

    @Test
    void searchConTituloVacioDevuelve400() throws Exception {
        mockMvc.perform(post("/api/books/search").param("title", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchLibroInexistenteDevuelve404() throws Exception {
        when(bookService.buscarYRegistrarLibro(anyString()))
                .thenThrow(new BookNotFoundException("xyz"));

        mockMvc.perform(post("/api/books/search").param("title", "xyz"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listarTodosDevuelvePaginaConContenido() throws Exception {
        when(bookService.obtenerTodosLosLibros(any(), any(Pageable.class))).thenReturn(
                new PageImpl<>(List.of(
                        new BookDTO(1L, "A", new AuthorDTO("X", null, null), List.of("en"), 1, 2))));

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("A"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }
}
