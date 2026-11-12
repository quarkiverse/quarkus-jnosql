### Configuration

- `jnosql.keyvalue.database` is required — it namespaces keys (logical database name).
- Connection and pool: `jnosql.valkey.host` (default `localhost`), `jnosql.valkey.port` (default `6379`), `jnosql.valkey.max.total`, `jnosql.valkey.max.idle`, `jnosql.valkey.min.idle`, `jnosql.valkey.password`, `jnosql.valkey.user`, `jnosql.valkey.ssl`, `jnosql.valkey.timeout`, `jnosql.valkey.database` (server-side DB index). Cluster/sentinel variants exist under `jnosql.valkey.cluster.*` and `jnosql.valkey.sentinel.*`.
- The extension shares one connection pool across the whole application.

### Data access

- Inject `jakarta.nosql.Template` with `@Inject @Database(DatabaseType.KEY_VALUE)`.
- Inject `ValkeyBucketManager` (or `BucketManager`) for key-based access: `bucketManager.put(key, entity)`, `bucketManager.get(key)`.
- Repositories extend `jakarta.data.repository.BasicRepository<Entity, IdType>` with `@Repository` — only key-based CRUD is supported.

### Testing

- Use `@QuarkusTest` with a `QuarkusTestResourceLifecycleManager` starting a Valkey container via Testcontainers and returning `jnosql.valkey.host`/`jnosql.valkey.port` mapped to the container (see `ValkeyTestResource`).

### Common Pitfalls

- `findAll()` and derived queries are NOT supported — they throw `UnsupportedOperationException`. `BasicRepository` has no `existsById`; use `findById(id).isPresent()`.
- Do NOT create the manager factory or pool manually — the extension owns the shared factory.
- `jnosql.valkey.database` (server DB index) and `jnosql.keyvalue.database` (JNoSQL namespace) are different properties.
