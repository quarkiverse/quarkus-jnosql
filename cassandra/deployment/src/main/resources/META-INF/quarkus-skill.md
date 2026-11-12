### Configuration

- `jnosql.column.database` is required — the Cassandra keyspace.
- The connection is owned by `cassandra-quarkus-client`: configure contact points, credentials, datacenter and pooling with `quarkus.cassandra.*` properties.
- Keyspaces, tables (column families) and indexes must be provisioned before use — the extension does not create schema. Use CQL `init` scripts or migrations.

### Data access

- Inject `jakarta.nosql.Template` or `ColumnTemplate` with `@Inject @Database(DatabaseType.COLUMN)`.
- Inject `CassandraColumnManager` for the low-level column API.
- Repositories: `@Repository` interface extending `org.eclipse.jnosql.mapping.NoSQLRepository<Entity, IdType>` — queries translate to CQL where the driver supports them.

### Testing

- Use `@QuarkusTest` with `CassandraTestResource` (`com.datastax.oss.quarkus.test`, from `cassandra-quarkus-test-framework`) as `@QuarkusTestResource` — it starts Cassandra via Testcontainers and executes `src/test/resources/init_script.cql` to create the keyspace and column families.

### Common Pitfalls

- Do NOT expect the extension to create keyspaces/tables — missing schema fails at query time.
- The `@Id` field maps to the primary key column; Cassandra requires a defined primary key in the table DDL.
- Queries that the Cassandra driver cannot express fail at runtime — verify CQL support before using derived queries.
