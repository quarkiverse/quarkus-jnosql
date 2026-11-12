### Configuration

- `jnosql.column.database` is required — the ScyllaDB keyspace.
- Connection: `jnosql.scylladb.host.N` contact points (one shared `jnosql.scylladb.port`, default `9042`), `jnosql.scylladb.data.center` (default `datacenter1` — must match the cluster), optional `jnosql.scylladb.name`, `jnosql.scylladb.user`, `jnosql.scylladb.password`.
- Startup CQL can run through sorted `jnosql.scylladb.query.NNN` properties (lexicographic order), e.g. `jnosql.scylladb.query.000=CREATE KEYSPACE IF NOT EXISTS ...`.
- This extension uses the native ScyllaDB driver (`com.scylladb:java-driver-core`) — do NOT combine it with the Cassandra extension/client on the same classpath; the `com.datastax.oss.driver` classes overlap.

### Data access

- Inject `jakarta.nosql.Template` or `ColumnTemplate` with `@Inject @Database(DatabaseType.COLUMN)`.
- Inject `ScyllaDBColumnManager` for the low-level column API.
- Repositories: `@Repository` interface extending `org.eclipse.jnosql.mapping.NoSQLRepository<Entity, IdType>`.
- Keyspaces and tables must be provisioned before use; the driver's separate portable-CDI repository API is not part of this extension.

### Testing

- Use `@QuarkusTest` with a `QuarkusTestResourceLifecycleManager` starting `scylladb/scylla` via Testcontainers and returning the `jnosql.scylladb.*` properties (see `ScyllaDBTestResource`).

### Common Pitfalls

- Do NOT add `quarkus-jnosql-cassandra` or `cassandra-quarkus-client` to the same application — driver classes overlap and conflict.
- Do NOT expect schema auto-creation — create keyspaces/tables via `jnosql.scylladb.query.NNN` or externally.
- Custom managers from `ScyllaDBColumnManagerFactory.apply(keyspace)` are NOT closed by CDI — close them yourself.
