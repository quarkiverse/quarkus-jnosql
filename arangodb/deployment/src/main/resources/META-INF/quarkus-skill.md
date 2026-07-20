### Required Setup Procedure

1. First load this `quarkus-jnosql-arangodb` skill.
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
- ArangoDB supports Document and Key-Value models; Jakarta Data repositories are supported only for Document entities.
- ArangoDB Key-Value access does not support Jakarta Data repositories; use `Template` with `@Database(DatabaseType.KEY_VALUE)` for key-value entities.
- Configure document access with `jnosql.document.database` and key-value access with `jnosql.keyvalue.database`.
- Use `@Database(DatabaseType.DOCUMENT)` for document `Template` and repository injections.
- Use `@Database(DatabaseType.KEY_VALUE)` for key-value `Template` injections.

### Testing

- Use `@QuarkusTest` with the custom `ArangodbTestResource` pattern.
- The test resource starts a Testcontainers `arangodb:latest` container, sets `ARANGO_NO_AUTH=1`, exposes port `8529`, waits for the readiness log, and returns `ArangoDBConfigurations.HOST`.
- Test document paths separately from key-value paths so the correct `DatabaseType` qualifier is verified.

### Common Pitfalls

- Do not inject an unqualified `Template` when both document and key-value models are active; use the correct `@Database` qualifier.
- Do not create `NoSQLRepository` interfaces for ArangoDB Key-Value access; repositories are for Document entities in this extension.
