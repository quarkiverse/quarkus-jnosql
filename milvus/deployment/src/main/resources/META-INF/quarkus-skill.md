### Shared essentials

- Entities use `jakarta.nosql` annotations: `@Entity` on the class, `@Id` on the identifier field, `@Column` on mapped fields — Java records work. Use `@Embeddable` for nested objects (`FLAT` merges fields into the parent, `GROUPING` nests them under the property name).
- The `org.eclipse.jnosql.lite:mapping-lite-processor` MUST run during compilation on Java 21+ — on Maven, `maven-compiler-plugin` needs `annotationProcessorPaths` (`org.eclipse.jnosql.lite:mapping-lite-processor`) and `<annotationProcessors>` (`org.eclipse.jnosql.lite.mapping.EntityProcessor`, `org.eclipse.jnosql.lite.mapping.repository.RepositoryProcessor`). At least one `@Entity` is REQUIRED: without entity metadata every JNoSQL injection point fails with `UnsatisfiedResolutionException` for `EntitiesMetadata`. If that error appears, run a full clean build so the processors re-run.
- Never instantiate managers, factories, templates or clients manually — inject the CDI beans produced by the extension. On Gradle, exclude `org.eclipse.jnosql.mapping:jnosql-mapping-reflection` or injection fails with `AmbiguousResolutionException`.
- The `quarkus-jnosql-core` skill holds the full shared conventions (repository rules, `jnosql.<model>.database` names, staleness symptoms) — load it too when transitive skill scanning is enabled (`AGENT_MCP_SKILLS_INCLUDE_TRANSITIVE=true` on the Quarkus Agent MCP).

### Configuration

- Requires `jnosql.vector.database` (shared vector config) plus `jnosql.milvus.*` connection properties: `jnosql.milvus.host`, `jnosql.milvus.port` (default `19530`), `jnosql.milvus.collection.metric` and `jnosql.milvus.collection.vector.size`; optional `jnosql.milvus.uri`, `jnosql.milvus.token`, `jnosql.milvus.tls`, `jnosql.milvus.timeout`, `jnosql.milvus.consistency`, `jnosql.milvus.collection.string.max.length`.
- The collection is named after the entity.

### Data access

- Follows the shared vector pattern shown in "Default usage" below: `VectorTemplate` with `@Database(DatabaseType.VECTOR)`, `DenseVector` entity fields, and `BasicRepository` Jakarta Data repositories. The `quarkus-jnosql-vector` skill holds the full shared vector conventions when transitive skill scanning is enabled.

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

- Use `@QuarkusTest` with `MilvusTestResource` (Testcontainers). The `milvusdb/milvus:v3.0.4` image needs `DEPLOY_MODE=STANDALONE` to run with the embedded etcd — copy that environment setting when writing your own test resource.

### Common Pitfalls

- Supported metrics: `COSINE`, `EUCLIDEAN`, `INNER_PRODUCT`.
- `collection.vector.size` must match the `DenseVector` dimension used at runtime.
- Native compilation is not verified for this extension.
