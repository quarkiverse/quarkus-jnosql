### Configuration

- `jnosql.document.database` is required — the CouchDB database name.
- Connection: `jnosql.couchdb.host`, `jnosql.couchdb.port`, `jnosql.couchdb.username`, `jnosql.couchdb.password`, `jnosql.couchdb.token`, `jnosql.couchdb.enable.ssl`, `jnosql.couchdb.socket.timeout`, `jnosql.couchdb.max.connections`.

### Data access

- Inject `jakarta.nosql.Template` or `DocumentTemplate` with `@Inject @Database(DatabaseType.DOCUMENT)`.
- Inject `CouchDBDocumentManager` for the low-level document API.
- Repositories: `@Repository` interface extending `org.eclipse.jnosql.mapping.NoSQLRepository<Entity, IdType>`.

### Testing

- Use `@QuarkusTest` with a `QuarkusTestResourceLifecycleManager` starting CouchDB via Testcontainers and returning the `jnosql.couchdb.*` connection properties (see `CouchdbTestResource`).

### Common Pitfalls

- The database must exist in CouchDB before use — the extension does not create it.
- Do NOT confuse `jnosql.couchdb.token` (CouchDB token auth) with basic-auth `username`/`password` — use one or the other.
