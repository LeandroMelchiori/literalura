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
 * Crea el usuario ADMIN inicial si no hay usuarios. Las credenciales vienen
 * de variables de entorno; la migración no incluye contraseñas.
 */
@Configuration
public class AdminSeeder {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    @Bean
    public CommandLineRunner seedAdmin(AppUserRepository repository,
                                       PasswordEncoder encoder,
                                       @Value("${app.admin.username}") String username,
                                       @Value("${app.admin.password}") String password) {
        return args -> {
            if (repository.count() == 0) {
                repository.save(new AppUser(username, encoder.encode(password), UserRole.ADMIN));
                log.warn("Usuario ADMIN inicial '{}' creado. Cambiar la contraseña por defecto en producción.", username);
            }
        };
    }
}
