### Configuration

- `jnosql.keyvalue.database` is required — the DynamoDB table name.
- The connection is owned by `quarkus-amazon-dynamodb` (Quarkiverse Amazon Services): configure credentials, region and endpoint through its `quarkus.amazon-dynamodb.*`/AWS SDK properties — do not invent parallel JNoSQL connection properties.
- DynamoDB exposes more than one database type, so injecting templates, managers or repositories requires the `@Database(DatabaseType.KEY_VALUE)` qualifier.

### Data access

- Inject `jakarta.nosql.Template` with `@Inject @Database(DatabaseType.KEY_VALUE)`.
- Inject `DynamoDBBucketManager` (or `BucketManager`) for key-based access.
- Repositories extend `jakarta.data.repository.BasicRepository<Entity, IdType>` with `@Repository` and MUST also carry `@Database(DatabaseType.KEY_VALUE)` to disambiguate.

### Testing

- Use `@QuarkusTest` with `@Database(DatabaseType.KEY_VALUE)` on injected repositories; `PersonBasicRepositoryTest` covers save/findById/update/deleteById and asserts `findAll()`/`findAll(PageRequest, Order)` throw `UnsupportedOperationException`.

### Common Pitfalls

- `findAll()` and derived queries are NOT supported — they throw `UnsupportedOperationException`. `BasicRepository` has no `existsById`; use `findById(id).isPresent()`.
- Do NOT forget `@Database(DatabaseType.KEY_VALUE)` on injection points — without it resolution is ambiguous or fails.
- The table must exist before use; the extension does not create it.
