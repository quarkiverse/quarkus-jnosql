### Shared essentials

- Entities use `jakarta.nosql` annotations: `@Entity` on the class, `@Id` on the identifier field, `@Column` on mapped fields — Java records work. Use `@Embeddable` for nested objects (`FLAT` merges fields into the parent, `GROUPING` nests them under the property name).
- The `org.eclipse.jnosql.lite:mapping-lite-processor` MUST run during compilation on Java 21+ — on Maven, `maven-compiler-plugin` needs `annotationProcessorPaths` (`org.eclipse.jnosql.lite:mapping-lite-processor`) and `<annotationProcessors>` (`org.eclipse.jnosql.lite.mapping.EntityProcessor`, `org.eclipse.jnosql.lite.mapping.repository.RepositoryProcessor`). At least one `@Entity` is REQUIRED: without entity metadata every JNoSQL injection point fails with `UnsatisfiedResolutionException` for `EntitiesMetadata`. If that error appears, run a full clean build so the processors re-run.
- Never instantiate managers, factories, templates or clients manually — inject the CDI beans produced by the extension. On Gradle, exclude `org.eclipse.jnosql.mapping:jnosql-mapping-reflection` or injection fails with `AmbiguousResolutionException`.
- The `quarkus-jnosql-core` skill holds the full shared conventions (repository rules, `jnosql.<model>.database` names, staleness symptoms) — load it too when transitive skill scanning is enabled (`AGENT_MCP_SKILLS_INCLUDE_TRANSITIVE=true` on the Quarkus Agent MCP).

### Configuration

- `jnosql.keyvalue.database` is required — the DynamoDB table name.
- The connection is owned by `quarkus-amazon-dynamodb` (Quarkiverse Amazon Services): configure credentials, region and endpoint through its `quarkus.amazon-dynamodb.*`/AWS SDK properties — do not invent parallel JNoSQL connection properties.
- DynamoDB exposes more than one database type, so injecting templates, managers or repositories requires the `@Database(DatabaseType.KEY_VALUE)` qualifier.

### Data access

- Inject `jakarta.nosql.Template` with `@Inject @Database(DatabaseType.KEY_VALUE)`.
- Inject `DynamoDBBucketManager` (or `BucketManager`) for key-based access.
- Repositories extend `jakarta.data.repository.BasicRepository<Entity, IdType>` with `@Repository` and MUST also carry `@Database(DatabaseType.KEY_VALUE)` to disambiguate.

### Default usage

```java
@Entity
public record Person(@Id String id, @Column String name) {}

// template — key-based CRUD
@Inject @Database(DatabaseType.KEY_VALUE) Template template;
template.insert(person);
template.find(Person.class, person.id());   // Optional<Person>

// repository — CRUD by key only; the qualifier is mandatory here
@Repository
@Database(DatabaseType.KEY_VALUE)
public interface PersonRepository extends BasicRepository<Person, String> {}
```

### Testing

- Use `@QuarkusTest` with `@Database(DatabaseType.KEY_VALUE)` on injected repositories; `PersonBasicRepositoryTest` covers save/findById/update/deleteById and asserts `findAll()`/`findAll(PageRequest, Order)` throw `UnsupportedOperationException`.

### Common Pitfalls

- `findAll()` and derived queries are NOT supported — they throw `UnsupportedOperationException`. `BasicRepository` has no `existsById`; use `findById(id).isPresent()`.
- Do NOT forget `@Database(DatabaseType.KEY_VALUE)` on injection points — without it resolution is ambiguous or fails.
- The table must exist before use; the extension does not create it.
