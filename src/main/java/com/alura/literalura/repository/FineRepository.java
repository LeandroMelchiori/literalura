package com.alura.literalura.repository;

import com.alura.literalura.model.Fine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FineRepository extends JpaRepository<Fine, Long> {

    List<Fine> findByMemberIdOrderByCreatedAtDesc(Long memberId);

    List<Fine> findByPaidFalseOrderByCreatedAtAsc();

    boolean existsByMemberIdAndPaidFalse(Long memberId);
}
