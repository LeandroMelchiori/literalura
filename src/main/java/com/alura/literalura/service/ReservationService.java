package com.alura.literalura.service;

import com.alura.literalura.dto.LoanDTO;
import com.alura.literalura.dto.ReservationDTO;
import com.alura.literalura.exception.BusinessRuleException;
import com.alura.literalura.exception.ResourceNotFoundException;
import com.alura.literalura.model.*;
import com.alura.literalura.repository.BookRepository;
import com.alura.literalura.repository.CopyRepository;
import com.alura.literalura.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final BookRepository bookRepository;
    private final CopyRepository copyRepository;
    private final LoanService loanService;

    public ReservationService(ReservationRepository reservationRepository,
                              BookRepository bookRepository,
                              CopyRepository copyRepository,
                              LoanService loanService) {
        this.reservationRepository = reservationRepository;
        this.bookRepository = bookRepository;
        this.copyRepository = copyRepository;
        this.loanService = loanService;
    }

    /** Un socio reserva un título. */
    @Transactional
    public ReservationDTO reservar(Member member, Long bookId) {
        if (!member.isActive()) {
            throw new BusinessRuleException("El socio está suspendido y no puede reservar.");
        }
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Título", bookId));
        if (reservationRepository.existsByMemberIdAndBookIdAndStatus(
                member.getId(), bookId, ReservationStatus.PENDING)) {
            throw new BusinessRuleException("Ya tenés una reserva pendiente de ese título.");
        }
        return toDto(reservationRepository.save(new Reservation(member, book)));
    }

    @Transactional(readOnly = true)
    public List<ReservationDTO> misReservas(Long memberId) {
        return reservationRepository.findByMemberIdOrderByReservationDateDesc(memberId)
                .stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationDTO> pendientes() {
        return reservationRepository.findByStatusOrderByReservationDateAsc(ReservationStatus.PENDING)
                .stream().map(this::toDto).toList();
    }

    /** El socio cancela una reserva propia. */
    @Transactional
    public ReservationDTO cancelar(Long reservationId, Member member) {
        Reservation reservation = buscarOFallar(reservationId);
        if (!reservation.getMember().getId().equals(member.getId())) {
            throw new BusinessRuleException("La reserva no pertenece al socio.");
        }
        exigirPendiente(reservation);
        reservation.setStatus(ReservationStatus.CANCELLED);
        return toDto(reservation);
    }

    /**
     * El bibliotecario cumple una reserva prestando un ejemplar del título reservado.
     * Reutiliza las reglas de préstamo (disponibilidad, socio activo, sin vencidos).
     */
    @Transactional
    public LoanDTO cumplir(Long reservationId, Long copyId) {
        Reservation reservation = buscarOFallar(reservationId);
        exigirPendiente(reservation);

        Copy copy = copyRepository.findById(copyId)
                .orElseThrow(() -> new ResourceNotFoundException("Ejemplar", copyId));
        if (!copy.getBook().getId().equals(reservation.getBook().getId())) {
            throw new BusinessRuleException("El ejemplar no corresponde al título reservado.");
        }

        LoanDTO loan = loanService.registrarPrestamo(copyId, reservation.getMember().getId());
        reservation.setStatus(ReservationStatus.FULFILLED);
        return loan;
    }

    private void exigirPendiente(Reservation reservation) {
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new BusinessRuleException("La reserva ya fue atendida o cancelada.");
        }
    }

    private Reservation buscarOFallar(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva", id));
    }

    private ReservationDTO toDto(Reservation r) {
        return new ReservationDTO(
                r.getId(),
                r.getBook().getId(),
                r.getBook().getTitle(),
                r.getMember().getId(),
                r.getMember().getName(),
                r.getReservationDate(),
                r.getStatus());
    }
}
