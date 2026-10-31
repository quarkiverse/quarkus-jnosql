package io.quarkiverse.jnosql.influxdb.it;

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
@QuarkusTestResource(InfluxDBTestResource.class)
class MetricsRepositoryTest {

    @Inject
    @Database(DatabaseType.TIME_SERIES)
    Metrics repository;

    @Test
    void shouldInsertAndFindById() {
        Instant id = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        repository.insert(new Metric(id, "repo-find", 1.5));

        Metric found = repository.findById(id).orElseThrow();
        assertEquals(id, found.getTimestamp());
        assertEquals("repo-find", found.getSource());
        assertEquals(1.5, found.getValue());
    }

    @Test
    void shouldQueryBySource() {
        String key = "repo-" + UUID.randomUUID();
        Instant id = Instant.now().truncatedTo(ChronoUnit.MILLIS).plusSeconds(60);
        repository.insert(new Metric(id, key, 7.0));

        var result = repository.findBySource(key);
        assertEquals(1, result.size());
        assertEquals(id, result.get(0).getTimestamp());
    }

    @Test
    void shouldHandleUpdate() {
        Instant id = Instant.now().truncatedTo(ChronoUnit.MILLIS).plusSeconds(120);
        repository.insert(new Metric(id, "repo-update", 1.0));

        // the provider rejects entity updates
        assertThrows(UnsupportedOperationException.class, () -> repository.update(new Metric(id, "repo-update", 2.0)));
        assertEquals(1.0, repository.findById(id).orElseThrow().getValue());
    }

    @Test
    void shouldRejectDeleteById() {
        Instant id = Instant.now().truncatedTo(ChronoUnit.MILLIS).plusSeconds(180);
        repository.insert(new Metric(id, "repo-delete", 1.0));
        assertTrue(repository.findById(id).isPresent());

        // the provider does not support row-level deletes
        assertThrows(UnsupportedOperationException.class, () -> repository.deleteById(id));
        assertTrue(repository.findById(id).isPresent());
    }
}
