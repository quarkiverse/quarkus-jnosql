package io.quarkiverse.jnosql.iotdb.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import jakarta.inject.Inject;

import org.eclipse.jnosql.mapping.Database;
import org.eclipse.jnosql.mapping.DatabaseType;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@QuarkusTestResource(IoTDBTestResource.class)
class SensorReadingsRepositoryTest {

    @Inject
    @Database(DatabaseType.TIME_SERIES)
    SensorReadings repository;

    @Test
    void shouldInsertAndFindById() {
        Instant id = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        repository.insert(new SensorReading(id, "repo-find", 1.5));

        SensorReading found = repository.findById(id).orElseThrow();
        assertEquals(id, found.getTimestamp());
        assertEquals("repo-find", found.getSensor());
        assertEquals(1.5, found.getValue());
    }

    @Test
    void shouldQueryBySensor() {
        String key = "repo-" + UUID.randomUUID();
        Instant id = Instant.now().truncatedTo(ChronoUnit.MILLIS).plusSeconds(60);
        repository.insert(new SensorReading(id, key, 7.0));

        var result = repository.findBySensor(key);
        assertEquals(1, result.size());
        assertEquals(id, result.get(0).getTimestamp());
    }

    @Test
    void shouldHandleUpdate() {
        Instant id = Instant.now().truncatedTo(ChronoUnit.MILLIS).plusSeconds(120);
        repository.insert(new SensorReading(id, "repo-update", 1.0));

        // the provider rejects entity updates
        assertThrows(UnsupportedOperationException.class, () -> repository.update(new SensorReading(id, "repo-update", 2.0)));
        assertEquals(1.0, repository.findById(id).orElseThrow().getValue());
    }

    @Test
    void shouldDeleteById() {
        Instant id = Instant.now().truncatedTo(ChronoUnit.MILLIS).plusSeconds(180);
        repository.insert(new SensorReading(id, "repo-delete", 1.0));
        assertTrue(repository.findById(id).isPresent());

        repository.deleteById(id);
        assertTrue(repository.findById(id).isEmpty());
    }
}
