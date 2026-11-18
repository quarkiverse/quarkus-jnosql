### Shared essentials

- Entities use `jakarta.nosql` annotations: `@Entity` on the class, `@Id` on the identifier field, `@Column` on mapped fields — Java records work. Use `@Embeddable` for nested objects (`FLAT` merges fields into the parent, `GROUPING` nests them under the property name).
- The `org.eclipse.jnosql.lite:mapping-lite-processor` MUST run during compilation on Java 21+ — on Maven, `maven-compiler-plugin` needs `annotationProcessorPaths` (`org.eclipse.jnosql.lite:mapping-lite-processor`) and `<annotationProcessors>` (`org.eclipse.jnosql.lite.mapping.EntityProcessor`, `org.eclipse.jnosql.lite.mapping.repository.RepositoryProcessor`). At least one `@Entity` is REQUIRED: without entity metadata every JNoSQL injection point fails with `UnsatisfiedResolutionException` for `EntitiesMetadata`. If that error appears, run a full clean build so the processors re-run.
- Never instantiate managers, factories, templates or clients manually — inject the CDI beans produced by the extension. On Gradle, exclude `org.eclipse.jnosql.mapping:jnosql-mapping-reflection` or injection fails with `AmbiguousResolutionException`.
- The `quarkus-jnosql-core` skill holds the full shared conventions (repository rules, `jnosql.<model>.database` names, staleness symptoms) — load it too when transitive skill scanning is enabled (`AGENT_MCP_SKILLS_INCLUDE_TRANSITIVE=true` on the Quarkus Agent MCP).

### Configuration

- `jnosql.document.database` is required — the Solr core/collection name.
- Connection: `jnosql.solr.host`, `jnosql.solr.user`, `jnosql.solr.password`, `jnosql.solr.automatic.commit` (whether writes are committed automatically).

### Data access

- Inject `jakarta.nosql.Template` or `DocumentTemplate` with `@Inject @Database(DatabaseType.DOCUMENT)`.
- Inject `SolrDocumentManager` for the low-level document API.
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

- Use `@QuarkusTest` with a `QuarkusTestResourceLifecycleManager` starting Solr via Testcontainers and returning the `jnosql.solr.*` properties (see `SolrTestResource`).

### Common Pitfalls

- The Solr core must exist before use — the extension does not create it.
- If documents are not visible after writes, check `jnosql.solr.automatic.commit` — Solr commits are explicit unless enabled.
