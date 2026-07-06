package com.alura.literalura.service;

import com.alura.literalura.dto.AuthorDTO;
import com.alura.literalura.model.Author;
import com.alura.literalura.repository.AuthorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Page<AuthorDTO> obtenerTodosLosAutores(Pageable pageable) {
        return repository.findAll(pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public Page<AuthorDTO> obtenerAutoresVivosHasta(int year, Pageable pageable) {
        return repository.findAuthorsAliveUntil(year, pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public long contarAutores() {
        return repository.count();
    }

    private AuthorDTO toDto(Author author) {
        return new AuthorDTO(author.getName(), author.getBirthYear(), author.getDeathYear());
    }
}
