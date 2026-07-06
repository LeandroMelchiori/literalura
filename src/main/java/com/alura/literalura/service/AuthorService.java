package com.alura.literalura.service;

import com.alura.literalura.dto.AuthorDTO;
import com.alura.literalura.model.Author;
import com.alura.literalura.repository.AuthorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthorService {

    private final AuthorRepository repository;

    public AuthorService(AuthorRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Author guardarAutor(Author author) {
        return repository.findByName(author.getName())
                .orElseGet(() -> repository.save(author));
    }

    @Transactional(readOnly = true)
    public List<AuthorDTO> obtenerTodosLosAutores() {
        return repository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AuthorDTO> obtenerAutoresVivosHasta(int year) {
        return repository.findAuthorsAliveUntil(year).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long contarAutores() {
        return repository.count();
    }

    private AuthorDTO toDto(Author author) {
        return new AuthorDTO(author.getName(), author.getBirthYear(), author.getDeathYear());
    }
}
