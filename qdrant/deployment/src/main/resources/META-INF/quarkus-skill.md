### Configuration

- Requires `jnosql.vector.database` (shared vector config) plus `jnosql.qdrant.*` connection properties: `jnosql.qdrant.host`, `jnosql.qdrant.port` (gRPC, default `6334`), `jnosql.qdrant.collection.distance` and `jnosql.qdrant.collection.vector.size`; optional `jnosql.qdrant.api.key`, `jnosql.qdrant.tls`, `jnosql.qdrant.timeout` (milliseconds).
- The collection is named after the entity and is created on the first insert when a distance is configured.

### Data access

- Follows the shared vector pattern: `VectorTemplate` with `@Database(DatabaseType.VECTOR)`, `DenseVector` entity fields, and `BasicRepository` Jakarta Data repositories — see the `quarkus-jnosql-vector` skill for details.

### Testing

- Use `@QuarkusTest` with `QdrantTestResource` (Testcontainers) returning the `jnosql.qdrant.*` properties; the codestart's `ArticleRepositoryTest` is a working example.

### Common Pitfalls

- Supported distances: `COSINE`, `EUCLID`, `DOT`, `MANHATTAN` — other values fail configuration.
- Entities may have a single vector field; TTL on save is not supported.
- `collection.vector.size` must match the `DenseVector` dimension used at runtime.
- Native compilation is not verified for this extension.
