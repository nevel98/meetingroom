package com.example.meetingroom.repository;

import com.example.meetingroom.entity.Room;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class RoomRepositoryTest {

    @Autowired private RoomRepository repository;

    @Test
    void saveAndFindByName_ShouldWork() {
        Room room = new Room();
        room.setName("Sala Test");
        room.setCapacity(6);
        repository.save(room);

        assertThat(repository.findByName("Sala Test")).isPresent();
        assertThat(repository.existsByName("Sala Test")).isTrue();
        assertThat(repository.existsByName("No Existe")).isFalse();
    }
}