package org.acme;

import jakarta.data.Sort;
import jakarta.data.page.PageRequest;
import jakarta.data.repository.*;
import org.eclipse.jnosql.mapping.NoSQLRepository;

import java.util.List;

/**
 * <p>Hotel is an interface for managing room check-ins and check-outs.</p>
 *
 * <p>It's using the Jakarta Data Specification capabilities.</p>
 *
 * <p></p>
 * <p>To use it, you need to inject it in your application like below:</p>
 *
 * <pre>
 * // By field injection
 * &#64Inject
 * Hotel hotel;
 *
 * // By constructor injection
 * &#64Inject
 * SomeService(Hotel hotel) {
 * </pre>
 *
 * <p>Example usage:</p>
 * <pre>
 * &#64Inject
 * Hotel hotel;
 * ...
 * Room room=...
 * room = hotel.checkIn(room);
 * ...
 * hotel.checkOut(room);
 * ...
 * </pre>
 */
@Repository
public interface Hotel extends NoSQLRepository<Room, String> {

    /**
     * Checks in a room by saving it to the repository.
     *
     * @param room the room to be checked in
     * @return the saved room
     */
    @Save
    Room checkIn(Room room);

    /**
     * Checks out a room by removing it from the repository.
     *
     * @param room the room to be checked out
     */
    @Delete
    void checkOut(Room room);

    /**
     * Retrieves a paginated list of rooms ordered by their number.
     *
     * @param pageRequest the pagination request containing page and size information
     * @return a list of rooms for the specified page
     */
    @Find
    @OrderBy("number")
    List<Room> getCheckedInRooms(PageRequest pageRequest);

    /**
     * Retrieves a paginated list of rooms ordered by their number and sorted by the specified sort criteria.
     *
     * @param pageRequest the pagination request containing page and size information
     * @param sorts       the sorting criteria to apply
     * @return a list of rooms for the specified page, sorted according to the provided criteria
     */
    @Find
    List<Room> getCheckedInRoomsByGuestDocument(@By("guest.document") String guestDocument, PageRequest pageRequest, Sort... sorts);
}