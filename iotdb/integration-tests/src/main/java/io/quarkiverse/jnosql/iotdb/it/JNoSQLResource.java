package io.quarkiverse.jnosql.iotdb.it;

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
    SensorReadings readings;

    @GET
    @Path("/using-timeseries-template")
    public SensorReading fromTemplate() {
        SensorReading reading = new SensorReading(timestamp(), "template", 42.0);
        template.insert(reading);
        return template.find(SensorReading.class, reading.getTimestamp()).orElseThrow(NotFoundException::new);
    }

    @GET
    @Path("/using-jakarta-data")
    public SensorReading fromRepository() {
        SensorReading reading = new SensorReading(timestamp(), "repository", 21.0);
        readings.insert(reading);
        return readings.findById(reading.getTimestamp()).orElseThrow(NotFoundException::new);
    }

    private Instant timestamp() {
        return Instant.now().truncatedTo(ChronoUnit.MILLIS);
    }
}
