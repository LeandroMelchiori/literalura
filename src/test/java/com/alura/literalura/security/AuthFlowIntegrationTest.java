package com.alura.literalura.security;

import com.fasterxml.jackson.databind.ObjectMapper;
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
 * Flujo de seguridad completo con los filtros reales activos:
 * rutas públicas, 401 sin token, login, acceso con token y control por rol.
 */
@SpringBootTest
@AutoConfigureMockMvc
class AuthFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String loginAs(String username, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"%s\",\"password\":\"%s\"}"
                                .formatted(username, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString())
                .get("token").asText();
    }

    @Test
    void elCatalogoEsPublicoEnLectura() throws Exception {
        mockMvc.perform(get("/api/books")).andExpect(status().isOk());
        mockMvc.perform(get("/api/authors")).andExpect(status().isOk());
    }

    @Test
    void losEndpointsDeBibliotecaRequierenToken() throws Exception {
        mockMvc.perform(get("/api/loans")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/members")).andExpect(status().isUnauthorized());
    }

    @Test
    void loginConCredencialesInvalidasDevuelve401() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"incorrecta\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void conTokenValidoSeAccedeALosEndpointsProtegidos() throws Exception {
        String token = loginAs("admin", "admin12345");

        mockMvc.perform(get("/api/loans").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void listarUsuariosEsSoloParaAdmin() throws Exception {
        String adminToken = loginAs("admin", "admin12345");
        mockMvc.perform(get("/api/auth/users").header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").exists());

        String demoToken = loginAs("demo", "demo12345");
        mockMvc.perform(get("/api/auth/users").header("Authorization", "Bearer " + demoToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void elUsuarioDemoQuedaSeedeadoYOperaComoBibliotecario() throws Exception {
        String demoToken = loginAs("demo", "demo12345");

        // Puede operar la biblioteca...
        mockMvc.perform(get("/api/loans").header("Authorization", "Bearer " + demoToken))
                .andExpect(status().isOk());
        // ...pero no crear usuarios (eso es solo ADMIN).
        mockMvc.perform(post("/api/auth/users")
                        .header("Authorization", "Bearer " + demoToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"x\",\"password\":\"password123\",\"role\":\"LIBRARIAN\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void soloElAdminPuedeCrearUsuarios() throws Exception {
        String adminToken = loginAs("admin", "admin12345");

        // El ADMIN crea un bibliotecario.
        mockMvc.perform(post("/api/auth/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"biblio\",\"password\":\"password123\",\"role\":\"LIBRARIAN\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("LIBRARIAN"));

        // El bibliotecario opera la biblioteca pero no crea usuarios.
        String librarianToken = loginAs("biblio", "password123");
        mockMvc.perform(get("/api/members").header("Authorization", "Bearer " + librarianToken))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/auth/users")
                        .header("Authorization", "Bearer " + librarianToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"otro\",\"password\":\"password123\",\"role\":\"LIBRARIAN\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void conTokenAdulteradoDevuelve401() throws Exception {
        String token = loginAs("admin", "admin12345");

        // Adulterar el payload invalida la firma; agregar un carácter al final no siempre lo hace.
        String[] parts = token.split("\\.");
        String tampered = parts[0] + "." + parts[1].substring(1) + "." + parts[2];

        mockMvc.perform(get("/api/loans").header("Authorization", "Bearer " + tampered))
                .andExpect(status().isUnauthorized());
    }
}
