### Installation And Build Setup

- Add one of the concrete JNoSQL database extensions for application data access; `quarkus-jnosql-core` contains shared infrastructure and is normally pulled in transitively.
- For Java 21 or later, configure the JNoSQL Lite annotation processor explicitly so entity and repository implementations are generated during compilation.
- Register both processors when configuring annotation processing: `org.eclipse.jnosql.lite.mapping.EntityProcessor` and `org.eclipse.jnosql.lite.mapping.repository.RepositoryProcessor`.
- Use the `org.eclipse.jnosql.lite:mapping-lite-processor` artifact as the annotation processor dependency.

### Entity Mapping

- Model persistent types with `@Entity`, mark the identifier with `@Id`, and annotate persisted fields or record components with `@Column`.
- Java records are supported when their components carry the same Jakarta NoSQL mapping annotations used by POJO fields.
- Use `@Embeddable` for embedded value objects. Use `@Embeddable(GROUPING)` when the embedded state should be stored as a grouped structure instead of flattened into the owning entity.

### Data Access Patterns

- Inject `jakarta.nosql.Template` for Jakarta NoSQL template operations such as `insert` and `find`.
- Use Jakarta Data repositories by declaring an interface annotated with `@Repository` that extends `org.eclipse.jnosql.mapping.NoSQLRepository<Entity, Id>` when the selected backend supports Jakarta Data.
- Add `@Database(DatabaseType.DOCUMENT)`, `@Database(DatabaseType.COLUMN)`, `@Database(DatabaseType.GRAPH)`, or `@Database(DatabaseType.KEY_VALUE)` when an extension exposes multiple database models or CDI injection needs disambiguation.
- Use `org.eclipse.jnosql.mapping.graph.GraphTemplate` for graph databases instead of the generic `Template`.

### Testing

- Use `@QuarkusTest` for JVM tests and `@QuarkusIntegrationTest` for packaged application tests.
- Prefer exercising both the repository path and the template path when the backend supports both Jakarta Data and Jakarta NoSQL.
- Clean repository-backed test data before or after tests, for example with `repository.findAll().forEach(repository::delete)`, to avoid cross-test contamination.
- Use the backend extension's test resource or Dev Services setup rather than mocking JNoSQL infrastructure.

### Common Pitfalls

- If tests log `Eclipse JNoSQL Lite does not support reflection, including the use of constructors`, do not assume the entity mapping is broken first. JNoSQL Lite relies on generated classes under `target/generated-sources/annotations`, and Quarkus dev mode can run with stale or missing generated classes after interrupted reloads or target cleanup. Stop all running Quarkus instances, clean and recompile the project, then run the tests again.
- Do not create repositories, templates, managers, or manager factories manually with `new`; inject them as CDI beans.
- Do not use a repository for a backend model that does not support Jakarta Data. Use `Template`, `GraphTemplate`, or the backend manager API instead.
