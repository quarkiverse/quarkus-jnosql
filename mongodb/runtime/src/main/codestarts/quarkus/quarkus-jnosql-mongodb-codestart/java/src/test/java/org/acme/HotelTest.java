package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.data.page.PageRequest;
import jakarta.inject.Inject;
import net.datafaker.Faker;
import org.assertj.core.api.Assertions;
import org.eclipse.jnosql.mapping.Database;
import org.eclipse.jnosql.mapping.DatabaseType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class HotelTest {

    @Inject
    @Database(DatabaseType.DOCUMENT)
    Hotel hotel;

    Faker faker = new Faker();

    @BeforeEach
    @AfterEach
    void resetDatabase() {
        // Clear the hotel database before each test
        hotel.deleteAll();
    }

    @Test
    void testHelloEndpoint() {
        // Given the hotel database is empty, we expect to get an empty list of rooms

        var room1 = new Room("101", new Guest(faker.passport().toString(), faker.name().fullName()));
        var room2 = new Room("102", new Guest(faker.passport().toString(), faker.name().fullName()));
        var room3 = new Room("103", new Guest(faker.passport().toString(), faker.name().fullName()));

        var rooms = List.of(room1, room2, room3);

        assertThat(hotel.getRooms(PageRequest.ofPage(1).size(2)))
                .as("should there's no rooms in the hotel database")
                .isEmpty();

        // Check that the hotel database is empty
        assertThat(hotel.checkIn(room1))
                .as("should do the check-in of the room %s in the hotel database", room1)
                .isEqualTo(room1);
        assertThat(hotel.checkIn(room2))
                .as("should do the check-in of the room %s in the hotel database", room2)
                .isEqualTo(room2);
        assertThat(hotel.checkIn(room3))
                .as("should do the check-in ofk the room %s in the hotel database", room3)
                .isEqualTo(room3);

        assertThat(hotel.getRooms(PageRequest.ofPage(1).size(2)))
                .as("should return %d of %d rooms from the first page of the paged query in the hotel database", 2, 2)
                .hasSize(2);

        assertThat(hotel.getRooms(PageRequest.ofPage(2).size(2)))
                .as("should return %d of %d rooms from the first page of the paged query in the hotel database", 1, 2)
                .hasSize(1);

        assertThat(hotel.getRooms(PageRequest.ofPage(1).size(10)))
                .as("should return %d of %d rooms from the first page of the paged query in the hotel database", rooms.size(), 10)
                .hasSize(rooms.size())
                .as("should contains any of the checked-in rooms in the hotel database previously")
                .containsAll(rooms);

        // Check that the hotel database is empty
        Assertions.assertThatCode(() -> hotel.checkOut(room1))
                .as("should check out the room %s in the hotel database", room1)
                .doesNotThrowAnyException();

        rooms = List.of(room2, room3);

        assertThat(hotel.getRooms(PageRequest.ofPage(1).size(10)))
                .as("should return %d of %d rooms from the first page of the paged query in the hotel database", rooms.size(), 10)
                .hasSize(rooms.size())
                .as("should contains any of the checked-in rooms in the hotel database previously")
                .containsAll(rooms);

    }

}