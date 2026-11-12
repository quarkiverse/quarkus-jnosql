### Configuration

- `jnosql.timeseries.database` is required — the IoTDB database name.
- Connection: `jnosql.iotdb.host`, `jnosql.iotdb.port` (default `6667`), `jnosql.iotdb.username`, `jnosql.iotdb.password`, `jnosql.iotdb.enable.redirection` (set `false` for standalone).

### Entities

- `@Entity` classes (or records) whose `@Id` field is a temporal type such as `Instant` — the timestamp is the identity.

### Data access

- Inject `TimeSeriesTemplate` with `@Inject @Database(DatabaseType.TIME_SERIES)`.
- Repositories: `@Repository` interface extending `org.eclipse.jnosql.mapping.NoSQLRepository<Entity, Instant>`; inject with `@Database(DatabaseType.TIME_SERIES)`.
- Inject `IoTDBTimeSeriesManager` for the low-level time-series API.

### Testing

- Use `@QuarkusTest` with `IoTDBTestResource` (a `QuarkusTestResourceLifecycleManager` starting Apache IoTDB via Testcontainers and returning the `jnosql.iotdb.*` properties).

### Common Pitfalls

- `update` is rejected by IoTDB — do not use repository `update`. `insert`, `findById`, `deleteById` and derived queries work.
- IoTDB organizes data into tree-structured devices — entity field names map to measurement paths; check the driver docs for naming constraints.
