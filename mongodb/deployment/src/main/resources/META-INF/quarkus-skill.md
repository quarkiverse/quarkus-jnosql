### Shared essentials

- Entities use `jakarta.nosql` annotations: `@Entity` on the class, `@Id` on the identifier field, `@Column` on mapped fields — Java records work. Use `@Embeddable` for nested objects (`FLAT` merges fields into the parent, `GROUPING` nests them under the property name).
- The `org.eclipse.jnosql.lite:mapping-lite-processor` MUST run during compilation on Java 21+ — on Maven, `maven-compiler-plugin` needs `annotationProcessorPaths` (`org.eclipse.jnosql.lite:mapping-lite-processor`) and `<annotationProcessors>` (`org.eclipse.jnosql.lite.mapping.EntityProcessor`, `org.eclipse.jnosql.lite.mapping.repository.RepositoryProcessor`). At least one `@Entity` is REQUIRED: without entity metadata every JNoSQL injection point fails with `UnsatisfiedResolutionException` for `EntitiesMetadata`. If that error appears, run a full clean build so the processors re-run.
- Never instantiate managers, factories, templates or clients manually — inject the CDI beans produced by the extension. On Gradle, exclude `org.eclipse.jnosql.mapping:jnosql-mapping-reflection` or injection fails with `AmbiguousResolutionException`.
- The `quarkus-jnosql-core` skill holds the full shared conventions (repository rules, `jnosql.<model>.database` names, staleness symptoms) — load it too when transitive skill scanning is enabled (`AGENT_MCP_SKILLS_INCLUDE_TRANSITIVE=true` on the Quarkus Agent MCP).

### Configuration

- `jnosql.document.database` is required — the MongoDB database name.
- The connection is owned by `quarkus-mongodb-client`: configure it with `quarkus.mongodb.*` properties (hosts/connection string, credentials, TLS). Dev Services starts a MongoDB container automatically in dev/test when no connection is configured.
- Do NOT set parallel `jnosql.mongodb.*` connection properties — the injected `MongoClient` already carries the connection.

### Data access

- Inject `jakarta.nosql.Template` or `DocumentTemplate` with `@Inject @Database(DatabaseType.DOCUMENT)` — the extension's beans carry that qualifier, so qualify every injection point (template, manager, repository).
- Inject `MongoDBDocumentManager` for the low-level document API.
- Repositories: `@Repository` interface extending `org.eclipse.jnosql.mapping.NoSQLRepository<Entity, IdType>` — derived queries are supported.

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

- Use `@QuarkusTest` — Dev Services provides the database; no test resource needed.
- The integration tests exercise POJO and record entities through the template, `DocumentManager` and Jakarta Data repositories, asserted via a JAX-RS `JNoSQLResource` with REST Assured.

### Common Pitfalls

- Do NOT create `MongoClient` or `MongoDBDocumentManagerFactory` manually — the extension shares the Quarkus-managed client.
