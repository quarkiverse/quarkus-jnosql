### Shared essentials

- Entities use `jakarta.nosql` annotations: `@Entity` on the class, `@Id` on the identifier field, `@Column` on mapped fields — Java records work. Use `@Embeddable` for nested objects (`FLAT` merges fields into the parent, `GROUPING` nests them under the property name).
- The `org.eclipse.jnosql.lite:mapping-lite-processor` MUST run during compilation on Java 21+ — on Maven, `maven-compiler-plugin` needs `annotationProcessorPaths` (`org.eclipse.jnosql.lite:mapping-lite-processor`) and `<annotationProcessors>` (`org.eclipse.jnosql.lite.mapping.EntityProcessor`, `org.eclipse.jnosql.lite.mapping.repository.RepositoryProcessor`). At least one `@Entity` is REQUIRED: without entity metadata every JNoSQL injection point fails with `UnsatisfiedResolutionException` for `EntitiesMetadata`. If that error appears, run a full clean build so the processors re-run.
- Never instantiate managers, factories, templates or clients manually — inject the CDI beans produced by the extension. On Gradle, exclude `org.eclipse.jnosql.mapping:jnosql-mapping-reflection` or injection fails with `AmbiguousResolutionException`.
- The `quarkus-jnosql-core` skill holds the full shared conventions (repository rules, `jnosql.<model>.database` names, staleness symptoms) — load it too when transitive skill scanning is enabled (`AGENT_MCP_SKILLS_INCLUDE_TRANSITIVE=true` on the Quarkus Agent MCP).

### Configuration

- `jnosql.timeseries.database` is required — the InfluxDB bucket/database name.
- Connection: `jnosql.influxdb.url` (e.g. `http://localhost:8181`) and `jnosql.influxdb.token`.

### Entities

- `@Entity` classes (or records) whose `@Id` field is a temporal type such as `Instant` — the timestamp is the identity. Truncate to the precision the provider stores (tests use `ChronoUnit.MILLIS`).

### Data access

- Inject `TimeSeriesTemplate` with `@Inject @Database(DatabaseType.TIME_SERIES)`.
- Repositories: `@Repository` interface extending `org.eclipse.jnosql.mapping.NoSQLRepository<Entity, Instant>`; inject with `@Database(DatabaseType.TIME_SERIES)`.
- Inject `InfluxDBTimeSeriesManager` for the low-level time-series API.

### Default usage

```java
@Entity
public record Metric(@Id Instant timestamp, @Column String source, @Column double value) {}

// repository — @Id is the temporal identity; inject with the qualifier
@Repository
public interface Metrics extends NoSQLRepository<Metric, Instant> {
    List<Metric> findBySource(String source);
}

@Inject @Database(DatabaseType.TIME_SERIES) Metrics metrics;
metrics.insert(metric);
metrics.findById(timestamp);   // Optional<Metric>
```

### Testing

- Use `@QuarkusTest` with `InfluxDBTestResource` (a `QuarkusTestResourceLifecycleManager` starting InfluxDB via Testcontainers and returning `jnosql.influxdb.url`/`token`).

### Common Pitfalls

- `update` is rejected by InfluxDB — do not use repository `update`; `deleteById` is also rejected. `insert`, `findById` and derived queries work.
- Points are immutable once written — model corrections as new points, not updates.
