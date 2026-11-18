### Shared essentials

- Entities use `jakarta.nosql` annotations: `@Entity` on the class, `@Id` on the identifier field, `@Column` on mapped fields — Java records work. Use `@Embeddable` for nested objects (`FLAT` merges fields into the parent, `GROUPING` nests them under the property name).
- The `org.eclipse.jnosql.lite:mapping-lite-processor` MUST run during compilation on Java 21+ — on Maven, `maven-compiler-plugin` needs `annotationProcessorPaths` (`org.eclipse.jnosql.lite:mapping-lite-processor`) and `<annotationProcessors>` (`org.eclipse.jnosql.lite.mapping.EntityProcessor`, `org.eclipse.jnosql.lite.mapping.repository.RepositoryProcessor`). At least one `@Entity` is REQUIRED: without entity metadata every JNoSQL injection point fails with `UnsatisfiedResolutionException` for `EntitiesMetadata`. If that error appears, run a full clean build so the processors re-run.
- Never instantiate managers, factories, templates or clients manually — inject the CDI beans produced by the extension. On Gradle, exclude `org.eclipse.jnosql.mapping:jnosql-mapping-reflection` or injection fails with `AmbiguousResolutionException`.
- The `quarkus-jnosql-core` skill holds the full shared conventions (repository rules, `jnosql.<model>.database` names, staleness symptoms) — load it too when transitive skill scanning is enabled (`AGENT_MCP_SKILLS_INCLUDE_TRANSITIVE=true` on the Quarkus Agent MCP).

### Configuration

- `jnosql.keyvalue.database` is required — it namespaces keys (logical database name).
- Connection and pool: `jnosql.valkey.host` (default `localhost`), `jnosql.valkey.port` (default `6379`), `jnosql.valkey.max.total`, `jnosql.valkey.max.idle`, `jnosql.valkey.min.idle`, `jnosql.valkey.password`, `jnosql.valkey.user`, `jnosql.valkey.ssl`, `jnosql.valkey.timeout`, `jnosql.valkey.database` (server-side DB index). Cluster/sentinel variants exist under `jnosql.valkey.cluster.*` and `jnosql.valkey.sentinel.*`.
- The extension shares one connection pool across the whole application.

### Data access

- Inject `jakarta.nosql.Template` with `@Inject @Database(DatabaseType.KEY_VALUE)`.
- Inject `ValkeyBucketManager` (or `BucketManager`) for key-based access: `bucketManager.put(key, entity)`, `bucketManager.get(key)`.
- Repositories extend `jakarta.data.repository.BasicRepository<Entity, IdType>` with `@Repository` — only key-based CRUD is supported.

### Default usage

```java
@Entity
public record Person(@Id String id, @Column String name) {}

// template — key-based CRUD
@Inject @Database(DatabaseType.KEY_VALUE) Template template;
template.insert(person);
template.find(Person.class, person.id());   // Optional<Person>

// repository — CRUD by key only (no findAll or derived queries)
@Repository
public interface PersonRepository extends BasicRepository<Person, String> {}
```

### Testing

- Use `@QuarkusTest` with a `QuarkusTestResourceLifecycleManager` starting a Valkey container via Testcontainers and returning `jnosql.valkey.host`/`jnosql.valkey.port` mapped to the container (see `ValkeyTestResource`).

### Common Pitfalls

- `findAll()` and derived queries are NOT supported — they throw `UnsupportedOperationException`. `BasicRepository` has no `existsById`; use `findById(id).isPresent()`.
- Do NOT create the manager factory or pool manually — the extension owns the shared factory.
- `jnosql.valkey.database` (server DB index) and `jnosql.keyvalue.database` (JNoSQL namespace) are different properties.
