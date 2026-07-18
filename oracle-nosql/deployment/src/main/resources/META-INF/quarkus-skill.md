### Usage Patterns

- This extension brings `quarkus-jnosql-core` transitively. Use the JNoSQL Core skill for shared installation, entity mapping, generic `Template` and repository patterns, Java 21+ annotation processor setup, and generated-code troubleshooting.
- Oracle NoSQL supports Document and Key-Value models and supports Jakarta Data for document-style access.
- Configure document access with `jnosql.document.database` and key-value access with `jnosql.keyvalue.database`.
- Use `@Database(DatabaseType.DOCUMENT)` for document `Template` and repository injections.
- Use `@Database(DatabaseType.KEY_VALUE)` for key-value `Template` or manager injections.

### Testing

- Use `@QuarkusTest` with the custom `OracleNoSQLTestResource` pattern.
- The test resource starts a Testcontainers `ghcr.io/oracle/nosql:latest-ce` container, exposes port `8080`, and returns `OracleNoSQLConfigurations.HOST` as `http://<host>:<mapped-port>`.
- Test document repository/template paths separately from key-value paths so CDI qualifiers and database configuration are both covered.

### Common Pitfalls

- Do not inject an unqualified `Template` when both document and key-value models are active; use the correct `@Database` qualifier.
- Do not configure only one database model when tests exercise both document and key-value APIs.
