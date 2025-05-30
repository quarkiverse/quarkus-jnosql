package org.acme;

import jakarta.data.page.PageRequest;
import jakarta.data.repository.Delete;
import jakarta.data.repository.Find;
import jakarta.data.repository.Repository;
import jakarta.data.repository.Save;
import org.eclipse.jnosql.mapping.NoSQLRepository;

import java.util.List;

@Repository
public interface Hotel extends NoSQLRepository<Room, String> {

    @Save
    Room checkIn(Room room);

    @Delete
    void checkOut(Room room);

    @Find
    List<Room> getRooms(PageRequest pageRequest);
}