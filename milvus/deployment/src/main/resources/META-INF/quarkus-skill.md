### Configuration

- Requires `jnosql.vector.database` (shared vector config) plus `jnosql.milvus.*` connection properties: `jnosql.milvus.host`, `jnosql.milvus.port` (default `19530`), `jnosql.milvus.collection.metric` and `jnosql.milvus.collection.vector.size`; optional `jnosql.milvus.uri`, `jnosql.milvus.token`, `jnosql.milvus.tls`, `jnosql.milvus.timeout`, `jnosql.milvus.consistency`, `jnosql.milvus.collection.string.max.length`.
- The collection is named after the entity.

### Data access

- Follows the shared vector pattern: `VectorTemplate` with `@Database(DatabaseType.VECTOR)`, `DenseVector` entity fields, and `BasicRepository` Jakarta Data repositories — see the `quarkus-jnosql-vector` skill for details.

### Testing

- Use `@QuarkusTest` with `MilvusTestResource` (Testcontainers). The `milvusdb/milvus:v3.0.4` image needs `DEPLOY_MODE=STANDALONE` to run with the embedded etcd — copy that environment setting when writing your own test resource.

### Common Pitfalls

- Supported metrics: `COSINE`, `EUCLIDEAN`, `INNER_PRODUCT`.
- `collection.vector.size` must match the `DenseVector` dimension used at runtime.
- Native compilation is not verified for this extension.
