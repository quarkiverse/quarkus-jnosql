### Required Setup Procedure

1. First load this `quarkus-jnosql-couchdb` skill.
2. Then immediately load and follow the `quarkus-jnosql-core` skill before making code or build changes.
3. Treat the `quarkus-jnosql-core` skill as authoritative for:
   - installation steps
   - annotation processor configuration
   - entity metadata generation
   - repository setup
   - generated-code troubleshooting
4. Use codestarts only as fallback references if the Core skill is unavailable or incomplete.

### Usage Patterns

- This extension brings `quarkus-jnosql-core` transitively. Use the `quarkus-jnosql-core` skill for shared installation, entity mapping, generic `Template` and repository patterns, Java 21+ annotation processor setup, and generated-code troubleshooting.
- CouchDB uses the Document API and supports Jakarta Data repositories.
- Configure the document database name with `jnosql.document.database`.
- Inject document repositories and templates with `@Database(DatabaseType.DOCUMENT)` when CDI needs model disambiguation.
- Use repository methods for Jakarta Data queries and `Template` for direct document operations.

### Testing

- Use `@QuarkusTest` with the custom `CouchdbTestResource` pattern.
- The test resource starts a Testcontainers `couchdb:latest` container and passes `COUCHDB_USER` and `COUCHDB_PASSWORD` from configuration.
- Create the test database before running document operations.
- Create JSON indexes required by query tests, such as indexes for simple fields and nested document fields.

### Common Pitfalls

- Do not run CouchDB query tests without creating the database and required indexes first.
- Do not omit CouchDB user and password configuration when the test resource expects them.
