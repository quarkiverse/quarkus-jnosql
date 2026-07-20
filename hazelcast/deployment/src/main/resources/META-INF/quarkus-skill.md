### Required Setup Procedure

1. First load this `quarkus-jnosql-hazelcast` skill.
2. Then immediately load and follow the `quarkus-jnosql-core` skill before making code or build changes.
3. Treat the `quarkus-jnosql-core` skill as authoritative for:
   - installation steps
   - annotation processor configuration
   - entity metadata generation
   - repository setup
   - generated-code troubleshooting
4. Use codestarts only as fallback references if the Core skill is unavailable or incomplete.

### Usage Patterns

- This extension brings `quarkus-jnosql-core` transitively. Use the `quarkus-jnosql-core` skill for shared installation, entity mapping, generic `Template` and repository patterns, Java 21+ annotation processor setup, and generated-code troubleshooting.
- Hazelcast support is key-value oriented.
- Hazelcast does not support Jakarta Data repositories in this extension; use `Template` or `BucketManager` for key-value access.
- Configure the key-value database name with `jnosql.keyvalue.database`.
- Use `jakarta.nosql.Template` for mapped key-value entity operations.
- Inject `org.eclipse.jnosql.communication.keyvalue.BucketManager` for direct key-value bucket operations.

### Testing

- Use `@QuarkusTest` with `@QuarkusTestResource(HazelcastServerTestResource.class)`.
- Test both `Template` and `BucketManager` paths when the application exposes both APIs.
- For `BucketManager`, assert values by retrieving the key and converting the returned value with `value.get(Entity.class)`.

### Common Pitfalls

- Do not omit the Hazelcast server test resource when tests require an ephemeral Hazelcast server.
- Do not use document, column, or graph `DatabaseType` qualifiers for Hazelcast key-value injections.
- Do not create `NoSQLRepository` interfaces for Hazelcast; this extension is key-value only and does not provide Jakarta Data repository support.
