### Configuration

- `jnosql.keyvalue.database` is required — it namespaces keys in the Hazelcast map.
- The connection is owned by `quarkus-hazelcast-client`: configure cluster members with `quarkus.hazelcast-client.cluster-members` (e.g. `localhost:5701`) and credentials/TLS through the `quarkus.hazelcast-client.*` properties of that extension.
- The extension injects the CDI `HazelcastInstance` produced by the Quarkus Hazelcast client extension; the driver's own `jnosql.hazelcast.*` properties are not used.

### Data access

- Inject `jakarta.nosql.Template` with `@Inject @Database(DatabaseType.KEY_VALUE)`.
- Inject `HazelcastBucketManager` (or `BucketManager`) for key-based access.
- Repositories extend `jakarta.data.repository.BasicRepository<Entity, IdType>` with `@Repository` — only key-based CRUD is supported.

### Testing

- Use `@QuarkusTest` with `HazelcastServerTestResource` (`io.quarkus.test.hazelcast`) as `@QuarkusTestResource` — it starts a Hazelcast member; set `quarkus.hazelcast-client.cluster-members` in `application.properties`.

### Common Pitfalls

- `findAll()` and derived queries are NOT supported — they throw `UnsupportedOperationException`. `BasicRepository` has no `existsById`; use `findById(id).isPresent()`.
- Do NOT create `HazelcastInstance` or `HazelcastClient` manually — inject the CDI bean from `quarkus-hazelcast-client`.
- Do NOT set `jnosql.hazelcast.host`/`port` expecting them to take effect — the injected `HazelcastInstance` already carries the connection.
