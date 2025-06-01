package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.data.page.PageRequest;
import jakarta.inject.Inject;
import net.datafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * <p>HotelTest is a test class for testing the Hotel operations including check-in, check-out,
 * and paginated room retrieval.</p>
 *
 * <p>It uses Quarkus Test framework and Faker library to generate random data for testing.</p>
 */
@QuarkusTest
class HotelTest {

    @Inject
    Hotel hotel;

    Faker faker = new Faker();

    @BeforeEach
    @AfterEach
    void resetDatabase() {
        // Clear the hotel database before each test
        hotel.deleteAll();
    }

    @Test
    @DisplayName("Test hotel operations including check-in, check-out, and paginated room retrieval")
    void testHotelOperations() {

        assertThat(hotel.getCheckedInRooms(PageRequest.ofPage(1).size(2)))
                .as("If no rooms are checked in, any paginated request should return an empty list.")
                .isEmpty();

        var room1 = new Room("101", new Guest(faker.passport().valid(), faker.name().fullName()));
        var room2 = new Room("102", new Guest(faker.passport().valid(), faker.name().fullName()));
        var room3 = new Room("103", new Guest(faker.passport().valid(), faker.name().fullName()));

        var rooms = List.of(room1, room2, room3);

        assertThat(hotel.checkIn(room1))
                .as("there should have been a check-in for room %s", room1)
                .isEqualTo(room1);
        assertThat(hotel.checkIn(room2))
                .as("there should have been a check-in for room %s", room2)
                .isEqualTo(room2);
        assertThat(hotel.checkIn(room3))
                .as("there should have been a check-in for room %s", room3)
                .isEqualTo(room3);

        assertThat(hotel.getCheckedInRooms(PageRequest.ofPage(1).size(2)))
                .as("Given a paginated request for page %d with %d items per page," +
                        " the response should return exactly %d checked-in rooms.", 1, 2, 2)
                .hasSize(2)
                .as("The response must include the checked-in rooms %s", Stream.of(room1, room2)
                        .map(Room::number).collect(Collectors.joining(", and ")))
                .containsExactly(room1, room2);

        assertThat(hotel.getCheckedInRooms(PageRequest.ofPage(2).size(2)))
                .as("When requesting page %d with %d items per page, " +
                        "the response must not include rooms already listed on the previous page.", 2, 2)
                .hasSize(1)
                .as("The response must include the checked-in room %s", room3.number())
                .containsExactly(room3);

        assertThat(hotel.getCheckedInRooms(PageRequest.ofPage(3).size(2)))
                .as("When requesting page %d with %d items per page, " +
                        "the result must include no checked-in rooms.", 3, 2)
                .hasSize(0);

        assertThat(hotel.getCheckedInRooms(PageRequest.ofPage(1).size(10)))
                .as("Given a paginated request for page %d with %d items per page, " +
                        "the response should return exactly %d checked-in rooms.", 1, 10, rooms.size())
                .hasSize(rooms.size())
                .as("The response must include the checked-in rooms %s.", rooms.stream()
                        .map(Room::number).collect(Collectors.joining(", and ")))
                .containsAll(rooms);

        assertThatCode(() -> hotel.checkOut(room1))
                .as("%s should be checked out in the hotel", room1)
                .doesNotThrowAnyException();

        rooms = List.of(room2, room3);

        assertThat(hotel.getCheckedInRooms(PageRequest.ofPage(1).size(10)))
                .as("Given a paginated request for page %d with %d items per page, " +
                        "the response should return exactly %d checked-in rooms.", 1, 10, rooms.size())
                .hasSize(rooms.size())
                .as("The response must include the checked-in rooms %s.", rooms.stream()
                        .map(Room::number).collect(Collectors.joining(", and ")))
                .containsAll(rooms);

        assertThat(hotel.getCheckedInRoomsByGuestDocument(room2.guest().document(), PageRequest.ofPage(1).size(2)))
                .as("Given a paginated request for page %d with %d items per page for given a guest document, " +
                        "the response should return exactly %d checked-in rooms.", 1, 2, 1)
                .hasSize(1)
                .as("The response must include the checked-in room %s", Stream.of(room2)
                        .map(Room::number).collect(Collectors.joining(", and ")))
                .containsExactly(room2);

        assertThat(hotel.getCheckedInRoomsByGuestDocument(faker.passport().valid(), PageRequest.ofPage(1).size(2)))
                .as("Given a paginated request for page %d with %d items per page for given a non registered guest document, " +
                        "any paginated request should return an empty list.", 1, 2)
                .isEmpty();
    }

}