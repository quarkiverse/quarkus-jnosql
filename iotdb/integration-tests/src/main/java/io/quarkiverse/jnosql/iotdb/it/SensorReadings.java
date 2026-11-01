package io.quarkiverse.jnosql.iotdb.it;

import java.time.Instant;
import java.util.List;

import jakarta.data.repository.Repository;

import org.eclipse.jnosql.mapping.NoSQLRepository;

@Repository
public interface SensorReadings extends NoSQLRepository<SensorReading, Instant> {

    List<SensorReading> findBySensor(String sensor);
}
