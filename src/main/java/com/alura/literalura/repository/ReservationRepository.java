package com.alura.literalura.repository;

import com.alura.literalura.model.Reservation;
import com.alura.literalura.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByMemberIdOrderByReservationDateDesc(Long memberId);

    List<Reservation> findByStatusOrderByReservationDateAsc(ReservationStatus status);

    boolean existsByMemberIdAndBookIdAndStatus(Long memberId, Long bookId, ReservationStatus status);

    boolean existsByBookIdAndStatus(Long bookId, ReservationStatus status);
}
