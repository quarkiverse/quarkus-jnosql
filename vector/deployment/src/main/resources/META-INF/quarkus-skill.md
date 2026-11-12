### Vector entities

- Entities are regular `@Entity` classes (or records) with a `DenseVector` field mapped with `@Column`, plus `@Id` on the identifier.
- The JNoSQL Lite annotation processor requires a setter for every mapped field, including the vector field — records satisfy this through their canonical constructor.
- Only `DenseVector` is exercised by the integration tests; other vector representations depend on each driver.

### Configuration

- `jnosql.vector.database` is required — the producer throws `MappingException` when it is missing or blank.
- `jnosql.vector.provider` selects the `DatabaseConfiguration` implementation and is required only when more than one JNoSQL vector provider is on the classpath, for example `org.eclipse.jnosql.databases.qdrant.communication.QdrantConfiguration`.
- Each database extension adds its own `jnosql.<db>.*` connection properties (host, port, collection distance/vector size, credentials). See the database-specific guide.

### Data access

- Inject `VectorTemplate` with `@Inject @Database(DatabaseType.VECTOR)`.
- Persist with `template.insert(entity)` or `template.upsert(entity)`; read with `template.find(Entity.class, id)`.
- Similarity search: `template.search(Entity.class).vector(DenseVector.of(...)).where("field").eq(value).limit(Limit.of(n)).result()`.
- Jakarta Data works: declare `@Repository` interfaces extending `BasicRepository<Entity, String>` — `save` (insert/update), `findById`, `deleteById` and derived queries like `findByCategory` are verified against each database.

### Testing

- Use `@QuarkusTest` with a `QuarkusTestResourceLifecycleManager` starting the database via Testcontainers (see `QdrantTestResource`, `MilvusTestResource`, `WeaviateTestResource`).
- Pin the database image version in the test resource, as the existing resources do.
- Verify save/insert, findById, deleteById and one derived query plus a similarity search through `VectorTemplate`.

### Common Pitfalls

- Do NOT forget `jnosql.vector.database` — the application fails at startup without it.
- Do NOT rely on a single provider when several vector extensions are on the classpath; set `jnosql.vector.provider` explicitly.
- Vector dimensions and collection creation are driver-specific (Qdrant creates the collection on first insert when a distance is configured); do not assume portable behavior.
- Native compilation has not been verified for these extensions — do not claim native support.
