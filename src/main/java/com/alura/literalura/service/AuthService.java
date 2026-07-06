package com.alura.literalura.service;

import com.alura.literalura.dto.*;
import com.alura.literalura.exception.BusinessRuleException;
import com.alura.literalura.model.AppUser;
import com.alura.literalura.repository.AppUserRepository;
import com.alura.literalura.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager,
                       AppUserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public TokenResponse login(LoginRequest request) {
        // Lanza AuthenticationException (401) si las credenciales no son válidas.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        AppUser user = userRepository.findByUsername(request.username()).orElseThrow();
        String token = jwtService.generateToken(user.getUsername(), user.getRole().name());
        return TokenResponse.bearer(token, jwtService.getExpirationSeconds());
    }

    @Transactional
    public UserDTO crearUsuario(UserRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessRuleException("Ya existe un usuario con ese nombre.");
        }
        AppUser user = userRepository.save(new AppUser(
                request.username(),
                passwordEncoder.encode(request.password()),
                request.role()));
        return new UserDTO(user.getId(), user.getUsername(), user.getRole());
    }

    @Transactional(readOnly = true)
    public List<UserDTO> listarUsuarios() {
        return userRepository.findAll().stream()
                .map(u -> new UserDTO(u.getId(), u.getUsername(), u.getRole()))
                .toList();
    }
}
