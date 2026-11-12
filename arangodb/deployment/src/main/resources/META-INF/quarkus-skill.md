### Configuration

- ArangoDB is multi-model: configure BOTH `jnosql.document.database` (document store) and `jnosql.keyvalue.database` (key-value store) — each names a separate ArangoDB database.
- Connection: `jnosql.arangodb.host` (`host:port` format, e.g. `localhost:8529`), `jnosql.arangodb.user`, `jnosql.arangodb.password`, `jnosql.arangodb.protocol`, `jnosql.arangodb.user.ssl`, `jnosql.arangodb.timeout`, `jnosql.arangodb.connections.max`, `jnosql.arangodb.chunk.size`.
- Every injection point MUST carry `@Database(DatabaseType.DOCUMENT)` or `@Database(DatabaseType.KEY_VALUE)` — the extension produces both types and unqualified injection is ambiguous.

### Data access

- Document: inject `DocumentTemplate`/`Template` with `@Database(DOCUMENT)`; `ArangoDBDocumentManager` for the low-level API; `@Repository` interfaces extending `NoSQLRepository<Entity, IdType>` for Jakarta Data.
- Key-value: inject `Template` with `@Database(KEY_VALUE)`; `ArangoDBBucketManager` for key-based access; `BasicRepository` for key-based CRUD only.

### Testing

- Use `@QuarkusTest` with `ArangodbTestResource`/`ArangoDBTestResource` (Testcontainers `arangodb` image, `ARANGO_NO_AUTH=1`) returning `jnosql.arangodb.host` mapped to the container.

### Common Pitfalls

- Do NOT forget the `@Database` qualifier — with both types on the classpath, unqualified `Template`/repository injection fails.
- Do NOT assume one `jnosql.*.database` property covers both models — document and key-value use separate ArangoDB databases.
