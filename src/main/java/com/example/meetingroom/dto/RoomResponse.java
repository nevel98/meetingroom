package com.example.meetingroom.dto;

import com.example.meetingroom.entity.Room;

public record RoomResponse(
    Long id,
    String name,
    Integer capacity
) {
    public static RoomResponse from(Room room) {
        return new RoomResponse(room.getId(), room.getName(), room.getCapacity());
    }
}
