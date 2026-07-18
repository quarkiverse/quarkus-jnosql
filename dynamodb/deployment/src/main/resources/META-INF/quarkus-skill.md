### Usage Patterns

- This extension brings `quarkus-jnosql-core` transitively. Use the JNoSQL Core skill for shared installation, entity mapping, generic `Template` and repository patterns, Java 21+ annotation processor setup, and generated-code troubleshooting.
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
