package com.alura.literalura.repository;

import com.alura.literalura.model.Copy;
import com.alura.literalura.model.CopyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CopyRepository extends JpaRepository<Copy, Long> {

    Optional<Copy> findByInventoryCode(String inventoryCode);

    Page<Copy> findByStatus(CopyStatus status, Pageable pageable);

    Page<Copy> findByBookId(Long bookId, Pageable pageable);

    Page<Copy> findByBookIdAndStatus(Long bookId, CopyStatus status, Pageable pageable);

    long countByBookIdAndStatus(Long bookId, CopyStatus status);

    // Disponibles por título en una sola consulta, para no hacer un COUNT por libro al listar.
    @Query("""
            SELECT c.book.id, COUNT(c) FROM Copy c
            WHERE c.status = com.alura.literalura.model.CopyStatus.AVAILABLE
            GROUP BY c.book.id
            """)
    List<Object[]> countAvailableGroupedByBook();
}
