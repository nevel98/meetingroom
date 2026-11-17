package com.example.meetingroom.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RoomCreateRequest(
    @NotBlank(message = "name cannot be empty")
    @Size(max = 255, message = "name cannot exceed 255 characters")
    String name,

    @NotNull(message = "capacity is mandatory")
    @Min(value = 1, message = "capacity must be greater than 0")
    Integer capacity
) {}
