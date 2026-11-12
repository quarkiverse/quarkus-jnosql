### Configuration

- Requires `jnosql.vector.database` (shared vector config) plus `jnosql.weaviate.*` connection properties: `jnosql.weaviate.host`, `jnosql.weaviate.http.port` (default `8080`), `jnosql.weaviate.grpc.port` (default `50051`), `jnosql.weaviate.collection.distance`; optional `jnosql.weaviate.api.key`, `jnosql.weaviate.secure`, `jnosql.weaviate.timeout`.
- The client uses BOTH HTTP and gRPC — both ports must be reachable.

### Data access

- Follows the shared vector pattern: `VectorTemplate` with `@Database(DatabaseType.VECTOR)`, `DenseVector` entity fields, and `BasicRepository` Jakarta Data repositories — see the `quarkus-jnosql-vector` skill for details.

### Testing

- Use `@QuarkusTest` with `WeaviateTestResource` (Testcontainers) returning the `jnosql.weaviate.*` properties; the codestart's `ArticleRepositoryTest` is a working example.

### Common Pitfalls

- Supported distances: `COSINE`, `DOT`, `EUCLIDEAN`, `MANHATTAN`, `HAMMING`.
- Exposing only one of the HTTP/gRPC ports breaks the client — both are required.
- Native compilation is not verified for this extension.
