package com.alura.literalura.repository;

import com.alura.literalura.model.Copy;
import com.alura.literalura.model.CopyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CopyRepository extends JpaRepository<Copy, Long> {

    Optional<Copy> findByInventoryCode(String inventoryCode);

    Page<Copy> findByStatus(CopyStatus status, Pageable pageable);

    Page<Copy> findByBookId(Long bookId, Pageable pageable);
}
