package com.alura.literalura.config;

import com.alura.literalura.security.AppUserDetailsService;
import com.alura.literalura.security.JwtAuthFilter;
import com.alura.literalura.security.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtService jwtService;
    private final AppUserDetailsService userDetailsService;

    public SecurityConfig(JwtService jwtService, AppUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // API stateless con JWT: sin sesión ni CSRF.
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        // El catálogo es de lectura pública.
                        .requestMatchers(HttpMethod.GET, "/api/books/**", "/api/authors/**").permitAll()

                        // Gestión de usuarios: solo ADMIN.
                        .requestMatchers("/api/auth/users/**").hasRole("ADMIN")

                        // Portal del cliente: cada socio ve y gestiona lo suyo.
                        .requestMatchers(HttpMethod.GET, "/api/loans/mine").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/api/reservations/mine").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.POST, "/api/reservations").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.POST, "/api/reservations/*/cancel").hasRole("CLIENTE")

                        // Operación de la biblioteca: solo personal (bibliotecario o admin).
                        .requestMatchers(HttpMethod.POST, "/api/books/search").hasAnyRole("LIBRARIAN", "ADMIN")
                        .requestMatchers("/api/copies/**", "/api/members/**").hasAnyRole("LIBRARIAN", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/reservations").hasAnyRole("LIBRARIAN", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/reservations/*/fulfill").hasAnyRole("LIBRARIAN", "ADMIN")
                        .requestMatchers("/api/loans/**").hasAnyRole("LIBRARIAN", "ADMIN")

                        .anyRequest().authenticated())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) ->
                                writeProblem(res, HttpServletResponse.SC_UNAUTHORIZED,
                                        "No autenticado", "Se requiere un token válido."))
                        .accessDeniedHandler((req, res, e) ->
                                writeProblem(res, HttpServletResponse.SC_FORBIDDEN,
                                        "Acceso denegado", "No tiene permisos para esta operación.")))
                .addFilterBefore(new JwtAuthFilter(jwtService, userDetailsService),
                        UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    // Mantiene el formato ProblemDetail (RFC 7807) también en los errores de seguridad.
    private void writeProblem(HttpServletResponse res, int status, String title, String detail)
            throws java.io.IOException {
        res.setStatus(status);
        res.setContentType("application/problem+json;charset=UTF-8");
        res.getWriter().write("""
                {"type":"about:blank","title":"%s","status":%d,"detail":"%s"}"""
                .formatted(title, status, detail));
    }

    /** Orígenes del frontend permitidos (dev de Vite por defecto; en prod, el dominio de Vercel). */
    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.allowed-origins}") List<String> allowedOrigins) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(List.of("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}
