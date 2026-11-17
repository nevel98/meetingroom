package com.example.meetingroom.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.meetingroom.entity.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT r FROM Reservation r WHERE r.room.id = :roomId " +
           "AND r.startTime < :endTime AND r.endTime > :startTime")
    Optional<Reservation> findOverlappingReservation(
        @Param("roomId") Long roomId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );

    Page<Reservation> findByRoomId(Long roomId, Pageable pageable);

    @Query("SELECT r FROM Reservation r WHERE CAST(r.startTime AS date) = :date")
    Page<Reservation> findByDate(@Param("date") LocalDate date, Pageable pageable);

    @Query("SELECT r FROM Reservation r WHERE r.room.id = :roomId " +
           "AND CAST(r.startTime AS date) = :date")
    Page<Reservation> findByRoomIdAndDate(
        @Param("roomId") Long roomId,
        @Param("date") LocalDate date,
        Pageable pageable
    );
}