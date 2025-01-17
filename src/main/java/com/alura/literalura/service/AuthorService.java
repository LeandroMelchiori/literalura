package com.alura.literalura.service;

import com.alura.literalura.model.Author;
import com.alura.literalura.repository.AuthorRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AuthorService {

    @Autowired
    private AuthorRepository repository;

    public Author guardarAutor(Author author) {
        Optional<Author> existente = repository.findByName(author.getName());
        if (existente.isPresent()) {
            return existente.get(); // Devuelve la entidad existente, que ya está gestionada
        }
        return repository.save(author); // Guarda y devuelve una nueva entidad gestionada
    }

    // Método para obtener todos los autores registrados
    public List<Author> obtenerTodosLosAutores() {
        return repository.findAll();
    }

    // Método para obtener autores vivos hasta un año específico
    public List<Author> obtenerAutoresVivosHasta(int year) {
        return repository.findAuthorsAliveUntil(year);
    }
}



