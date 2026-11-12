### Entities

- Map entities with `jakarta.nosql` annotations: `@Entity` on the class, `@Id` on the identifier field, `@Column` on mapped fields. Java records work as entities.
- When using the `mapping-lite-processor`, at least one `@Entity` class is REQUIRED in the application: the processor emits `EntitiesMetadata` and the CDI beans built on it only when it finds entities. With no entity, the extension produces no bean and injection fails with `UnsatisfiedResolutionException` ("Unsatisfied dependency for type org.eclipse.jnosql.mapping.metadata.EntitiesMetadata").
- Use `@Embeddable` for nested objects: default `FLAT` merges fields into the parent structure; `@Embeddable(GROUPING)` keeps them grouped under the property name.
- Every `@Entity`/`@Embeddable` class needs a setter (or record-style constructor) for each mapped field — the JNoSQL Lite processor generates the mapping metadata at compile time.

### Mapping Lite annotation processor

- On Java 21+ the `org.eclipse.jnosql.lite:mapping-lite-processor` MUST run during compilation, or entity/repository beans are not generated. Configure `maven-compiler-plugin` with `annotationProcessorPaths` and `<annotationProcessors>` listing `org.eclipse.jnosql.lite.mapping.EntityProcessor` and `org.eclipse.jnosql.lite.mapping.repository.RepositoryProcessor`.
- The processor is usually pulled in transitively by the extension, but declaring it explicitly is required when the build uses explicit processor configuration.
- On Gradle, also exclude `org.eclipse.jnosql.mapping:jnosql-mapping-reflection` — both metadata implementations become CDI beans and injection fails with `AmbiguousResolutionException`.

### Templates

- Inject `jakarta.nosql.Template` (or the model-specific `DocumentTemplate`, `ColumnTemplate`, `KeyValueTemplate`, `GraphTemplate`, `TimeSeriesTemplate`, `VectorTemplate`) with `@Inject`.
- Qualify with `@Database(DatabaseType.X)` when the application has several database types or several providers — `DatabaseType` is `org.eclipse.jnosql.mapping.DatabaseType`.
- Low-level managers (`DocumentManager`, `BucketManager`, `ColumnManager`, `GraphManager`, `TimeSeriesManager`, `VectorManager`) are produced as CDI beans by each database extension; inject the concrete type for direct access.

### Jakarta Data repositories

- Declare `@Repository` interfaces extending `org.eclipse.jnosql.mapping.NoSQLRepository<Entity, IdType>` for derived queries and `jakarta.data.repository.BasicRepository` where only key-based CRUD is supported (key-value databases).
- Inject the repository interface directly — Eclipse JNoSQL generates the implementation at build time through the Lite processor.
- Do NOT write repository implementation classes; the generated proxy is registered automatically.

### Configuration

- Database names are read from MicroProfile Config as `jnosql.<model>.database` (`jnosql.document.database`, `jnosql.keyvalue.database`, `jnosql.column.database`, `jnosql.graph.database`, `jnosql.timeseries.database`, `jnosql.vector.database`). A missing or blank name fails startup with `MappingException`.
- Driver connection properties use the `jnosql.<db>.*` namespace (or the `quarkus.*` properties of a reused Quarkus client extension). See the extension's guide for the full list.

### Testing

- Use `@QuarkusTest` against a real database — Dev Services or a `QuarkusTestResourceLifecycleManager` with Testcontainers. Mocks do not prove driver integration.
- Cover both the template and the repository when both exist; assert the real behavior of unsupported operations (usually `UnsupportedOperationException`) instead of assuming support.

### Common Pitfalls

- Do NOT instantiate managers, factories or templates manually — always inject the CDI beans produced by the extension.
- Do NOT ship an application without any `@Entity` when using the `mapping-lite-processor` — no entity metadata is generated, so every JNoSQL injection point fails with `UnsatisfiedResolutionException` for `EntitiesMetadata`.
- Do NOT add `jnosql-mapping-reflection` back to the classpath; entity metadata comes from the Lite processor only.
- Do NOT assume every database supports every operation — Jakarta Query/derived queries are database-dependent; verify against the driver.
