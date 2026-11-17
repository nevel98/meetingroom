package com.example.meetingroom.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record ReservationCreateRequest(
    @NotNull(message = "roomId is required")
    Long roomId,

    @NotBlank(message = "responsible cannot be empty")
    String responsible,

    @NotNull(message = "startTime is required")
    @Future(message = "startTime must be in the future")
    LocalDateTime startTime,

    @NotNull(message = "endTime is required")
    @Future(message = "endTime must be in the future")
    LocalDateTime endTime
) {}
