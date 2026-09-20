package io.quarkiverse.jnosql.questdb.it;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;

import org.eclipse.jnosql.mapping.Database;
import org.eclipse.jnosql.mapping.DatabaseType;
import org.eclipse.jnosql.mapping.timeseries.TimeSeriesTemplate;

@Path("/jnosql")
@ApplicationScoped
public class JNoSQLResource {

    private static final AtomicLong TIMESTAMP = new AtomicLong(System.currentTimeMillis());

    @Inject
    @Database(DatabaseType.TIME_SERIES)
    TimeSeriesTemplate template;

    @Inject
    @Database(DatabaseType.TIME_SERIES)
    Metrics metrics;

    @GET
    @Path("/using-timeseries-template")
    public Metric fromTemplate() {
        Metric metric = new Metric(timestamp(), "template", 42.0);
        template.insert(metric);
        return await(() -> template.find(Metric.class, metric.getTimestamp()));
    }

    @GET
    @Path("/using-jakarta-data")
    public Metric fromRepository() {
        Metric metric = new Metric(timestamp(), "repository", 21.0);
        metrics.insert(metric);
        return await(() -> metrics.findById(metric.getTimestamp()));
    }

    private Instant timestamp() {
        return Instant.ofEpochMilli(TIMESTAMP.incrementAndGet());
    }

    private Metric await(Supplier<Optional<Metric>> lookup) {
        for (int attempt = 0; attempt < 50; attempt++) {
            Optional<Metric> metric = lookup.get();
            if (metric.isPresent()) {
                return metric.get();
            }
            try {
                Thread.sleep(100L);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Interrupted while waiting for QuestDB ingestion", exception);
            }
        }
        throw new NotFoundException();
    }
}
