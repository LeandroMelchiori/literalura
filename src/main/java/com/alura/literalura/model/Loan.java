package com.alura.literalura.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "prestamos")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "copy_id", nullable = false)
    private Copy copy;

    @ManyToOne(optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "loan_date", nullable = false)
    private LocalDate loanDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    // null mientras el ejemplar no se devuelve.
    @Column(name = "return_date")
    private LocalDate returnDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status = LoanStatus.ACTIVE;

    @Column(nullable = false)
    private int renewals = 0;

    protected Loan() {
    }

    public Loan(Copy copy, Member member, LocalDate loanDate, LocalDate dueDate) {
        this.copy = copy;
        this.member = member;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.status = LoanStatus.ACTIVE;
    }

    /** Vencido: sigue activo y su fecha esperada de devolución ya pasó. */
    public boolean isOverdue() {
        return status == LoanStatus.ACTIVE && dueDate.isBefore(LocalDate.now());
    }

    public void registrarDevolucion(LocalDate returnDate) {
        this.returnDate = returnDate;
        this.status = LoanStatus.RETURNED;
    }

    /** Extiende el plazo y cuenta la renovación. */
    public void renovar(LocalDate nuevaFecha) {
        this.dueDate = nuevaFecha;
        this.renewals++;
    }

    /** Días de atraso a una fecha dada (0 si no hay atraso). */
    public long daysLate(LocalDate reference) {
        return dueDate.isBefore(reference)
                ? java.time.temporal.ChronoUnit.DAYS.between(dueDate, reference)
                : 0;
    }

    public Long getId() {
        return id;
    }

    public Copy getCopy() {
        return copy;
    }

    public Member getMember() {
        return member;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public int getRenewals() {
        return renewals;
    }
}
