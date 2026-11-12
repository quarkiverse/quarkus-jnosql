### Configuration

- `jnosql.document.database` is required — the MongoDB database name.
- The connection is owned by `quarkus-mongodb-client`: configure it with `quarkus.mongodb.*` properties (hosts/connection string, credentials, TLS). Dev Services starts a MongoDB container automatically in dev/test when no connection is configured.
- Do NOT set parallel `jnosql.mongodb.*` connection properties — the injected `MongoClient` already carries the connection.

### Data access

- Inject `jakarta.nosql.Template` or `DocumentTemplate` with `@Inject @Database(DatabaseType.DOCUMENT)` — the extension's beans carry that qualifier, so qualify every injection point (template, manager, repository).
- Inject `MongoDBDocumentManager` for the low-level document API.
- Repositories: `@Repository` interface extending `org.eclipse.jnosql.mapping.NoSQLRepository<Entity, IdType>` — derived queries are supported.

### Testing

- Use `@QuarkusTest` — Dev Services provides the database; no test resource needed.
- The integration tests exercise POJO and record entities through the template, `DocumentManager` and Jakarta Data repositories, asserted via a JAX-RS `JNoSQLResource` with REST Assured.

### Common Pitfalls

- Do NOT create `MongoClient` or `MongoDBDocumentManagerFactory` manually — the extension shares the Quarkus-managed client.
