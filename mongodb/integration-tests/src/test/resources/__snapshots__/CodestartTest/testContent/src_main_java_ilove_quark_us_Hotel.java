package ilove.quark.us;

import jakarta.data.page.PageRequest;
import jakarta.data.repository.Delete;
import jakarta.data.repository.Find;
import jakarta.data.repository.Repository;
import jakarta.data.repository.Save;
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

    @Save
    Room checkIn(Room room);

    @Delete
    void checkOut(Room room);

    @Find
    List<Room> getRooms(PageRequest pageRequest);
}