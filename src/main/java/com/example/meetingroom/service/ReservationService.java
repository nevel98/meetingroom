package com.example.meetingroom.service;

import com.example.meetingroom.dto.ReservationCreateRequest;
import com.example.meetingroom.entity.Reservation;
import com.example.meetingroom.entity.Room;
import com.example.meetingroom.exception.ConflictException;
import com.example.meetingroom.exception.ResourceNotFoundException;
import com.example.meetingroom.repository.ReservationRepository;
import com.example.meetingroom.repository.RoomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;

    public ReservationService(ReservationRepository reservationRepository, RoomRepository roomRepository) {
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
    }

    @Transactional
    public Reservation create(ReservationCreateRequest request) {
        if (!request.endTime().isAfter(request.startTime())) {
            throw new IllegalArgumentException("The end time must be later than the start time.");
        }

        Room room = roomRepository.findById(request.roomId())
            .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + request.roomId()));

        reservationRepository.findOverlappingReservation(
            request.roomId(), request.startTime(), request.endTime()
        ).ifPresent(r -> {
            throw new ConflictException(
                "The room is already booked for that time. Conflict with reservation ID: " + r.getId()
            );
        });

        Reservation reservation = new Reservation();
        reservation.setRoom(room);
        reservation.setResponsible(request.responsible());
        reservation.setStartTime(request.startTime());
        reservation.setEndTime(request.endTime());
        return reservationRepository.save(reservation);
    }

    @Transactional(readOnly = true)
    public Page<Reservation> findAll(Long roomId, LocalDate date, Pageable pageable) {
        if (roomId != null && date != null) {
            return reservationRepository.findByRoomIdAndDate(roomId, date, pageable);
        }
        if (roomId != null) {
            return reservationRepository.findByRoomId(roomId, pageable);
        }
        if (date != null) {
            return reservationRepository.findByDate(date, pageable);
        }
        return reservationRepository.findAll(pageable);
    }

    @Transactional
    public void delete(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Reservation not found with ID: " + id);
        }
        reservationRepository.deleteById(id);
    }
}