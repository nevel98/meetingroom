package com.example.meetingroom.service;

import com.example.meetingroom.dto.RoomCreateRequest;
import com.example.meetingroom.entity.Room;
import com.example.meetingroom.exception.ConflictException;
import com.example.meetingroom.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock private RoomRepository roomRepository;
    @InjectMocks private RoomService roomService;

    @Test
    void create_ShouldSaveRoom_WhenNameIsUnique() {
        var request = new RoomCreateRequest("Sala A", 8);
        when(roomRepository.existsByName("Sala A")).thenReturn(false);
        when(roomRepository.save(any(Room.class))).thenAnswer(inv -> {
            Room r = inv.getArgument(0);
            r.setId(1L);
            return r;
        });

        Room saved = roomService.create(request);

        assertThat(saved.getId()).isEqualTo(1L);
        assertThat(saved.getName()).isEqualTo("Sala A");
    }

    @Test
    void create_ShouldThrowConflict_WhenNameExists() {
        when(roomRepository.existsByName("Sala A")).thenReturn(true);
        var request = new RoomCreateRequest("Sala A", 8);

        assertThatThrownBy(() -> roomService.create(request))
            .isInstanceOf(ConflictException.class);
    }
}
