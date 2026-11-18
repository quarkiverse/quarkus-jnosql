### Shared essentials

- Entities use `jakarta.nosql` annotations: `@Entity` on the class, `@Id` on the identifier field, `@Column` on mapped fields — Java records work. Use `@Embeddable` for nested objects (`FLAT` merges fields into the parent, `GROUPING` nests them under the property name).
- The `org.eclipse.jnosql.lite:mapping-lite-processor` MUST run during compilation on Java 21+ — on Maven, `maven-compiler-plugin` needs `annotationProcessorPaths` (`org.eclipse.jnosql.lite:mapping-lite-processor`) and `<annotationProcessors>` (`org.eclipse.jnosql.lite.mapping.EntityProcessor`, `org.eclipse.jnosql.lite.mapping.repository.RepositoryProcessor`). At least one `@Entity` is REQUIRED: without entity metadata every JNoSQL injection point fails with `UnsatisfiedResolutionException` for `EntitiesMetadata`. If that error appears, run a full clean build so the processors re-run.
- Never instantiate managers, factories, templates or clients manually — inject the CDI beans produced by the extension. On Gradle, exclude `org.eclipse.jnosql.mapping:jnosql-mapping-reflection` or injection fails with `AmbiguousResolutionException`.
- The `quarkus-jnosql-core` skill holds the full shared conventions (repository rules, `jnosql.<model>.database` names, staleness symptoms) — load it too when transitive skill scanning is enabled (`AGENT_MCP_SKILLS_INCLUDE_TRANSITIVE=true` on the Quarkus Agent MCP).

### Vector entities

- Entities are regular `@Entity` classes (or records) with a `DenseVector` field mapped with `@Column`, plus `@Id` on the identifier.
- The JNoSQL Lite annotation processor requires a setter for every mapped field, including the vector field — records satisfy this through their canonical constructor.
- Only `DenseVector` is exercised by the integration tests; other vector representations depend on each driver.

### Configuration

- `jnosql.vector.database` is required — the producer throws `MappingException` when it is missing or blank.
- `jnosql.vector.provider` selects the `DatabaseConfiguration` implementation and is required only when more than one JNoSQL vector provider is on the classpath, for example `org.eclipse.jnosql.databases.qdrant.communication.QdrantConfiguration`.
- Each database extension adds its own `jnosql.<db>.*` connection properties (host, port, collection distance/vector size, credentials). See the database-specific guide.

### Data access

- Inject `VectorTemplate` with `@Inject @Database(DatabaseType.VECTOR)`.
- Persist with `template.insert(entity)` or `template.upsert(entity)`; read with `template.find(Entity.class, id)`.
- Similarity search: `template.search(Entity.class).vector(DenseVector.of(...)).where("field").eq(value).limit(Limit.of(n)).result()`.
- Jakarta Data works: declare `@Repository` interfaces extending `BasicRepository<Entity, String>` — `save` (insert/update), `findById`, `deleteById` and derived queries like `findByCategory` are verified against each database.

### Default usage

```java
@Entity
public class Article {
    @Id private String id;
    @Column private String title;
    @Column private DenseVector embedding;   // the Lite processor needs a setter
}

// template
@Inject @Database(DatabaseType.VECTOR) VectorTemplate template;
template.insert(article);                     // or upsert
template.find(Article.class, article.id());

// similarity search
template.search(Article.class)
        .vector(DenseVector.of(0.1f, 0.2f, 0.3f))
        .where("category").eq("news")
        .limit(Limit.of(5))
        .result();

// repository — derived queries work where the driver supports them
@Repository
public interface ArticleRepository extends BasicRepository<Article, String> {
    List<Article> findByCategory(String category);
}
```

### Testing

- Use `@QuarkusTest` with a `QuarkusTestResourceLifecycleManager` starting the database via Testcontainers (see `QdrantTestResource`, `MilvusTestResource`, `WeaviateTestResource`).
- Pin the database image version in the test resource, as the existing resources do.
- Verify save/insert, findById, deleteById and one derived query plus a similarity search through `VectorTemplate`.

### Common Pitfalls

- Do NOT forget `jnosql.vector.database` — the application fails at startup without it.
- Do NOT rely on a single provider when several vector extensions are on the classpath; set `jnosql.vector.provider` explicitly.
- Vector dimensions and collection creation are driver-specific (Qdrant creates the collection on first insert when a distance is configured); do not assume portable behavior.
- Native compilation has not been verified for these extensions — do not claim native support.
