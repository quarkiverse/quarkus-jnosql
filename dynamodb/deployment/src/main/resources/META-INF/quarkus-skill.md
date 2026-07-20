### Required Setup Procedure

1. First load this `quarkus-jnosql-dynamodb` skill.
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
- DynamoDB support is key-value oriented.
- DynamoDB does not support Jakarta Data repositories in this extension; use `Template` or `BucketManager` for key-value access.
- Configure the key-value database name with `jnosql.keyvalue.database`.
- Use `jakarta.nosql.Template` for mapped key-value entity operations.
- Inject `org.eclipse.jnosql.communication.keyvalue.BucketManager` when direct bucket operations are needed.

### Testing

- Use `@QuarkusTest` for DynamoDB extension tests.
- The current integration tests do not attach a custom `QuarkusTestResource`; rely on the configured DynamoDB service or Dev Services behavior.
- Test both `Template` operations and direct `BucketManager` operations when both APIs are used.

### Common Pitfalls

- Do not use document or column configuration properties for DynamoDB key-value tests.
- Do not create `NoSQLRepository` interfaces for DynamoDB; this extension is key-value only and does not provide Jakarta Data repository support.
