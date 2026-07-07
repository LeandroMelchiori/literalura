package com.alura.literalura.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Multa generada cuando un préstamo se devuelve fuera de plazo. */
@Entity
@Table(name = "multas")
public class Fine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "days_late", nullable = false)
    private int daysLate;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt = LocalDate.now();

    @Column(nullable = false)
    private boolean paid = false;

    protected Fine() {
    }

    public Fine(Member member, Loan loan, BigDecimal amount, int daysLate) {
        this.member = member;
        this.loan = loan;
        this.amount = amount;
        this.daysLate = daysLate;
    }

    public void pay() {
        this.paid = true;
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public Loan getLoan() {
        return loan;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public int getDaysLate() {
        return daysLate;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public boolean isPaid() {
        return paid;
    }
}
