package com.alura.literalura.service;

import com.alura.literalura.dto.LoanDTO;
import com.alura.literalura.exception.BusinessRuleException;
import com.alura.literalura.exception.ResourceNotFoundException;
import com.alura.literalura.model.*;
import com.alura.literalura.repository.CopyRepository;
import com.alura.literalura.repository.FineRepository;
import com.alura.literalura.repository.LoanRepository;
import com.alura.literalura.repository.MemberRepository;
import com.alura.literalura.repository.ReservationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class LoanService {

    // Políticas de la biblioteca.
    private static final int LOAN_DAYS = 14;
    private static final int MAX_ACTIVE_LOANS = 3;
    private static final int MAX_RENEWALS = 2;
    private static final BigDecimal FINE_PER_DAY = new BigDecimal("50.00");

    private final LoanRepository loanRepository;
    private final CopyRepository copyRepository;
    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;
    private final FineRepository fineRepository;

    public LoanService(LoanRepository loanRepository,
                       CopyRepository copyRepository,
                       MemberRepository memberRepository,
                       ReservationRepository reservationRepository,
                       FineRepository fineRepository) {
        this.loanRepository = loanRepository;
        this.copyRepository = copyRepository;
        this.memberRepository = memberRepository;
        this.reservationRepository = reservationRepository;
        this.fineRepository = fineRepository;
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
        if (fineRepository.existsByMemberIdAndPaidFalse(memberId)) {
            throw new BusinessRuleException("El socio tiene multas impagas.");
        }
        if (loanRepository.countByMemberIdAndStatus(memberId, LoanStatus.ACTIVE) >= MAX_ACTIVE_LOANS) {
            throw new BusinessRuleException(
                    "El socio alcanzó el máximo de " + MAX_ACTIVE_LOANS + " préstamos activos.");
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

        LocalDate today = LocalDate.now();
        long late = loan.daysLate(today);
        loan.registrarDevolucion(today);
        loan.getCopy().setStatus(CopyStatus.AVAILABLE);

        // Devolución fuera de plazo: genera una multa impaga.
        if (late > 0) {
            BigDecimal amount = FINE_PER_DAY.multiply(BigDecimal.valueOf(late));
            fineRepository.save(new Fine(loan.getMember(), loan, amount, (int) late));
        }
        return toDto(loan);
    }

    /** El socio renueva un préstamo propio si no está vencido ni reservado por otro. */
    @Transactional
    public LoanDTO renovar(Long loanId, Member member) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Préstamo", loanId));
        if (!loan.getMember().getId().equals(member.getId())) {
            throw new BusinessRuleException("El préstamo no pertenece al socio.");
        }
        if (loan.getStatus() != LoanStatus.ACTIVE) {
            throw new BusinessRuleException("Solo se pueden renovar préstamos activos.");
        }
        if (loan.isOverdue()) {
            throw new BusinessRuleException("No se puede renovar un préstamo vencido.");
        }
        if (loan.getRenewals() >= MAX_RENEWALS) {
            throw new BusinessRuleException(
                    "El préstamo alcanzó el máximo de " + MAX_RENEWALS + " renovaciones.");
        }
        if (reservationRepository.existsByBookIdAndStatus(
                loan.getCopy().getBook().getId(), ReservationStatus.PENDING)) {
            throw new BusinessRuleException("Hay una reserva pendiente para este título; no se puede renovar.");
        }
        loan.renovar(loan.getDueDate().plusDays(LOAN_DAYS));
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
                l.isOverdue(),
                l.getRenewals());
    }
}
