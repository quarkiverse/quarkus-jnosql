### Configuration

- `jnosql.graph.database` is required — the graph database name.
- The connection is owned by `quarkus-neo4j`: configure the URI and credentials with `quarkus.neo4j.uri` and `quarkus.neo4j.authentication.username`/`password`.

### Data access

- Inject `GraphTemplate` (or `jakarta.nosql.Template`) with `@Inject @Database(DatabaseType.GRAPH)`.
- Inject `Neo4JDatabaseManager` for the low-level graph API.
- Repositories: `@Repository` interface extending `jakarta.data.repository.BasicRepository<Entity, IdType>`; use `@Find` + `@By("property")` on parameters for custom queries (see the `MagazineRepository` codestart).
- Entities map to graph vertices; model relationships through entity fields the driver maps to edges — verify the mapping in the JNoSQL Neo4j driver docs.

### Testing

- Use `@QuarkusTest`; the integration tests exercise `GraphTemplate` with POJO and record entities through a JAX-RS `JNoSQLResource`.

### Common Pitfalls

- Do NOT create the Neo4j `Driver` manually — it is produced by `quarkus-neo4j` and shared with the extension.
- Do NOT forget `jnosql.graph.database` — startup fails with `MappingException` when it is missing.
