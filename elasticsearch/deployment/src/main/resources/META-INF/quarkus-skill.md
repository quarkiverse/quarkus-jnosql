### Configuration

- `jnosql.document.database` is required — used as the index name.
- The connection is owned by `quarkus-elasticsearch-java-client`: configure hosts, credentials and TLS with `quarkus.elasticsearch.*` properties. The driver's `jnosql.elasticsearch.host/user/password` apply only when configuring the driver directly.
- Does NOT support native compilation — do not claim `-Dnative` support for this extension.

### Data access

- Inject `jakarta.nosql.Template` or `DocumentTemplate` with `@Inject @Database(DatabaseType.DOCUMENT)`.
- Inject `ElasticsearchDocumentManager` for the low-level document API.
- Repositories: `@Repository` interface extending `org.eclipse.jnosql.mapping.NoSQLRepository<Entity, IdType>`.

### Testing

- Use `@QuarkusTest` with a `QuarkusTestResourceLifecycleManager` starting Elasticsearch via Testcontainers and returning the connection properties (see `ElasticsearchTestResource`).

### Common Pitfalls

- Do NOT create `ElasticsearchClient` or `ElasticsearchDocumentManagerFactory` manually — inject the CDI beans.
- Search-engine semantics differ from document stores: fields are indexed/analyzed, so assert query results accordingly rather than expecting exact document equality.
- Native image is unsupported — JVM mode only.
