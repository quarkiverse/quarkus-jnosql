### Usage Patterns

- This extension brings `quarkus-jnosql-core` transitively. Use the JNoSQL Core skill for shared installation, entity mapping, generic `Template` and repository patterns, Java 21+ annotation processor setup, and generated-code troubleshooting.
- MongoDB uses the Document API and supports Jakarta Data repositories.
- Configure the document database name with `jnosql.document.database`.
- Inject document repositories and templates with `@Database(DatabaseType.DOCUMENT)` when the application has more than one JNoSQL database model.
- Use `jakarta.nosql.Template` for document operations and `NoSQLRepository<Entity, String>` for repository-style access.

### Testing

- Use `@QuarkusTest` for MongoDB extension tests.
- The current integration tests do not attach a custom `QuarkusTestResource`; rely on the configured MongoDB service or Quarkus Dev Services behavior.
- Test both Jakarta NoSQL template operations and Jakarta Data repository operations for POJO and record entities.

### Common Pitfalls

- Do not omit `jnosql.document.database`; document operations need a target database.
- Do not use key-value, column, or graph `DatabaseType` qualifiers with MongoDB document injections.
