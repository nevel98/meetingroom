package com.example.meetingroom.repository;

import com.example.meetingroom.entity.Reservation;
import com.example.meetingroom.entity.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ReservationRepositoryTest {

    @Autowired private TestEntityManager em;
    @Autowired private ReservationRepository repository;

    private Room room;

    @BeforeEach
    void setUp() {
        room = new Room();
        room.setName("Sala Repo");
        room.setCapacity(6);
        em.persist(room);
    }

    private Reservation persist(String responsible, LocalDateTime start, LocalDateTime end) {
        Reservation r = new Reservation();
        r.setRoom(room);
        r.setResponsible(responsible);
        r.setStartTime(start);
        r.setEndTime(end);
        return em.persistAndFlush(r);
    }

    @Test
    void findOverlapping_ShouldReturnReservation_WhenTimesOverlap() {
        persist("Pedro",
            LocalDateTime.of(2025, 1, 1, 10, 0),
            LocalDateTime.of(2025, 1, 1, 12, 0));

        var result = repository.findOverlappingReservation(
            room.getId(),
            LocalDateTime.of(2025, 1, 1, 11, 0),
            LocalDateTime.of(2025, 1, 1, 13, 0)
        );

        assertThat(result).isPresent();
        assertThat(result.get().getResponsible()).isEqualTo("Pedro");
    }

    @Test
    void findOverlapping_ShouldReturnEmpty_WhenDifferentRoom() {
        persist("Pedro",
            LocalDateTime.of(2025, 1, 1, 10, 0),
            LocalDateTime.of(2025, 1, 1, 12, 0));

        Room other = new Room();
        other.setName("Sala B");
        other.setCapacity(4);
        em.persistAndFlush(other);

        var result = repository.findOverlappingReservation(
            other.getId(),
            LocalDateTime.of(2025, 1, 1, 10, 0),
            LocalDateTime.of(2025, 1, 1, 12, 0)
        );

        assertThat(result).isEmpty();
    }

    @Test
    void findByDate_ShouldReturnOnlyMatchingDay() {
        persist("A", LocalDateTime.of(2025, 3, 10, 9, 0), LocalDateTime.of(2025, 3, 10, 10, 0));
        persist("B", LocalDateTime.of(2025, 3, 11, 9, 0), LocalDateTime.of(2025, 3, 11, 10, 0));

        Page<Reservation> page = repository.findByDate(
            LocalDate.of(2025, 3, 10), PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0).getResponsible()).isEqualTo("A");
    }

    @Test
    void findByRoomId_ShouldReturnOnlyThatRoom() {
        persist("A", LocalDateTime.of(2025, 4, 1, 9, 0), LocalDateTime.of(2025, 4, 1, 10, 0));

        Room other = new Room();
        other.setName("Sala B");
        other.setCapacity(4);
        em.persistAndFlush(other);
        Reservation r2 = new Reservation();
        r2.setRoom(other);
        r2.setResponsible("B");
        r2.setStartTime(LocalDateTime.of(2025, 4, 1, 11, 0));
        r2.setEndTime(LocalDateTime.of(2025, 4, 1, 12, 0));
        em.persistAndFlush(r2);

        Page<Reservation> page = repository.findByRoomId(room.getId(), PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0).getResponsible()).isEqualTo("A");
    }

    @Test
    void findByRoomIdAndDate_ShouldCombineBothFilters() {
        persist("A", LocalDateTime.of(2025, 5, 1, 9, 0), LocalDateTime.of(2025, 5, 1, 10, 0));
        persist("B", LocalDateTime.of(2025, 5, 2, 9, 0), LocalDateTime.of(2025, 5, 2, 10, 0));

        Page<Reservation> page = repository.findByRoomIdAndDate(
            room.getId(), LocalDate.of(2025, 5, 1), PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0).getResponsible()).isEqualTo("A");
    }
}