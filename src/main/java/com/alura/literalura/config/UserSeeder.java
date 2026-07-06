package com.alura.literalura.config;

import com.alura.literalura.model.AppUser;
import com.alura.literalura.model.UserRole;
import com.alura.literalura.repository.AppUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Crea los usuarios iniciales si no existen: el ADMIN (siempre) y, si
 * {@code app.demo.enabled} está activo, un LIBRARIAN de demostración con
 * credenciales públicas para que cualquiera pueda probar la app desplegada.
 * Las contraseñas vienen de variables de entorno; las migraciones no las incluyen.
 */
@Configuration
public class UserSeeder {

    private static final Logger log = LoggerFactory.getLogger(UserSeeder.class);

    @Bean
    public CommandLineRunner seedUsers(
            AppUserRepository repository,
            PasswordEncoder encoder,
            @Value("${app.admin.username}") String adminUser,
            @Value("${app.admin.password}") String adminPass,
            @Value("${app.demo.enabled:true}") boolean demoEnabled,
            @Value("${app.demo.username:demo}") String demoUser,
            @Value("${app.demo.password:demo12345}") String demoPass) {
        return args -> {
            createIfAbsent(repository, encoder, adminUser, adminPass, UserRole.ADMIN);
            if (demoEnabled) {
                createIfAbsent(repository, encoder, demoUser, demoPass, UserRole.LIBRARIAN);
            }
        };
    }

    private void createIfAbsent(AppUserRepository repository, PasswordEncoder encoder,
                                String username, String rawPassword, UserRole role) {
        if (repository.existsByUsername(username)) {
            return;
        }
        repository.save(new AppUser(username, encoder.encode(rawPassword), role));
        log.warn("Usuario {} inicial '{}' creado. Cambiar la contraseña por defecto en producción.", role, username);
    }
}
