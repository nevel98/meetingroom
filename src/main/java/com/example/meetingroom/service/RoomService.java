package com.example.meetingroom.service;

import com.example.meetingroom.dto.RoomCreateRequest;
import com.example.meetingroom.entity.Room;
import com.example.meetingroom.exception.ConflictException;
import com.example.meetingroom.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class RoomService {
    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Transactional
    public Room create(RoomCreateRequest request) {
        if (roomRepository.existsByName(request.name())) {
            throw new ConflictException("Room name already exists: " + request.name());
        }
        Room room = new Room();
        room.setName(request.name());
        room.setCapacity(request.capacity());
        return roomRepository.save(room);
    }

    @Transactional(readOnly = true)
    public List<Room> findAll() {
        return roomRepository.findAll();
    }
}
