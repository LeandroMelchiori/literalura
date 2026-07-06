package com.alura.literalura.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private static final String SECRET = "unit-test-secret-key-0123456789-0123456789abcdef";

    @Test
    void generarYExtraer_conTokenValido_devuelveElUsername() {
        JwtService service = new JwtService(SECRET, 3600);

        String token = service.generateToken("admin", "ADMIN");

        assertThat(service.extractUsername(token)).contains("admin");
    }

    @Test
    void extraer_conTokenAdulterado_devuelveVacio() {
        JwtService service = new JwtService(SECRET, 3600);
        String token = service.generateToken("admin", "ADMIN");

        // Adulterar el payload (parte media del JWT) invalida la firma.
        String[] parts = token.split("\\.");
        String tampered = parts[0] + "." + parts[1].substring(1) + "." + parts[2];

        assertThat(service.extractUsername(tampered)).isEmpty();
        assertThat(service.extractUsername("no-es-un-jwt")).isEmpty();
    }

    @Test
    void extraer_conTokenExpirado_devuelveVacio() {
        // Expiración negativa: el token nace vencido.
        JwtService service = new JwtService(SECRET, -60);
        String token = service.generateToken("admin", "ADMIN");

        assertThat(service.extractUsername(token)).isEmpty();
    }

    @Test
    void extraer_conTokenFirmadoConOtraClave_devuelveVacio() {
        JwtService emisor = new JwtService(SECRET, 3600);
        JwtService receptor = new JwtService("otra-clave-distinta-0123456789-0123456789abcdef", 3600);

        String token = emisor.generateToken("admin", "ADMIN");

        assertThat(receptor.extractUsername(token)).isEmpty();
    }
}
