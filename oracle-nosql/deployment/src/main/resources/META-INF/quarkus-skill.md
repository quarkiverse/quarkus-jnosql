### Shared essentials

- Entities use `jakarta.nosql` annotations: `@Entity` on the class, `@Id` on the identifier field, `@Column` on mapped fields — Java records work. Use `@Embeddable` for nested objects (`FLAT` merges fields into the parent, `GROUPING` nests them under the property name).
- The `org.eclipse.jnosql.lite:mapping-lite-processor` MUST run during compilation on Java 21+ — on Maven, `maven-compiler-plugin` needs `annotationProcessorPaths` (`org.eclipse.jnosql.lite:mapping-lite-processor`) and `<annotationProcessors>` (`org.eclipse.jnosql.lite.mapping.EntityProcessor`, `org.eclipse.jnosql.lite.mapping.repository.RepositoryProcessor`). At least one `@Entity` is REQUIRED: without entity metadata every JNoSQL injection point fails with `UnsatisfiedResolutionException` for `EntitiesMetadata`. If that error appears, run a full clean build so the processors re-run.
- Never instantiate managers, factories, templates or clients manually — inject the CDI beans produced by the extension. On Gradle, exclude `org.eclipse.jnosql.mapping:jnosql-mapping-reflection` or injection fails with `AmbiguousResolutionException`.
- The `quarkus-jnosql-core` skill holds the full shared conventions (repository rules, `jnosql.<model>.database` names, staleness symptoms) — load it too when transitive skill scanning is enabled (`AGENT_MCP_SKILLS_INCLUDE_TRANSITIVE=true` on the Quarkus Agent MCP).

### Configuration

- Oracle NoSQL is multi-model: configure BOTH `jnosql.document.database` and `jnosql.keyvalue.database` for the respective stores.
- Connection: `jnosql.oracle.nosql.host`, `jnosql.oracle.nosql.user`, `jnosql.oracle.nosql.password`, `jnosql.oracle.nosql.tenant.id`, `jnosql.oracle.nosql.compartment`, `jnosql.oracle.nosql.namespace`, `jnosql.oracle.nosql.deployment`, plus `jnosql.oracle.nosql.config.file`, `jnosql.oracle.nosql.fingerprint`, `jnosql.oracle.nosql.private.key` for wallet/TLS setups.
- Every injection point MUST carry `@Database(DatabaseType.DOCUMENT)` or `@Database(DatabaseType.KEY_VALUE)` — both types are produced and unqualified injection is ambiguous.

### Data access

- Document: inject `DocumentTemplate`/`Template` with `@Database(DOCUMENT)`; `OracleNoSQLDocumentManager` for the low-level API; `@Repository` interfaces extending `NoSQLRepository<Entity, IdType>`.
- Key-value: inject `Template` with `@Database(KEY_VALUE)`; `OracleNoSQLBucketManager` for key-based access; `BasicRepository` for key-based CRUD only.

### Default usage

```java
@Entity
public record Car(@Id String id, @Column String model) {}

// both stores are produced — every injection point needs its qualifier
@Inject @Database(DatabaseType.DOCUMENT) DocumentTemplate documents;
@Inject @Database(DatabaseType.KEY_VALUE) Template keyValues;

documents.insert(car);
keyValues.insert(car);                  // @Id is the key
keyValues.find(Car.class, car.id());    // Optional<Car>
```

### Testing

- Use `@QuarkusTest` with `OracleNoSQLTestResource` (Testcontainers) returning the `jnosql.oracle.nosql.*` properties.

### Common Pitfalls

- Do NOT forget the `@Database` qualifier — with both types on the classpath, unqualified `Template`/repository injection fails.
- Wallet-based connections need the `config.file`/`fingerprint`/`private.key` properties — plain user/password applies to non-wallet setups.
