package com.alura.literalura.security;

import com.alura.literalura.model.Author;
import com.alura.literalura.model.Book;
import com.alura.literalura.repository.AuthorRepository;
import com.alura.literalura.repository.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Flujo del portal del cliente: el bibliotecario registra al socio (con su
 * login), el cliente reserva y ve lo suyo, y no puede operar la biblioteca.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ClientPortalIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private BookRepository bookRepository;
    @Autowired private AuthorRepository authorRepository;

    private Long bookId;

    @BeforeEach
    void seedBook() {
        // @SpringBootTest confirma los cambios entre tests; reutilizar el autor evita
        // violar la restricción de nombre único al re-ejecutar el @BeforeEach.
        Author author = authorRepository.findByName("Austen, Jane")
                .orElseGet(() -> authorRepository.save(new Author("Austen, Jane", 1775, 1817)));
        Book book = new Book("Pride and Prejudice", author, "en", 12345);
        bookId = bookRepository.save(book).getId();
    }

    private String token(String user, String pass) throws Exception {
        MvcResult r = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"%s\",\"password\":\"%s\"}".formatted(user, pass)))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(r.getResponse().getContentAsString()).get("token").asText();
    }

    private String registrarCliente(String librarian, String username) throws Exception {
        mockMvc.perform(post("/api/members")
                        .header("Authorization", "Bearer " + librarian)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Cliente Test","email":"%s@mail.com","documentId":"%s",
                                 "username":"%s","password":"password123"}"""
                                .formatted(username, username, username)))
                .andExpect(status().isCreated());
        return token(username, "password123");
    }

    @Test
    void elBibliotecarioRegistraUnClienteQueLuegoPuedeIniciarSesion() throws Exception {
        String librarian = token("demo", "demo12345");
        String client = registrarCliente(librarian, "cliente1");

        // El cliente ve su portal (vacío al inicio).
        mockMvc.perform(get("/api/loans/mine").header("Authorization", "Bearer " + client))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void elClienteReservaYVeSuReserva() throws Exception {
        String librarian = token("demo", "demo12345");
        String client = registrarCliente(librarian, "cliente2");

        mockMvc.perform(post("/api/reservations")
                        .header("Authorization", "Bearer " + client)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bookId\":" + bookId + "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"));

        mockMvc.perform(get("/api/reservations/mine").header("Authorization", "Bearer " + client))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookTitle").value("Pride and Prejudice"));
    }

    @Test
    void elClienteNoPuedeOperarLaBiblioteca() throws Exception {
        String librarian = token("demo", "demo12345");
        String client = registrarCliente(librarian, "cliente3");

        // Crear préstamos, ver socios o listar reservas pendientes es solo del personal.
        mockMvc.perform(post("/api/loans")
                        .header("Authorization", "Bearer " + client)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"copyId\":1,\"memberId\":1}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/members").header("Authorization", "Bearer " + client))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/reservations").header("Authorization", "Bearer " + client))
                .andExpect(status().isForbidden());
    }

    @Test
    void elBibliotecarioNoPuedeReservar() throws Exception {
        String librarian = token("demo", "demo12345");
        mockMvc.perform(post("/api/reservations")
                        .header("Authorization", "Bearer " + librarian)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bookId\":" + bookId + "}"))
                .andExpect(status().isForbidden());
    }
}
