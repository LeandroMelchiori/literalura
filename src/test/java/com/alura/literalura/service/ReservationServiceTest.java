package com.alura.literalura.service;

import com.alura.literalura.dto.LoanDTO;
import com.alura.literalura.dto.ReservationDTO;
import com.alura.literalura.exception.BusinessRuleException;
import com.alura.literalura.model.*;
import com.alura.literalura.repository.BookRepository;
import com.alura.literalura.repository.CopyRepository;
import com.alura.literalura.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock private ReservationRepository reservationRepository;
    @Mock private BookRepository bookRepository;
    @Mock private CopyRepository copyRepository;
    @Mock private LoanService loanService;
    @InjectMocks private ReservationService service;

    private Member member;
    private Book book;

    @BeforeEach
    void setUp() {
        book = mock(Book.class);
        lenient().when(book.getId()).thenReturn(5L);
        lenient().when(book.getTitle()).thenReturn("Emma");
        member = mock(Member.class);
        lenient().when(member.getId()).thenReturn(1L);
        lenient().when(member.getName()).thenReturn("Ana");
        lenient().when(member.isActive()).thenReturn(true);
    }

    @Test
    void reservar_titulo_creaReservaPendiente() {
        when(bookRepository.findById(5L)).thenReturn(Optional.of(book));
        when(reservationRepository.existsByMemberIdAndBookIdAndStatus(1L, 5L, ReservationStatus.PENDING))
                .thenReturn(false);
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> inv.getArgument(0));

        ReservationDTO dto = service.reservar(member, 5L);

        assertThat(dto.bookTitle()).isEqualTo("Emma");
        assertThat(dto.status()).isEqualTo(ReservationStatus.PENDING);
    }

    @Test
    void reservar_socioSuspendido_falla() {
        when(member.isActive()).thenReturn(false);

        assertThatThrownBy(() -> service.reservar(member, 5L))
                .isInstanceOf(BusinessRuleException.class);
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void reservar_duplicadaPendiente_falla() {
        when(bookRepository.findById(5L)).thenReturn(Optional.of(book));
        when(reservationRepository.existsByMemberIdAndBookIdAndStatus(1L, 5L, ReservationStatus.PENDING))
                .thenReturn(true);

        assertThatThrownBy(() -> service.reservar(member, 5L))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void cancelar_reservaDeOtroSocio_falla() {
        Member otro = mock(Member.class);
        when(otro.getId()).thenReturn(99L);
        Reservation reserva = new Reservation(otro, book);
        when(reservationRepository.findById(7L)).thenReturn(Optional.of(reserva));

        assertThatThrownBy(() -> service.cancelar(7L, member))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void cumplir_prestaElEjemplarYMarcaCumplida() {
        Reservation reserva = new Reservation(member, book);
        Copy copy = mock(Copy.class);
        when(copy.getBook()).thenReturn(book);
        when(reservationRepository.findById(7L)).thenReturn(Optional.of(reserva));
        when(copyRepository.findById(3L)).thenReturn(Optional.of(copy));
        when(loanService.registrarPrestamo(3L, 1L))
                .thenReturn(mock(LoanDTO.class));

        service.cumplir(7L, 3L);

        assertThat(reserva.getStatus()).isEqualTo(ReservationStatus.FULFILLED);
        verify(loanService).registrarPrestamo(3L, 1L);
    }

    @Test
    void cumplir_ejemplarDeOtroTitulo_falla() {
        Reservation reserva = new Reservation(member, book);
        Copy copy = mock(Copy.class);
        Book otro = mock(Book.class);
        when(otro.getId()).thenReturn(999L);
        when(copy.getBook()).thenReturn(otro);
        when(reservationRepository.findById(7L)).thenReturn(Optional.of(reserva));
        when(copyRepository.findById(3L)).thenReturn(Optional.of(copy));

        assertThatThrownBy(() -> service.cumplir(7L, 3L))
                .isInstanceOf(BusinessRuleException.class);
        verify(loanService, never()).registrarPrestamo(anyLong(), anyLong());
    }
}
