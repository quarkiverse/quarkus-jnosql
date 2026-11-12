### Application-owned Graph

- This extension has NO database dependency: the application must provide a CDI bean of type `org.apache.tinkerpop.gremlin.structure.Graph` via a `@Produces` producer — the extension consumes it and never creates, replaces or closes it.
- Add the TinkerPop provider dependency yourself (e.g. `com.arangodb:arangodb-tinkerpop-provider`); the extension only integrates JNoSQL with whatever `Graph` you supply.
- Without a `Graph` bean the build fails with a message asking for a CDI producer — there is no fallback graph. Dispose of the `Graph` in the producer (`@Disposes`).

### Data access

- Inject `org.eclipse.jnosql.databases.tinkerpop.mapping.TinkerpopTemplate` with `@Inject`.
- Repositories: declare interfaces extending `org.eclipse.jnosql.databases.tinkerpop.mapping.TinkerPopRepository<Entity, IdType>` WITHOUT `@Repository` — the extension registers them as CDI beans automatically.
- Jakarta Data also works: `@Repository` interfaces extending `BasicRepository<Entity, IdType>` resolve through the same application `Graph`.

### Testing

- Use `@QuarkusTest` with a `QuarkusTestResourceLifecycleManager` starting a TinkerPop provider database (the integration tests use `arangodb:3.12.13` via Testcontainers with `arangodb-tinkerpop-provider`).
- `MissingGraphTest` (a `QuarkusUnitTest` in the deployment module) verifies the build-time failure when no `Graph` bean exists.

### Common Pitfalls

- With the ArangoDB provider, `TinkerpopTemplate.insert` and `TinkerPopRepository.save` fail because the JNoSQL driver writes the `_id` property, which that provider reserves — create vertices through the application's `Graph` and read them back through JNoSQL.
- Do NOT annotate `TinkerPopRepository` interfaces with `@Repository` — they are driver interfaces registered by the extension, not Jakarta Data repositories.
- Native compilation depends on the chosen TinkerPop provider — the extension registers repository proxies and reflection, but the provider itself must support native image.
- Only ArangoDB is covered by the integration tests; other providers follow from the TinkerPop API but are untested here.
