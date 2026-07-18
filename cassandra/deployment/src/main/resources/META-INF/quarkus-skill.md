### Usage Patterns

- This extension brings `quarkus-jnosql-core` transitively. Use the JNoSQL Core skill for shared installation, entity mapping, generic `Template` and repository patterns, Java 21+ annotation processor setup, and generated-code troubleshooting.
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

- Do not configure only `jnosql.document.database` or `jnosql.keyvalue.database`; Cassandra expects the column database configuration.
- Do not forget the Cassandra test resource when tests need an ephemeral Cassandra service.
