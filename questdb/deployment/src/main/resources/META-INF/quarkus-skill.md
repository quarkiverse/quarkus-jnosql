### Configuration

- `jnosql.timeseries.database` is required — the QuestDB table name.
- Connection: `jnosql.questdb.url` in QuestDB ILP/WebSocket form, e.g. `ws::addr=localhost:9000;`.

### Entities

- `@Entity` classes (or records) whose `@Id` field is a temporal type such as `Instant` — the timestamp is the identity.

### Data access

- Inject `TimeSeriesTemplate` with `@Inject @Database(DatabaseType.TIME_SERIES)`.
- Repositories: `@Repository` interface extending `org.eclipse.jnosql.mapping.NoSQLRepository<Entity, Instant>`; inject with `@Database(DatabaseType.TIME_SERIES)`.
- Inject `QuestDBTimeSeriesManager` for the low-level time-series API.

### Testing

- Use `@QuarkusTest` with `QuestDBTestResource` (a `QuarkusTestResourceLifecycleManager` starting QuestDB via Testcontainers and returning `jnosql.questdb.url`).

### Common Pitfalls

- `update` is a no-op and `deleteById` is rejected on QuestDB — `insert`, `findById` and derived queries work.
- Ingestion is asynchronous: a just-written row may take a moment to become queryable — do not assert immediately after insert without retry/poll.
