package com.alura.literalura.repository;

import com.alura.literalura.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    Optional<Author> findByName(String name);
    @Query("SELECT a FROM Author a WHERE a.deathYear IS NULL OR a.deathYear >= :year")
    List<Author> findAuthorsAliveUntil(@Param("year") int year);

}
