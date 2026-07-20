### Installation And Build Setup

- For Java 21 or later, configure `org.eclipse.jnosql.lite:mapping-lite-processor` and register `org.eclipse.jnosql.lite.mapping.EntityProcessor` and `org.eclipse.jnosql.lite.mapping.repository.RepositoryProcessor`.

### Entity Mapping

- Model entities with `@Entity`, `@Id`, and `@Column`; Java records and `@Embeddable` value objects are supported when annotated correctly.
- For deeper annotation and embeddable semantics, consult the Jakarta NoSQL specification: https://jakarta.ee/specifications/nosql/1.0/jakarta-nosql-1.0

### Data Access Patterns

- Use `jakarta.nosql.Template` for template operations and `@Repository` plus `NoSQLRepository<Entity, Id>` only when this backend supports Jakarta Data.
- Use `@Database(DatabaseType.DOCUMENT)`, `@Database(DatabaseType.COLUMN)`, `@Database(DatabaseType.GRAPH)`, or `@Database(DatabaseType.KEY_VALUE)` when CDI needs model disambiguation.
- For more details about `Template` behavior and operations, consult the Jakarta NoSQL specification: https://jakarta.ee/specifications/nosql/1.0/jakarta-nosql-1.0
- For more details about Jakarta Data repositories, query methods, pagination, and sorting, consult the Jakarta Data specification and addendum: https://jakarta.ee/specifications/data/1.0/jakarta-data-1.0 and https://jakarta.ee/specifications/data/1.0/jakarta-data-addendum-1.0

### Usage Patterns

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

- If tests log `Eclipse JNoSQL Lite does not support reflection, including the use of constructors`, stop Quarkus instances, clean and recompile, then rerun tests before assuming mapping is broken.
- If an error log shows `Unsatisfied dependency for type org.eclipse.jnosql.mapping.metadata.EntitiesMetadata and qualifiers`, check whether the source contains at least one Jakarta NoSQL entity. The `org.eclipse.jnosql.lite:mapping-lite-processor` annotation processor uses Jakarta NoSQL entities to generate the metadata classes required by CDI, so a project with no entities cannot produce that bean. If no entity exists, explain why one is required and offer to create a sample Jakarta NoSQL entity. After adding or fixing an entity, rebuild the project and restart dev mode if it is running.
- Do not use document or column configuration properties for DynamoDB key-value tests.
- Do not create `NoSQLRepository` interfaces for DynamoDB; this extension is key-value only and does not provide Jakarta Data repository support.
