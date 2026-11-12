### Configuration

- `jnosql.timeseries.database` is required — the InfluxDB bucket/database name.
- Connection: `jnosql.influxdb.url` (e.g. `http://localhost:8181`) and `jnosql.influxdb.token`.

### Entities

- `@Entity` classes (or records) whose `@Id` field is a temporal type such as `Instant` — the timestamp is the identity. Truncate to the precision the provider stores (tests use `ChronoUnit.MILLIS`).

### Data access

- Inject `TimeSeriesTemplate` with `@Inject @Database(DatabaseType.TIME_SERIES)`.
- Repositories: `@Repository` interface extending `org.eclipse.jnosql.mapping.NoSQLRepository<Entity, Instant>`; inject with `@Database(DatabaseType.TIME_SERIES)`.
- Inject `InfluxDBTimeSeriesManager` for the low-level time-series API.

### Testing

- Use `@QuarkusTest` with `InfluxDBTestResource` (a `QuarkusTestResourceLifecycleManager` starting InfluxDB via Testcontainers and returning `jnosql.influxdb.url`/`token`).

### Common Pitfalls

- `update` is rejected by InfluxDB — do not use repository `update`; `deleteById` is also rejected. `insert`, `findById` and derived queries work.
- Points are immutable once written — model corrections as new points, not updates.
