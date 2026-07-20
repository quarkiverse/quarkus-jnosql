### Installation And Build Setup

- Do not infer the way to set up this extension;
- Follow strictly the Cassandra codestart output as the preferred reference for required installation and configuration setup; `quarkus create app --extensions=jnosql-cassandra` generates the extension dependency, starter entities, tests, and configuration patterns. When working in an existing project, adapt those patterns to the project's current dependencies, package names, configuration files, and test setup.

### Mandatory Codestart Reference Workflow

- Before editing an existing project, generate or inspect a fresh Cassandra codestart created with `quarkus create app --extensions=jnosql-cassandra`.
- Compare the codestart against the existing project before making changes.
- Copy/Adapt all relevant setup from the codestart, including:
    - extension dependency
    - entity style
    - repository/template examples
    - `application.properties`
    - tests
    - annotation processor/build configuration, if present
- Do not invent starter entities or repositories without first checking the codestart shape

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

### Usage Patterns

- Cassandra uses the Column API and supports Jakarta Data repositories.
- Configure the column database name with `jnosql.column.database`.
- Inject Cassandra templates and repositories with `@Database(DatabaseType.COLUMN)` when the application has multiple JNoSQL database models.
- Use `NoSQLRepository<Entity, String>` for repository-style access and `jakarta.nosql.Template` for template operations.

### Testing

- Use `@QuarkusTest` with `@QuarkusTestResource(com.datastax.oss.quarkus.test.CassandraTestResource.class)`.
- Use the codestart `init_script.cql` pattern when schema initialization is required.
- Clean repository data before or after tests with `findAll().forEach(repository::delete)`.
- Exercise pagination and sorting repository methods with Jakarta Data types such as `PageRequest` and `Sort` when adding repository queries.

### Common Pitfalls

- If tests log `Eclipse JNoSQL Lite does not support reflection, including the use of constructors`, stop Quarkus instances, clean and recompile, then rerun tests before assuming mapping is broken.
- If an error log shows `Unsatisfied dependency for type org.eclipse.jnosql.mapping.metadata.EntitiesMetadata and qualifiers`, check whether the source contains at least one Jakarta NoSQL entity. The `org.eclipse.jnosql.lite:mapping-lite-processor` annotation processor uses Jakarta NoSQL entities to generate the metadata classes required by CDI, so a project with no entities cannot produce that bean. If no entity exists, explain why one is required and offer to create a sample Jakarta NoSQL entity. After adding or fixing an entity, rebuild the project and restart dev mode if it is running.
- Do not configure only `jnosql.document.database` or `jnosql.keyvalue.database`; Cassandra expects the column database configuration.
- Do not forget the Cassandra test resource when tests need an ephemeral Cassandra service.
