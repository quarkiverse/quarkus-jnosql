### Shared essentials

- Entities use `jakarta.nosql` annotations: `@Entity` on the class, `@Id` on the identifier field, `@Column` on mapped fields — Java records work. Use `@Embeddable` for nested objects (`FLAT` merges fields into the parent, `GROUPING` nests them under the property name).
- The `org.eclipse.jnosql.lite:mapping-lite-processor` MUST run during compilation on Java 21+ — on Maven, `maven-compiler-plugin` needs `annotationProcessorPaths` (`org.eclipse.jnosql.lite:mapping-lite-processor`) and `<annotationProcessors>` (`org.eclipse.jnosql.lite.mapping.EntityProcessor`, `org.eclipse.jnosql.lite.mapping.repository.RepositoryProcessor`). At least one `@Entity` is REQUIRED: without entity metadata every JNoSQL injection point fails with `UnsatisfiedResolutionException` for `EntitiesMetadata`. If that error appears, run a full clean build so the processors re-run.
- Never instantiate managers, factories, templates or clients manually — inject the CDI beans produced by the extension. On Gradle, exclude `org.eclipse.jnosql.mapping:jnosql-mapping-reflection` or injection fails with `AmbiguousResolutionException`.
- The `quarkus-jnosql-core` skill holds the full shared conventions (repository rules, `jnosql.<model>.database` names, staleness symptoms) — load it too when transitive skill scanning is enabled (`AGENT_MCP_SKILLS_INCLUDE_TRANSITIVE=true` on the Quarkus Agent MCP).

### Configuration

- `jnosql.graph.database` is required — the graph database name.
- The connection is owned by `quarkus-neo4j`: configure the URI and credentials with `quarkus.neo4j.uri` and `quarkus.neo4j.authentication.username`/`password`.

### Data access

- Inject `GraphTemplate` (or `jakarta.nosql.Template`) with `@Inject @Database(DatabaseType.GRAPH)`.
- Inject `Neo4JDatabaseManager` for the low-level graph API.
- Repositories: `@Repository` interface extending `jakarta.data.repository.BasicRepository<Entity, IdType>`; use `@Find` + `@By("property")` on parameters for custom queries (see the `MagazineRepository` codestart).
- Entities map to graph vertices; model relationships through entity fields the driver maps to edges — verify the mapping in the JNoSQL Neo4j driver docs.

### Default usage

```java
@Entity
public record Magazine(@Id String id, @Column("title") String title, @Column("edition") int edition) {}

// template
@Inject @Database(DatabaseType.GRAPH) GraphTemplate template;

// repository — @Find + @By for custom queries (see the MagazineRepository codestart)
@Repository
public interface MagazineRepository extends BasicRepository<Magazine, String> {
    @Find
    List<Magazine> findByTitle(@By("title") String title);
}
```

### Testing

- Use `@QuarkusTest`; the integration tests exercise `GraphTemplate` with POJO and record entities through a JAX-RS `JNoSQLResource`.

### Common Pitfalls

- Do NOT create the Neo4j `Driver` manually — it is produced by `quarkus-neo4j` and shared with the extension.
- Do NOT forget `jnosql.graph.database` — startup fails with `MappingException` when it is missing.
