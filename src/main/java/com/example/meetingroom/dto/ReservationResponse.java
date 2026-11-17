package com.example.meetingroom.dto;

import com.example.meetingroom.entity.Reservation;
import java.time.LocalDateTime;

public record ReservationResponse(
    Long id,
    Long roomId,
    String roomName,
    String responsible,
    LocalDateTime startTime,
    LocalDateTime endTime
) {
    public static ReservationResponse from(Reservation r) {
        return new ReservationResponse(
            r.getId(),
            r.getRoom().getId(),
            r.getRoom().getName(),
            r.getResponsible(),
            r.getStartTime(),
            r.getEndTime()
        );
    }
}
