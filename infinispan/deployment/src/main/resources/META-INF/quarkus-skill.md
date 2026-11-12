### Configuration

- `jnosql.keyvalue.database` is required — it selects the cache name; the cache must be defined in the Infinispan XML configuration.
- `jnosql.infinispan.config` points to an Infinispan XML file, resolved on the classpath first, then the filesystem (absolute paths work, e.g. `/etc/my-app/infinispan.xml`). A minimal local cache:

[source,xml]
----
<infinispan xmlns="urn:infinispan:config:16.0">
    <cache-container name="jnosql">
        <local-cache name="people"/>
    </cache-container>
</infinispan>
----

- The codestart ships this file and runs embedded with no server or Docker.

### Data access

- Inject `jakarta.nosql.Template` with `@Inject @Database(DatabaseType.KEY_VALUE)`.
- Inject `InfinispanBucketManager` (or `BucketManager`) for key-based access.
- Repositories extend `jakarta.data.repository.BasicRepository<Entity, IdType>` with `@Repository` — only key-based CRUD is supported.
- For custom managers created via `InfinispanBucketManagerFactory.apply(name)`, close them yourself — only the shared factory is managed by CDI.

### Testing

- Use `@QuarkusTest` with the embedded local-cache XML — no external service needed (`InfinispanTest` runs without Testcontainers).

### Common Pitfalls

- `findAll()` and derived queries are NOT supported — they throw `UnsupportedOperationException`. `BasicRepository` has no `existsById`; use `findById(id).isPresent()`.
- `jnosql.infinispan.host.*` or generic `jnosql.host.*` select Hot Rod remote mode and take precedence over the XML — leave them unset for embedded mode.
- Missing or invalid XML fails fast; there is no fallback to a default cache.
- In JNoSQL 1.1.19 `InfinispanBucketManagerFactory.close()` is a no-op — embedded container shutdown on dev-mode reload is an upstream limitation.
