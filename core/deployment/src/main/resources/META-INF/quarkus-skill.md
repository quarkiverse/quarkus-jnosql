### Installation And Build Setup

- Add one of the concrete JNoSQL database extensions for application data access; `quarkus-jnosql-core` contains shared infrastructure and is normally pulled in transitively.
- For Java 21 or later, configure the JNoSQL Lite annotation processor explicitly so entity and repository implementations are generated during compilation.
- Register both processors when configuring annotation processing: `org.eclipse.jnosql.lite.mapping.EntityProcessor` and `org.eclipse.jnosql.lite.mapping.repository.RepositoryProcessor`.
- Use the `org.eclipse.jnosql.lite:mapping-lite-processor` artifact as the annotation processor dependency.

### Entity Mapping

- Model persistent types with `@Entity`, mark the identifier with `@Id`, and annotate persisted fields or record components with `@Column`.
- Java records are supported when their components carry the same Jakarta NoSQL mapping annotations used by POJO fields.
- Use `@Embeddable` for embedded value objects. Use `@Embeddable(GROUPING)` when the embedded state should be stored as a grouped structure instead of flattened into the owning entity.
- For deeper annotation and embeddable semantics, consult the Jakarta NoSQL specification: https://jakarta.ee/specifications/nosql/1.0/jakarta-nosql-1.0

### Data Access Patterns

- Inject `jakarta.nosql.Template` for Jakarta NoSQL template operations such as `insert` and `find`.
- Use Jakarta Data repositories by declaring an interface annotated with `@Repository` that extends `org.eclipse.jnosql.mapping.NoSQLRepository<Entity, Id>` when the selected backend supports Jakarta Data.
- Add `@Database(DatabaseType.DOCUMENT)`, `@Database(DatabaseType.COLUMN)`, `@Database(DatabaseType.GRAPH)`, or `@Database(DatabaseType.KEY_VALUE)` when an extension exposes multiple database models or CDI injection needs disambiguation.
- Use `org.eclipse.jnosql.mapping.graph.GraphTemplate` for graph databases instead of the generic `Template`.
- For more details about `Template` behavior and operations, consult the Jakarta NoSQL specification: https://jakarta.ee/specifications/nosql/1.0/jakarta-nosql-1.0
- For more details about Jakarta Data repositories, query methods, pagination, and sorting, consult the Jakarta Data specification and addendum: https://jakarta.ee/specifications/data/1.0/jakarta-data-1.0 and https://jakarta.ee/specifications/data/1.0/jakarta-data-addendum-1.0

### Testing

- Use `@QuarkusTest` for JVM tests and `@QuarkusIntegrationTest` for packaged application tests.
- Prefer exercising both the repository path and the template path when the backend supports both Jakarta Data and Jakarta NoSQL.
- Clean repository-backed test data before or after tests, for example with `repository.findAll().forEach(repository::delete)`, to avoid cross-test contamination.
- Use the backend extension's test resource or Dev Services setup rather than mocking JNoSQL infrastructure.

### Common Pitfalls

- If tests log `Eclipse JNoSQL Lite does not support reflection, including the use of constructors`, do not assume the entity mapping is broken first. JNoSQL Lite relies on generated classes under `target/generated-sources/annotations`, and Quarkus dev mode can run with stale or missing generated classes after interrupted reloads or target cleanup. Stop all running Quarkus instances, clean and recompile the project, then run the tests again.
- If an error log shows `Unsatisfied dependency for type org.eclipse.jnosql.mapping.metadata.EntitiesMetadata and qualifiers`, create at least one Jakarta NoSQL entity class. The `org.eclipse.jnosql.lite:mapping-lite-processor` needs an annotated entity to generate the metadata classes required by CDI.
- Do not create repositories, templates, managers, or manager factories manually with `new`; inject them as CDI beans.
- Do not use a repository for a backend model that does not support Jakarta Data. Use `Template`, `GraphTemplate`, or the backend manager API instead.
