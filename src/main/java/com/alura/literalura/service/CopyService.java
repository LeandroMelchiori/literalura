package com.alura.literalura.service;

import com.alura.literalura.dto.CopyDTO;
import com.alura.literalura.dto.CopyRequest;
import com.alura.literalura.exception.BusinessRuleException;
import com.alura.literalura.exception.ResourceNotFoundException;
import com.alura.literalura.model.Book;
import com.alura.literalura.model.Copy;
import com.alura.literalura.model.CopyStatus;
import com.alura.literalura.repository.BookRepository;
import com.alura.literalura.repository.CopyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CopyService {

    private final CopyRepository repository;
    private final BookRepository bookRepository;

    public CopyService(CopyRepository repository, BookRepository bookRepository) {
        this.repository = repository;
        this.bookRepository = bookRepository;
    }

    @Transactional
    public CopyDTO registrarEjemplar(CopyRequest request) {
        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new ResourceNotFoundException("Título", request.bookId()));
        if (repository.findByInventoryCode(request.inventoryCode()).isPresent()) {
            throw new BusinessRuleException("Ya existe un ejemplar con ese código de inventario.");
        }
        Copy copy = repository.save(new Copy(book, request.inventoryCode()));
        return toDto(copy);
    }

    @Transactional(readOnly = true)
    public Page<CopyDTO> obtenerEjemplares(CopyStatus status, Pageable pageable) {
        Page<Copy> copies = (status != null)
                ? repository.findByStatus(status, pageable)
                : repository.findAll(pageable);
        return copies.map(this::toDto);
    }

    @Transactional(readOnly = true)
    public CopyDTO obtenerEjemplar(Long id) {
        return toDto(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ejemplar", id)));
    }

    private CopyDTO toDto(Copy c) {
        return new CopyDTO(c.getId(), c.getBook().getId(),
                c.getBook().getTitle(), c.getInventoryCode(), c.getStatus());
    }
}
