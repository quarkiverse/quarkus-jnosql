### Configuration

- `jnosql.document.database` is required — the Solr core/collection name.
- Connection: `jnosql.solr.host`, `jnosql.solr.user`, `jnosql.solr.password`, `jnosql.solr.automatic.commit` (whether writes are committed automatically).

### Data access

- Inject `jakarta.nosql.Template` or `DocumentTemplate` with `@Inject @Database(DatabaseType.DOCUMENT)`.
- Inject `SolrDocumentManager` for the low-level document API.
- Repositories: `@Repository` interface extending `org.eclipse.jnosql.mapping.NoSQLRepository<Entity, IdType>`.

### Testing

- Use `@QuarkusTest` with a `QuarkusTestResourceLifecycleManager` starting Solr via Testcontainers and returning the `jnosql.solr.*` properties (see `SolrTestResource`).

### Common Pitfalls

- The Solr core must exist before use — the extension does not create it.
- If documents are not visible after writes, check `jnosql.solr.automatic.commit` — Solr commits are explicit unless enabled.
