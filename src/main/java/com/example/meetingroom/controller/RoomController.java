package com.example.meetingroom.controller;

import com.example.meetingroom.dto.RoomCreateRequest;
import com.example.meetingroom.dto.RoomResponse;
import com.example.meetingroom.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping
    public ResponseEntity<RoomResponse> create(@Valid @RequestBody RoomCreateRequest request) {
        RoomResponse response = RoomResponse.from(roomService.create(request));
        return ResponseEntity.created(URI.create("/api/rooms/" + response.id())).body(response);
    }

    @GetMapping
    public ResponseEntity<List<RoomResponse>> list() {
        return ResponseEntity.ok(roomService.findAll().stream().map(RoomResponse::from).toList());
    }
}