package com.alura.literalura.repository;

import com.alura.literalura.model.Loan;
import com.alura.literalura.model.LoanStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    Page<Loan> findByStatus(LoanStatus status, Pageable pageable);

    Page<Loan> findByMemberId(Long memberId, Pageable pageable);

    long countByMemberIdAndStatus(Long memberId, LoanStatus status);

    // Un socio con préstamos vencidos no puede pedir más; esta consulta lo detecta.
    @Query("""
            SELECT COUNT(l) > 0 FROM Loan l
            WHERE l.member.id = :memberId
              AND l.status = com.alura.literalura.model.LoanStatus.ACTIVE
              AND l.dueDate < :today
            """)
    boolean memberHasOverdueLoans(Long memberId, LocalDate today);

    @Query("""
            SELECT l FROM Loan l
            WHERE l.status = com.alura.literalura.model.LoanStatus.ACTIVE
              AND l.dueDate < :today
            """)
    List<Loan> findOverdue(LocalDate today);
}
