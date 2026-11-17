package com.example.meetingroom.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import com.example.meetingroom.entity.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByName(String name);
    boolean existsByName(String name);
}
