### Configuration

- `jnosql.keyvalue.database` is required — it namespaces keys (logical bucket name; it does not create a durable database).
- Servers: `jnosql.memcached.host.N` entries (`host:port`), e.g. `jnosql.memcached.host.1=localhost:11211`; additional servers use `.2`, `.3`, etc. Optional driver properties include `jnosql.memcached.user`, `jnosql.memcached.password`, `jnosql.memcached.timeout`, `jnosql.memcached.protocol`.

### Data access

- Inject `jakarta.nosql.Template` with `@Inject @Database(DatabaseType.KEY_VALUE)`.
- Inject `MemcachedBucketManager` (or `BucketManager`) for key-based access.
- Repositories extend `jakarta.data.repository.BasicRepository<Entity, IdType>` with `@Repository` — only key-based CRUD is supported.

### Serialization

- The driver's default serialization requires stored entities to implement `java.io.Serializable`.
- In native images, entity hierarchies, `String` and `ArrayList` are pre-registered; other concrete types stored through `BucketManager` (or used in polymorphic fields/collections) need `@RegisterForReflection(serialization = true, targets = {...})`.

### Testing

- Use `@QuarkusTest` with a `QuarkusTestResourceLifecycleManager` starting `memcached` via Testcontainers and returning the mapped `host:port` in `jnosql.memcached.host.1` (see `MemcachedTestResource`).

### Common Pitfalls

- Only key-based operations are meaningful — do NOT assume durable persistence, queries, ordering, secondary indexes or transactions.
- `findAll()` and derived queries are NOT supported — they throw `UnsupportedOperationException`. `BasicRepository` has no `existsById`; use `findById(id).isPresent()`.
- In JNoSQL 1.1.18 the factory's `close()` is a no-op — the driver does not shut down its client, which can retain client threads across dev-mode reloads.
