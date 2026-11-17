package com.example.meetingroom.service;



import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.meetingroom.entity.Reservation;
import com.example.meetingroom.entity.Room;
import com.example.meetingroom.dto.ReservationCreateRequest;
import com.example.meetingroom.exception.ConflictException;
import com.example.meetingroom.repository.ReservationRepository;
import com.example.meetingroom.repository.RoomRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock private ReservationRepository reservationRepository;
    @Mock private RoomRepository roomRepository;
    @InjectMocks private ReservationService service;

    @Test
    void create_ShouldThrowConflict_WhenOverlapping() {
        var request = new ReservationCreateRequest(
            1L, "Juan", LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2)
        );
        Room room = new Room();
        room.setId(1L);
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(reservationRepository.findOverlappingReservation(any(), any(), any()))
            .thenReturn(Optional.of(new Reservation()));

        assertThatThrownBy(() -> service.create(request))
            .isInstanceOf(ConflictException.class)
            .hasMessageContaining("Conflict");
    }

    @Test
    void create_ShouldThrowBadRequest_WhenEndBeforeStart() {
        var request = new ReservationCreateRequest(
            1L, "Juan", LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(1)
        );
        assertThatThrownBy(() -> service.create(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("later");
    }
}
