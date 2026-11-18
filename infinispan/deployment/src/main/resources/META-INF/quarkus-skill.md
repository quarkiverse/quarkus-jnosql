### Shared essentials

- Entities use `jakarta.nosql` annotations: `@Entity` on the class, `@Id` on the identifier field, `@Column` on mapped fields — Java records work. Use `@Embeddable` for nested objects (`FLAT` merges fields into the parent, `GROUPING` nests them under the property name).
- The `org.eclipse.jnosql.lite:mapping-lite-processor` MUST run during compilation on Java 21+ — on Maven, `maven-compiler-plugin` needs `annotationProcessorPaths` (`org.eclipse.jnosql.lite:mapping-lite-processor`) and `<annotationProcessors>` (`org.eclipse.jnosql.lite.mapping.EntityProcessor`, `org.eclipse.jnosql.lite.mapping.repository.RepositoryProcessor`). At least one `@Entity` is REQUIRED: without entity metadata every JNoSQL injection point fails with `UnsatisfiedResolutionException` for `EntitiesMetadata`. If that error appears, run a full clean build so the processors re-run.
- Never instantiate managers, factories, templates or clients manually — inject the CDI beans produced by the extension. On Gradle, exclude `org.eclipse.jnosql.mapping:jnosql-mapping-reflection` or injection fails with `AmbiguousResolutionException`.
- The `quarkus-jnosql-core` skill holds the full shared conventions (repository rules, `jnosql.<model>.database` names, staleness symptoms) — load it too when transitive skill scanning is enabled (`AGENT_MCP_SKILLS_INCLUDE_TRANSITIVE=true` on the Quarkus Agent MCP).

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

- Use `@QuarkusTest` with the embedded local-cache XML — no external service needed (`InfinispanTest` runs without Testcontainers).

### Common Pitfalls

- `findAll()` and derived queries are NOT supported — they throw `UnsupportedOperationException`. `BasicRepository` has no `existsById`; use `findById(id).isPresent()`.
- `jnosql.infinispan.host.*` or generic `jnosql.host.*` select Hot Rod remote mode and take precedence over the XML — leave them unset for embedded mode.
- Missing or invalid XML fails fast; there is no fallback to a default cache.
- In JNoSQL 1.1.19 `InfinispanBucketManagerFactory.close()` is a no-op — embedded container shutdown on dev-mode reload is an upstream limitation.
