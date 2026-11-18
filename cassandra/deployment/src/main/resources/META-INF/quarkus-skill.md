### Shared essentials

- Entities use `jakarta.nosql` annotations: `@Entity` on the class, `@Id` on the identifier field, `@Column` on mapped fields — Java records work. Use `@Embeddable` for nested objects (`FLAT` merges fields into the parent, `GROUPING` nests them under the property name).
- The `org.eclipse.jnosql.lite:mapping-lite-processor` MUST run during compilation on Java 21+ — on Maven, `maven-compiler-plugin` needs `annotationProcessorPaths` (`org.eclipse.jnosql.lite:mapping-lite-processor`) and `<annotationProcessors>` (`org.eclipse.jnosql.lite.mapping.EntityProcessor`, `org.eclipse.jnosql.lite.mapping.repository.RepositoryProcessor`). At least one `@Entity` is REQUIRED: without entity metadata every JNoSQL injection point fails with `UnsatisfiedResolutionException` for `EntitiesMetadata`. If that error appears, run a full clean build so the processors re-run.
- Never instantiate managers, factories, templates or clients manually — inject the CDI beans produced by the extension. On Gradle, exclude `org.eclipse.jnosql.mapping:jnosql-mapping-reflection` or injection fails with `AmbiguousResolutionException`.
- The `quarkus-jnosql-core` skill holds the full shared conventions (repository rules, `jnosql.<model>.database` names, staleness symptoms) — load it too when transitive skill scanning is enabled (`AGENT_MCP_SKILLS_INCLUDE_TRANSITIVE=true` on the Quarkus Agent MCP).

### Configuration

- `jnosql.column.database` is required — the Cassandra keyspace.
- The connection is owned by `cassandra-quarkus-client`: configure contact points, credentials, datacenter and pooling with `quarkus.cassandra.*` properties.
- Keyspaces, tables (column families) and indexes must be provisioned before use — the extension does not create schema. Use CQL `init` scripts or migrations.

### Data access

- Inject `jakarta.nosql.Template` or `ColumnTemplate` with `@Inject @Database(DatabaseType.COLUMN)`.
- Inject `CassandraColumnManager` for the low-level column API.
- Repositories: `@Repository` interface extending `org.eclipse.jnosql.mapping.NoSQLRepository<Entity, IdType>` — queries translate to CQL where the driver supports them.

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

- Use `@QuarkusTest` with `CassandraTestResource` (`com.datastax.oss.quarkus.test`, from `cassandra-quarkus-test-framework`) as `@QuarkusTestResource` — it starts Cassandra via Testcontainers and executes `src/test/resources/init_script.cql` to create the keyspace and column families.

### Common Pitfalls

- Do NOT expect the extension to create keyspaces/tables — missing schema fails at query time.
- The `@Id` field maps to the primary key column; Cassandra requires a defined primary key in the table DDL.
- Queries that the Cassandra driver cannot express fail at runtime — verify CQL support before using derived queries.
