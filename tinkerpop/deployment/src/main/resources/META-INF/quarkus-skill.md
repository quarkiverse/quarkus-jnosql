### Shared essentials

- Entities use `jakarta.nosql` annotations: `@Entity` on the class, `@Id` on the identifier field, `@Column` on mapped fields — Java records work. Use `@Embeddable` for nested objects (`FLAT` merges fields into the parent, `GROUPING` nests them under the property name).
- The `org.eclipse.jnosql.lite:mapping-lite-processor` MUST run during compilation on Java 21+ — on Maven, `maven-compiler-plugin` needs `annotationProcessorPaths` (`org.eclipse.jnosql.lite:mapping-lite-processor`) and `<annotationProcessors>` (`org.eclipse.jnosql.lite.mapping.EntityProcessor`, `org.eclipse.jnosql.lite.mapping.repository.RepositoryProcessor`). At least one `@Entity` is REQUIRED: without entity metadata every JNoSQL injection point fails with `UnsatisfiedResolutionException` for `EntitiesMetadata`. If that error appears, run a full clean build so the processors re-run.
- Never instantiate managers, factories, templates or clients manually — inject the CDI beans produced by the extension. On Gradle, exclude `org.eclipse.jnosql.mapping:jnosql-mapping-reflection` or injection fails with `AmbiguousResolutionException`.
- The `quarkus-jnosql-core` skill holds the full shared conventions (repository rules, `jnosql.<model>.database` names, staleness symptoms) — load it too when transitive skill scanning is enabled (`AGENT_MCP_SKILLS_INCLUDE_TRANSITIVE=true` on the Quarkus Agent MCP).

### Application-owned Graph

- This extension has NO database dependency: the application must provide a CDI bean of type `org.apache.tinkerpop.gremlin.structure.Graph` via a `@Produces` producer — the extension consumes it and never creates, replaces or closes it.
- Add the TinkerPop provider dependency yourself (e.g. `com.arangodb:arangodb-tinkerpop-provider`); the extension only integrates JNoSQL with whatever `Graph` you supply.
- Without a `Graph` bean the build fails with a message asking for a CDI producer — there is no fallback graph. Dispose of the `Graph` in the producer (`@Disposes`).

### Data access

- Inject `org.eclipse.jnosql.databases.tinkerpop.mapping.TinkerpopTemplate` with `@Inject`.
- Repositories: declare interfaces extending `org.eclipse.jnosql.databases.tinkerpop.mapping.TinkerPopRepository<Entity, IdType>` WITHOUT `@Repository` — the extension registers them as CDI beans automatically.
- Jakarta Data also works: `@Repository` interfaces extending `BasicRepository<Entity, IdType>` resolve through the same application `Graph`.

### Default usage

```java
// the application must produce the Graph — see "Application-owned Graph"
@Produces
Graph graph() { /* a TinkerPop provider Graph, e.g. TinkerGraph or a database driver */ }

// driver repository — NO @Repository annotation, the extension registers it
public interface PersonRepository extends TinkerPopRepository<Person, String> {
    List<Person> findByName(String name);
}

// or Jakarta Data
@Repository
public interface PersonDataRepository extends BasicRepository<Person, String> {
    List<Person> findByName(String name);
}
```

### Testing

- Use `@QuarkusTest` with a `QuarkusTestResourceLifecycleManager` starting a TinkerPop provider database (the integration tests use `arangodb:3.12.13` via Testcontainers with `arangodb-tinkerpop-provider`).
- `MissingGraphTest` (a `QuarkusUnitTest` in the deployment module) verifies the build-time failure when no `Graph` bean exists.

### Common Pitfalls

- With the ArangoDB provider, `TinkerpopTemplate.insert` and `TinkerPopRepository.save` fail because the JNoSQL driver writes the `_id` property, which that provider reserves — create vertices through the application's `Graph` and read them back through JNoSQL.
- Do NOT annotate `TinkerPopRepository` interfaces with `@Repository` — they are driver interfaces registered by the extension, not Jakarta Data repositories.
- Native compilation depends on the chosen TinkerPop provider — the extension registers repository proxies and reflection, but the provider itself must support native image.
- Only ArangoDB is covered by the integration tests; other providers follow from the TinkerPop API but are untested here.
