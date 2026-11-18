### Shared essentials

- Entities use `jakarta.nosql` annotations: `@Entity` on the class, `@Id` on the identifier field, `@Column` on mapped fields — Java records work. Use `@Embeddable` for nested objects (`FLAT` merges fields into the parent, `GROUPING` nests them under the property name).
- The `org.eclipse.jnosql.lite:mapping-lite-processor` MUST run during compilation on Java 21+ — on Maven, `maven-compiler-plugin` needs `annotationProcessorPaths` (`org.eclipse.jnosql.lite:mapping-lite-processor`) and `<annotationProcessors>` (`org.eclipse.jnosql.lite.mapping.EntityProcessor`, `org.eclipse.jnosql.lite.mapping.repository.RepositoryProcessor`). At least one `@Entity` is REQUIRED: without entity metadata every JNoSQL injection point fails with `UnsatisfiedResolutionException` for `EntitiesMetadata`. If that error appears, run a full clean build so the processors re-run.
- Never instantiate managers, factories, templates or clients manually — inject the CDI beans produced by the extension. On Gradle, exclude `org.eclipse.jnosql.mapping:jnosql-mapping-reflection` or injection fails with `AmbiguousResolutionException`.
- The `quarkus-jnosql-core` skill holds the full shared conventions (repository rules, `jnosql.<model>.database` names, staleness symptoms) — load it too when transitive skill scanning is enabled (`AGENT_MCP_SKILLS_INCLUDE_TRANSITIVE=true` on the Quarkus Agent MCP).

### Configuration

- `jnosql.column.database` is required — the ScyllaDB keyspace.
- Connection: `jnosql.scylladb.host.N` contact points (one shared `jnosql.scylladb.port`, default `9042`), `jnosql.scylladb.data.center` (default `datacenter1` — must match the cluster), optional `jnosql.scylladb.name`, `jnosql.scylladb.user`, `jnosql.scylladb.password`.
- Startup CQL can run through sorted `jnosql.scylladb.query.NNN` properties (lexicographic order), e.g. `jnosql.scylladb.query.000=CREATE KEYSPACE IF NOT EXISTS ...`.
- This extension uses the native ScyllaDB driver (`com.scylladb:java-driver-core`) — do NOT combine it with the Cassandra extension/client on the same classpath; the `com.datastax.oss.driver` classes overlap.

### Data access

- Inject `jakarta.nosql.Template` or `ColumnTemplate` with `@Inject @Database(DatabaseType.COLUMN)`.
- Inject `ScyllaDBColumnManager` for the low-level column API.
- Repositories: `@Repository` interface extending `org.eclipse.jnosql.mapping.NoSQLRepository<Entity, IdType>`.
- Keyspaces and tables must be provisioned before use; the driver's separate portable-CDI repository API is not part of this extension.

### Default usage

```java
@Entity
public record Person(@Id String id, @Column String name) {}

// template — the keyspace and tables must already exist
@Inject @Database(DatabaseType.COLUMN) ColumnTemplate template;
template.insert(person);
template.find(Person.class, person.id());

// repository
@Repository
public interface PersonRepository extends NoSQLRepository<Person, String> {}
```

### Testing

- Use `@QuarkusTest` with a `QuarkusTestResourceLifecycleManager` starting `scylladb/scylla` via Testcontainers and returning the `jnosql.scylladb.*` properties (see `ScyllaDBTestResource`).

### Common Pitfalls

- Do NOT add `quarkus-jnosql-cassandra` or `cassandra-quarkus-client` to the same application — driver classes overlap and conflict.
- Do NOT expect schema auto-creation — create keyspaces/tables via `jnosql.scylladb.query.NNN` or externally.
- Custom managers from `ScyllaDBColumnManagerFactory.apply(keyspace)` are NOT closed by CDI — close them yourself.
