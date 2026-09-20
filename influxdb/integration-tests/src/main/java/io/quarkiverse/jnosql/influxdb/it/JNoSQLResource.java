package io.quarkiverse.jnosql.influxdb.it;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

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
        return template.find(Metric.class, metric.getTimestamp()).orElseThrow(NotFoundException::new);
    }

    @GET
    @Path("/using-jakarta-data")
    public Metric fromRepository() {
        Metric metric = new Metric(timestamp(), "repository", 21.0);
        metrics.insert(metric);
        return metrics.findById(metric.getTimestamp()).orElseThrow(NotFoundException::new);
    }

    private Instant timestamp() {
        return Instant.now().truncatedTo(ChronoUnit.MILLIS);
    }
}
