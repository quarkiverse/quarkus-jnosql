### Configuration

- `jnosql.keyvalue.database` is required — it namespaces keys in Redis (logical database name, not a Redis SELECT index).
- Connection and pool: `jnosql.redis.host` (default `localhost`), `jnosql.redis.port` (default `6379`), `jnosql.redis.max.total`, `jnosql.redis.max.idle`, `jnosql.redis.min.idle`, `jnosql.redis.password`, `jnosql.redis.user`, `jnosql.redis.ssl`, `jnosql.redis.timeout`, `jnosql.redis.database` (Redis logical DB index). Cluster/sentinel variants exist under `jnosql.redis.cluster.*` and `jnosql.redis.sentinel.*`.
- The extension shares one Jedis connection pool across the whole application.

### Data access

- Inject `jakarta.nosql.Template` with `@Inject @Database(DatabaseType.KEY_VALUE)`.
- Inject `RedisBucketManager` (or `BucketManager`) for key-based access: `bucketManager.put(key, entity)`, `bucketManager.get(key).map(v -> v.get(Person.class))`.
- Repositories extend `jakarta.data.repository.BasicRepository<Entity, IdType>` with `@Repository` — only key-based CRUD is supported.

### Testing

- Use `@QuarkusTest` with a `QuarkusTestResourceLifecycleManager` starting `redis` via Testcontainers and returning `jnosql.redis.host`/`jnosql.redis.port` mapped to the container (see `RedisTestResource`).
- The integration tests exercise template and `BucketManager` with POJOs and records, plus `PersonBasicRepositoryTest` for repository operations.

### Common Pitfalls

- `findAll()` and derived queries are NOT supported — they throw `UnsupportedOperationException`. `BasicRepository` has no `existsById`; use `findById(id).isPresent()`.
- Do NOT create `RedisBucketManagerFactory` or `JedisPool` manually — the extension owns the single shared factory and pool.
- `jnosql.redis.database` (Redis SELECT index) and `jnosql.keyvalue.database` (JNoSQL namespace) are different properties — do not confuse them.
