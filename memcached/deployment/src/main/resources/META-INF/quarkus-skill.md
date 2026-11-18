### Shared essentials

- Entities use `jakarta.nosql` annotations: `@Entity` on the class, `@Id` on the identifier field, `@Column` on mapped fields — Java records work. Use `@Embeddable` for nested objects (`FLAT` merges fields into the parent, `GROUPING` nests them under the property name).
- The `org.eclipse.jnosql.lite:mapping-lite-processor` MUST run during compilation on Java 21+ — on Maven, `maven-compiler-plugin` needs `annotationProcessorPaths` (`org.eclipse.jnosql.lite:mapping-lite-processor`) and `<annotationProcessors>` (`org.eclipse.jnosql.lite.mapping.EntityProcessor`, `org.eclipse.jnosql.lite.mapping.repository.RepositoryProcessor`). At least one `@Entity` is REQUIRED: without entity metadata every JNoSQL injection point fails with `UnsatisfiedResolutionException` for `EntitiesMetadata`. If that error appears, run a full clean build so the processors re-run.
- Never instantiate managers, factories, templates or clients manually — inject the CDI beans produced by the extension. On Gradle, exclude `org.eclipse.jnosql.mapping:jnosql-mapping-reflection` or injection fails with `AmbiguousResolutionException`.
- The `quarkus-jnosql-core` skill holds the full shared conventions (repository rules, `jnosql.<model>.database` names, staleness symptoms) — load it too when transitive skill scanning is enabled (`AGENT_MCP_SKILLS_INCLUDE_TRANSITIVE=true` on the Quarkus Agent MCP).

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

### Default usage

```java
// entities stored through BucketManager must be Serializable
@Entity
public record Person(@Id String id, @Column String name) implements Serializable {}

// template — key-based CRUD
@Inject @Database(DatabaseType.KEY_VALUE) Template template;
template.insert(person);
template.find(Person.class, person.id());   // Optional<Person>

// repository — CRUD by key only (no findAll or derived queries)
@Repository
public interface PersonRepository extends BasicRepository<Person, String> {}
```

### Testing

- Use `@QuarkusTest` with a `QuarkusTestResourceLifecycleManager` starting `memcached` via Testcontainers and returning the mapped `host:port` in `jnosql.memcached.host.1` (see `MemcachedTestResource`).

### Common Pitfalls

- Only key-based operations are meaningful — do NOT assume durable persistence, queries, ordering, secondary indexes or transactions.
- `findAll()` and derived queries are NOT supported — they throw `UnsupportedOperationException`. `BasicRepository` has no `existsById`; use `findById(id).isPresent()`.
- In JNoSQL 1.1.18 the factory's `close()` is a no-op — the driver does not shut down its client, which can retain client threads across dev-mode reloads.
