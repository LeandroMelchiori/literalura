package com.alura.literalura.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ejemplares",
        uniqueConstraints = @UniqueConstraint(columnNames = "inventory_code"))
public class Copy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // El préstamo se hace sobre el ejemplar, no sobre el título; de ahí esta relación.
    @ManyToOne(optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "inventory_code", nullable = false, unique = true)
    private String inventoryCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CopyStatus status = CopyStatus.AVAILABLE;

    protected Copy() {
    }

    public Copy(Book book, String inventoryCode) {
        this.book = book;
        this.inventoryCode = inventoryCode;
        this.status = CopyStatus.AVAILABLE;
    }

    public boolean isAvailable() {
        return status == CopyStatus.AVAILABLE;
    }

    public Long getId() {
        return id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public String getInventoryCode() {
        return inventoryCode;
    }

    public void setInventoryCode(String inventoryCode) {
        this.inventoryCode = inventoryCode;
    }

    public CopyStatus getStatus() {
        return status;
    }

    public void setStatus(CopyStatus status) {
        this.status = status;
    }
}
