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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;
    @Mock
    private CopyRepository copyRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private FineRepository fineRepository;

    @InjectMocks
    private LoanService loanService;

    private Book book;
    private Copy copy;
    private Member member;

    @BeforeEach
    void setUp() {
        book = new Book("Pride and Prejudice", new Author("Austen, Jane", 1775, 1817), "en", 100);
        copy = new Copy(book, "A-001");
        member = new Member("Ana Díaz", "ana@mail.com", "12345678");
    }

    @Test
    void registrarPrestamo_conEjemplarDisponibleYSocioActivo_creaElPrestamo() {
        when(copyRepository.findById(1L)).thenReturn(Optional.of(copy));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(member));
        when(loanRepository.memberHasOverdueLoans(eq(2L), any())).thenReturn(false);
        when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));

        LoanDTO dto = loanService.registrarPrestamo(1L, 2L);

        assertThat(dto.status()).isEqualTo(LoanStatus.ACTIVE);
        assertThat(dto.bookTitle()).isEqualTo("Pride and Prejudice");
        assertThat(dto.dueDate()).isEqualTo(LocalDate.now().plusDays(14));
        // El ejemplar queda marcado como prestado.
        assertThat(copy.getStatus()).isEqualTo(CopyStatus.ON_LOAN);
        verify(loanRepository).save(any(Loan.class));
    }

    @Test
    void registrarPrestamo_conEjemplarNoDisponible_lanzaBusinessRule() {
        copy.setStatus(CopyStatus.ON_LOAN);
        when(copyRepository.findById(1L)).thenReturn(Optional.of(copy));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(member));

        assertThatThrownBy(() -> loanService.registrarPrestamo(1L, 2L))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("no está disponible");
        verify(loanRepository, never()).save(any());
    }

    @Test
    void registrarPrestamo_conSocioSuspendido_lanzaBusinessRule() {
        member.setStatus(MemberStatus.SUSPENDED);
        when(copyRepository.findById(1L)).thenReturn(Optional.of(copy));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(member));

        assertThatThrownBy(() -> loanService.registrarPrestamo(1L, 2L))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("suspendido");
        verify(loanRepository, never()).save(any());
    }

    @Test
    void registrarPrestamo_conSocioQueTieneVencidos_lanzaBusinessRule() {
        when(copyRepository.findById(1L)).thenReturn(Optional.of(copy));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(member));
        when(loanRepository.memberHasOverdueLoans(eq(2L), any())).thenReturn(true);

        assertThatThrownBy(() -> loanService.registrarPrestamo(1L, 2L))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("vencidos");
        verify(loanRepository, never()).save(any());
    }

    @Test
    void registrarPrestamo_conEjemplarInexistente_lanzaNotFound() {
        when(copyRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loanService.registrarPrestamo(99L, 2L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void registrarDevolucion_dePrestamoActivo_lomarcaDevueltoYLiberaElEjemplar() {
        copy.setStatus(CopyStatus.ON_LOAN);
        Loan loan = new Loan(copy, member, LocalDate.now().minusDays(3), LocalDate.now().plusDays(11));
        when(loanRepository.findById(5L)).thenReturn(Optional.of(loan));

        LoanDTO dto = loanService.registrarDevolucion(5L);

        assertThat(dto.status()).isEqualTo(LoanStatus.RETURNED);
        assertThat(dto.returnDate()).isEqualTo(LocalDate.now());
        assertThat(copy.getStatus()).isEqualTo(CopyStatus.AVAILABLE);
    }

    @Test
    void registrarDevolucion_dePrestamoYaDevuelto_lanzaBusinessRule() {
        Loan loan = new Loan(copy, member, LocalDate.now().minusDays(3), LocalDate.now().plusDays(11));
        loan.registrarDevolucion(LocalDate.now().minusDays(1));
        when(loanRepository.findById(5L)).thenReturn(Optional.of(loan));

        assertThatThrownBy(() -> loanService.registrarDevolucion(5L))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("ya fue devuelto");
    }

    // ----- Trío: límite de préstamos, multas y renovaciones -----

    @Test
    void registrarPrestamo_conMultasImpagas_lanzaBusinessRule() {
        when(copyRepository.findById(1L)).thenReturn(Optional.of(copy));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(member));
        when(loanRepository.memberHasOverdueLoans(eq(2L), any())).thenReturn(false);
        when(fineRepository.existsByMemberIdAndPaidFalse(2L)).thenReturn(true);

        assertThatThrownBy(() -> loanService.registrarPrestamo(1L, 2L))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("multas impagas");
        verify(loanRepository, never()).save(any());
    }

    @Test
    void registrarPrestamo_alAlcanzarElMaximoDeActivos_lanzaBusinessRule() {
        when(copyRepository.findById(1L)).thenReturn(Optional.of(copy));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(member));
        when(loanRepository.memberHasOverdueLoans(eq(2L), any())).thenReturn(false);
        when(fineRepository.existsByMemberIdAndPaidFalse(2L)).thenReturn(false);
        when(loanRepository.countByMemberIdAndStatus(2L, LoanStatus.ACTIVE)).thenReturn(3L);

        assertThatThrownBy(() -> loanService.registrarPrestamo(1L, 2L))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("máximo");
        verify(loanRepository, never()).save(any());
    }

    @Test
    void registrarDevolucion_fueraDePlazo_generaUnaMulta() {
        copy.setStatus(CopyStatus.ON_LOAN);
        // Vencido hace 4 días.
        Loan loan = new Loan(copy, member, LocalDate.now().minusDays(18), LocalDate.now().minusDays(4));
        when(loanRepository.findById(5L)).thenReturn(Optional.of(loan));

        loanService.registrarDevolucion(5L);

        // 4 días * 50 = 200.
        verify(fineRepository).save(argThat(f ->
                f.getDaysLate() == 4 && f.getAmount().compareTo(new BigDecimal("200.00")) == 0));
    }

    @Test
    void registrarDevolucion_enPlazo_noGeneraMulta() {
        copy.setStatus(CopyStatus.ON_LOAN);
        Loan loan = new Loan(copy, member, LocalDate.now().minusDays(3), LocalDate.now().plusDays(11));
        when(loanRepository.findById(5L)).thenReturn(Optional.of(loan));

        loanService.registrarDevolucion(5L);

        verify(fineRepository, never()).save(any());
    }

    @Test
    void renovar_prestamoActivoSinReserva_extiendeElPlazo() {
        Member owner = mock(Member.class);
        when(owner.getId()).thenReturn(2L);
        Loan loan = new Loan(copy, owner, LocalDate.now().minusDays(3), LocalDate.now().plusDays(11));
        LocalDate dueOriginal = loan.getDueDate();
        when(loanRepository.findById(5L)).thenReturn(Optional.of(loan));
        when(reservationRepository.existsByBookIdAndStatus(any(), eq(ReservationStatus.PENDING)))
                .thenReturn(false);

        LoanDTO dto = loanService.renovar(5L, owner);

        assertThat(dto.renewals()).isEqualTo(1);
        assertThat(dto.dueDate()).isEqualTo(dueOriginal.plusDays(14));
    }

    @Test
    void renovar_conReservaPendiente_lanzaBusinessRule() {
        Member owner = mock(Member.class);
        when(owner.getId()).thenReturn(2L);
        Loan loan = new Loan(copy, owner, LocalDate.now().minusDays(3), LocalDate.now().plusDays(11));
        when(loanRepository.findById(5L)).thenReturn(Optional.of(loan));
        when(reservationRepository.existsByBookIdAndStatus(any(), eq(ReservationStatus.PENDING)))
                .thenReturn(true);

        assertThatThrownBy(() -> loanService.renovar(5L, owner))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("reserva pendiente");
    }

    @Test
    void renovar_prestamoVencido_lanzaBusinessRule() {
        Member owner = mock(Member.class);
        when(owner.getId()).thenReturn(2L);
        Loan loan = new Loan(copy, owner, LocalDate.now().minusDays(20), LocalDate.now().minusDays(6));
        when(loanRepository.findById(5L)).thenReturn(Optional.of(loan));

        assertThatThrownBy(() -> loanService.renovar(5L, owner))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("vencido");
    }

    @Test
    void renovar_prestamoDeOtroSocio_lanzaBusinessRule() {
        Member owner = mock(Member.class);
        when(owner.getId()).thenReturn(2L);
        Member otro = mock(Member.class);
        when(otro.getId()).thenReturn(99L);
        Loan loan = new Loan(copy, owner, LocalDate.now().minusDays(3), LocalDate.now().plusDays(11));
        when(loanRepository.findById(5L)).thenReturn(Optional.of(loan));

        assertThatThrownBy(() -> loanService.renovar(5L, otro))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("no pertenece");
    }
}
