package com.alura.literalura.repository;

import com.alura.literalura.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByTitle(String title);

    // JOIN FETCH del autor para evitar el N+1 al mapear cada libro a DTO.
    @Query(value = "SELECT b FROM Book b JOIN FETCH b.author",
            countQuery = "SELECT COUNT(b) FROM Book b")
    Page<Book> findAllWithAuthor(Pageable pageable);

    @Query(value = "SELECT b FROM Book b JOIN FETCH b.author WHERE b.language = :language",
            countQuery = "SELECT COUNT(b) FROM Book b WHERE b.language = :language")
    Page<Book> findByLanguage(String language, Pageable pageable);
}
