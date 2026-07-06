package com.alura.literalura.service;

import com.alura.literalura.dto.LoanDTO;
import com.alura.literalura.exception.BusinessRuleException;
import com.alura.literalura.exception.ResourceNotFoundException;
import com.alura.literalura.model.*;
import com.alura.literalura.repository.CopyRepository;
import com.alura.literalura.repository.LoanRepository;
import com.alura.literalura.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class LoanService {

    // Política de la biblioteca: plazo de devolución en días.
    private static final int LOAN_DAYS = 14;

    private final LoanRepository loanRepository;
    private final CopyRepository copyRepository;
    private final MemberRepository memberRepository;

    public LoanService(LoanRepository loanRepository,
                       CopyRepository copyRepository,
                       MemberRepository memberRepository) {
        this.loanRepository = loanRepository;
        this.copyRepository = copyRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public LoanDTO registrarPrestamo(Long copyId, Long memberId) {
        Copy copy = copyRepository.findById(copyId)
                .orElseThrow(() -> new ResourceNotFoundException("Ejemplar", copyId));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Socio", memberId));

        if (!copy.isAvailable()) {
            throw new BusinessRuleException("El ejemplar no está disponible para préstamo.");
        }
        if (!member.isActive()) {
            throw new BusinessRuleException("El socio está suspendido y no puede pedir préstamos.");
        }
        if (loanRepository.memberHasOverdueLoans(memberId, LocalDate.now())) {
            throw new BusinessRuleException("El socio tiene préstamos vencidos pendientes de devolución.");
        }

        LocalDate today = LocalDate.now();
        Loan loan = new Loan(copy, member, today, today.plusDays(LOAN_DAYS));
        copy.setStatus(CopyStatus.ON_LOAN);
        return toDto(loanRepository.save(loan));
    }

    @Transactional
    public LoanDTO registrarDevolucion(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Préstamo", loanId));
        if (loan.getStatus() != LoanStatus.ACTIVE) {
            throw new BusinessRuleException("El préstamo ya fue devuelto.");
        }
        loan.registrarDevolucion(LocalDate.now());
        loan.getCopy().setStatus(CopyStatus.AVAILABLE);
        return toDto(loan);
    }

    @Transactional(readOnly = true)
    public Page<LoanDTO> obtenerPrestamos(LoanStatus status, Pageable pageable) {
        Page<Loan> loans = (status != null)
                ? loanRepository.findByStatus(status, pageable)
                : loanRepository.findAll(pageable);
        return loans.map(this::toDto);
    }

    @Transactional(readOnly = true)
    public Page<LoanDTO> obtenerPrestamosPorSocio(Long memberId, Pageable pageable) {
        return loanRepository.findByMemberId(memberId, pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public List<LoanDTO> obtenerVencidos() {
        return loanRepository.findOverdue(LocalDate.now()).stream().map(this::toDto).toList();
    }

    private LoanDTO toDto(Loan l) {
        Copy c = l.getCopy();
        Member m = l.getMember();
        return new LoanDTO(
                l.getId(),
                c.getId(),
                c.getInventoryCode(),
                c.getBook().getTitle(),
                m.getId(),
                m.getName(),
                l.getLoanDate(),
                l.getDueDate(),
                l.getReturnDate(),
                l.getStatus(),
                l.isOverdue());
    }
}
