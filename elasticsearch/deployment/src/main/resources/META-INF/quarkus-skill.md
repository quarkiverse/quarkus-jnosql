### Shared essentials

- Entities use `jakarta.nosql` annotations: `@Entity` on the class, `@Id` on the identifier field, `@Column` on mapped fields — Java records work. Use `@Embeddable` for nested objects (`FLAT` merges fields into the parent, `GROUPING` nests them under the property name).
- The `org.eclipse.jnosql.lite:mapping-lite-processor` MUST run during compilation on Java 21+ — on Maven, `maven-compiler-plugin` needs `annotationProcessorPaths` (`org.eclipse.jnosql.lite:mapping-lite-processor`) and `<annotationProcessors>` (`org.eclipse.jnosql.lite.mapping.EntityProcessor`, `org.eclipse.jnosql.lite.mapping.repository.RepositoryProcessor`). At least one `@Entity` is REQUIRED: without entity metadata every JNoSQL injection point fails with `UnsatisfiedResolutionException` for `EntitiesMetadata`. If that error appears, run a full clean build so the processors re-run.
- Never instantiate managers, factories, templates or clients manually — inject the CDI beans produced by the extension. On Gradle, exclude `org.eclipse.jnosql.mapping:jnosql-mapping-reflection` or injection fails with `AmbiguousResolutionException`.
- The `quarkus-jnosql-core` skill holds the full shared conventions (repository rules, `jnosql.<model>.database` names, staleness symptoms) — load it too when transitive skill scanning is enabled (`AGENT_MCP_SKILLS_INCLUDE_TRANSITIVE=true` on the Quarkus Agent MCP).

### Configuration

- `jnosql.document.database` is required — used as the index name.
- The connection is owned by `quarkus-elasticsearch-java-client`: configure hosts, credentials and TLS with `quarkus.elasticsearch.*` properties. The driver's `jnosql.elasticsearch.host/user/password` apply only when configuring the driver directly.
- Does NOT support native compilation — do not claim `-Dnative` support for this extension.

### Data access

- Inject `jakarta.nosql.Template` or `DocumentTemplate` with `@Inject @Database(DatabaseType.DOCUMENT)`.
- Inject `ElasticsearchDocumentManager` for the low-level document API.
- Repositories: `@Repository` interface extending `org.eclipse.jnosql.mapping.NoSQLRepository<Entity, IdType>`.

### Default usage

```java
@Entity
public record Car(@Id String id, @Column String model) {}

// template
@Inject @Database(DatabaseType.DOCUMENT) DocumentTemplate template;
template.insert(car);
template.find(Car.class, car.id());

// repository — Jakarta Data; the codestart's Garage shows the full pattern
// (@Save/@Delete/@Find methods with @By parameters)
@Repository
public interface CarRepository extends NoSQLRepository<Car, String> {
    @Find
    List<Car> findByModel(@By("model") String model);
}
```

### Testing

- Use `@QuarkusTest` with a `QuarkusTestResourceLifecycleManager` starting Elasticsearch via Testcontainers and returning the connection properties (see `ElasticsearchTestResource`).

### Common Pitfalls

- Do NOT create `ElasticsearchClient` or `ElasticsearchDocumentManagerFactory` manually — inject the CDI beans.
- Search-engine semantics differ from document stores: fields are indexed/analyzed, so assert query results accordingly rather than expecting exact document equality.
- Native image is unsupported — JVM mode only.
