package io.quarkiverse.jnosql.iotdb.it;

import java.time.Instant;

import jakarta.data.repository.Repository;

import org.eclipse.jnosql.mapping.NoSQLRepository;

@Repository
public interface SensorReadings extends NoSQLRepository<SensorReading, Instant> {
}
