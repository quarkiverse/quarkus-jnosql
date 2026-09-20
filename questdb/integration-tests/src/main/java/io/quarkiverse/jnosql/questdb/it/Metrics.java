package io.quarkiverse.jnosql.questdb.it;

import java.time.Instant;

import jakarta.data.repository.Repository;

import org.eclipse.jnosql.mapping.NoSQLRepository;

@Repository
public interface Metrics extends NoSQLRepository<Metric, Instant> {
}
