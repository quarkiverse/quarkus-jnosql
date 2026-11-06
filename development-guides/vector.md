# Vector Extension Development Guide

Blueprint for adding a Quarkus JNoSQL extension for a **vector** database. Reference implementation:
**Qdrant** (`qdrant/`) together with the shared `vector/` module it builds on. Read [`common.md`](common.md) first for
the generic module layout, build items, CDI/Arc, testing commands and the generic Definition of Done. This guide covers
only what is specific to the vector model.

Qdrant is an architectural example. A different vector provider may not support the same vector kinds, filters, thresholds or
collection management; verify its driver before reusing any statement here.

## 1. Purpose

A vector extension lets an application inject, for a vector database:

- `org.eclipse.jnosql.mapping.vector.VectorTemplate`, qualified with `@Database(DatabaseType.VECTOR)`,
- the communication-layer `org.eclipse.jnosql.mapping.vector.VectorManager`,
- Jakarta Data repositories (`BasicRepository`) over entities holding a vector field.

Unlike other models, the Quarkus side of vector support is **shared**: `vector/` already produces the `VectorManager`
for any provider selected by configuration. A provider extension mostly adds the driver dependency, native-image support
for that driver and tests.

## 2. Architecture

| Concern | Owner | Source |
|---|---|---|
| Vector types and search API | Eclipse JNoSQL `jnosql-mapping-vector` | `Vector`, `DenseVector`, `SparseVector`, `MultiVector`, `VectorTemplate`, `VectorSearch`, `VectorManager` (package `org.eclipse.jnosql.mapping.vector`) |
| Vector dimension validity of a single vector | Eclipse JNoSQL | `DenseVector.of(float...)` throws `IllegalArgumentException` for zero dimensions |
| Provider communication, collection creation, point/payload conversion, filter conversion, dimension/representation validation | Driver `org.eclipse.jnosql.databases:jnosql-qdrant` | `QdrantVectorManager`, `QdrantVectors`, `QdrantPointConverter`, `QdrantFilterConverter`, `QdrantConfigurations` |
| Selecting the provider, producing and closing the `VectorManager` | **Shared Quarkus module `vector/`** | `QuarkusVectorManagerProducer` |
| Common native-image registrations (DatabaseConfiguration SPI, gRPC providers, Netty holders), excluding `VectorManagerSupplier` | **Shared Quarkus module `vector/`** | `vector/deployment/.../Processor.java` |
| Provider dependencies, extension metadata, codestart, provider tests | **Provider extension (`qdrant/`)** | `qdrant/runtime`, `qdrant/deployment`, `qdrant/integration-tests` |

Why shared: every vector driver implements `DatabaseConfiguration` and returns a `VectorManager`, so one producer
selected via `jnosql.vector.provider` works for all, instead of one producer per provider.

## 3. Reference implementation

### 3.1 Maven modules

```text
vector/                            # shared: quarkus-jnosql-vector (+ -deployment)
├── runtime/
└── deployment/
qdrant/
├── pom.xml                        # quarkus-jnosql-qdrant-parent
├── runtime/pom.xml                # quarkus-jnosql-qdrant: no Java classes, only the pom, metadata and codestart
├── deployment/pom.xml             # quarkus-jnosql-qdrant-deployment
└── integration-tests/pom.xml      # quarkus-jnosql-qdrant-integration-tests
```

Runtime dependencies ([`qdrant/runtime/pom.xml`](../qdrant/runtime/pom.xml)): `quarkus-jnosql-vector`, `jnosql-qdrant`
(excluding `jnosql-mapping-reflection` and the shaded gRPC netty artifact), `io.grpc:grpc-netty`, `quarkus-netty`,
`mapping-lite-processor`. The deployment module depends on `quarkus-jnosql-vector-deployment`, the runtime module and `quarkus-netty-deployment`.

### 3.2 Source map

| Concern | File |
|---|---|
| Shared producer | [`vector/runtime/src/main/java/io/quarkiverse/jnosql/vector/runtime/QuarkusVectorManagerProducer.java`](../vector/runtime/src/main/java/io/quarkiverse/jnosql/vector/runtime/QuarkusVectorManagerProducer.java) |
| Shared build steps | `vector/deployment/src/main/java/io/quarkiverse/jnosql/vector/deployment/Processor.java` (see [`vector/deployment`](../vector/deployment)) |
| Shared deployment test | [`vector/deployment/src/test/java/io/quarkiverse/jnosql/vector/deployment/VectorExtensionTest.java`](../vector/deployment/src/test/java/io/quarkiverse/jnosql/vector/deployment/VectorExtensionTest.java) |
| Qdrant build step (feature only) | [`qdrant/deployment/src/main/java/io/quarkiverse/jnosql/vector/qdrant/deployment/Processor.java`](../qdrant/deployment/src/main/java/io/quarkiverse/jnosql/vector/qdrant/deployment/Processor.java) |
| Metadata | [`qdrant/runtime/src/main/resources/META-INF/quarkus-extension.yaml`](../qdrant/runtime/src/main/resources/META-INF/quarkus-extension.yaml) |
| Codestart | [`qdrant/runtime/src/main/codestarts/quarkus/quarkus-jnosql-qdrant-codestart/codestart.yml`](../qdrant/runtime/src/main/codestarts/quarkus/quarkus-jnosql-qdrant-codestart/codestart.yml) |
| Entity, repository, REST resource | [`qdrant/integration-tests/src/main/java/io/quarkiverse/jnosql/qdrant/it/`](../qdrant/integration-tests/src/main/java/io/quarkiverse/jnosql/qdrant/it) (`Article`, `ArticleRepository`, `JNoSQLResource`) |
| Tests | [`qdrant/integration-tests/src/test/java/io/quarkiverse/jnosql/qdrant/it/`](../qdrant/integration-tests/src/test/java/io/quarkiverse/jnosql/qdrant/it) (`QdrantVectorTemplateTest`, `QdrantArticleRepositoryTest`, `JNoSQLResourceTest`, `JNoSQLResourceIT`, `QdrantTestResource`), [`.../qdrant/codestart/CodestartTest.java`](../qdrant/integration-tests/src/test/java/io/quarkiverse/jnosql/qdrant/codestart/CodestartTest.java) |
| Test app config | [`application.properties`](../qdrant/integration-tests/src/main/resources/application.properties) |

### 3.3 Build steps

**Shared `vector/deployment` `Processor`** (feature `jnosql-vector`):

- Excludes `org.eclipse.jnosql.mapping.vector.configuration.VectorManagerSupplier`.
- Registers the `DatabaseConfiguration` service and the gRPC providers `io.grpc.ManagedChannelProvider`, `io.grpc.NameResolverProvider`, `io.grpc.LoadBalancerProvider` through `ServiceProviderRegister`/`ServiceProviderBuildItem`.
- Registers `RuntimeInitializedClassBuildItem` for `io.grpc.netty.Utils$ByteBufAllocatorPreferDirectHolder` and `...PreferHeapHolder`.

**Qdrant `Processor`** (feature `jnosql-qdrant`): only `FeatureBuildItem`. There is no Qdrant-specific producer, configuration bean or exclusion,
because the shared module covers them.

### 3.4 Configuration

[`application.properties`](../qdrant/integration-tests/src/main/resources/application.properties):

```properties
jnosql.vector.database=articles
jnosql.vector.provider=org.eclipse.jnosql.databases.qdrant.communication.QdrantConfiguration

jnosql.qdrant.collection.distance=COSINE
jnosql.qdrant.collection.vector.size=4
jnosql.qdrant.timeout=10000
```

- `jnosql.vector.database` (`MappingConfigurations.VECTOR_DATABASE`) is required and must be non-blank, or `MappingException` is thrown by `QuarkusVectorManagerProducer`.
- `jnosql.vector.provider` (`VECTOR_PROVIDER`) is optional in the producer; it is a `DatabaseConfiguration` class. The Qdrant tests set it explicitly.
- `jnosql.qdrant.*` keys are defined by `QdrantConfigurations` (`HOST`, `PORT`, `TLS`, `API_KEY`, `TIMEOUT`, `DISTANCE`, `VECTOR_SIZE`); `QdrantTestResource` overrides `HOST` and `PORT` for the container's gRPC port.

## 4. Implementation workflow

**R** = required, **O** = optional. Steps match [`common.md`](common.md) section 3.

1. **Prerequisites (R).** Confirm `jnosql-<db>` exists. Read its manager and converter: supported `Vector` kinds, how collections are created, what filters it supports, how dimension/size mismatches are reported, and its configuration enum. Check the shared `vector/` module covers its transport (for example gRPC); extend it only if it does not.
2. **Maven modules (R).** `<db>/pom.xml`, `runtime`, `deployment`, `integration-tests`, as in `common.md`; register in the root `pom.xml` (the `qdrant` module is listed there).
3. **Dependencies (R).** Runtime depends on `quarkus-jnosql-vector` and the driver (excluding `jnosql-mapping-reflection`), plus transport dependencies the driver needs and Quarkus compatible equivalents (here `grpc-netty` replacing the shaded variant, and `quarkus-netty`). Deployment depends on `quarkus-jnosql-vector-deployment`.
4. **Runtime (usually none).** The reference has no Java in `runtime`. Add a producer only if the provider cannot be built through `jnosql.vector.provider`.
5. **Deployment (R).** A `Processor` with a `FeatureBuildItem`. Add native registrations only for classes that are specific to the provider and not already in the shared `Processor`.
6. **CDI (R).** Provided by the shared `QuarkusVectorManagerProducer` (`@Database(DatabaseType.VECTOR)`).
7. **Configuration (R).** Set `jnosql.vector.database` and `jnosql.vector.provider`; document driver keys from its configuration enum.
8. **Native image (R when native is supported).** Shared registrations cover gRPC; verify any additional driver needs with a native build.
9. **Integration tests (R).** Section 7.
10. **Build validation (R).** Section 7 and the Definition of Done.
11. **Codestart (O, present for Qdrant).** `codestart.yml` sets `database-name: "articles"` and adds `testcontainers-qdrant` and `assertj-core` as test dependencies; templates provide `Article`, `ArticleRepository`, `ArticleRepositoryTest`, `QdrantTestResource`.

## 5. Reference code

Entity with a dense vector (`Article`):

```java
@Entity
public class Article {
    @Id
    private String id;
    @Column
    private String title;
    @Column
    private String category;
    @Column
    private DenseVector embedding;
    // constructors, getters, setters
}
```

Repository (`ArticleRepository`):

```java
@Repository
public interface ArticleRepository extends BasicRepository<Article, String> {
    List<Article> findByCategory(String category);
}
```

Similarity search through `VectorTemplate` (`QdrantVectorTemplateTest`):

```java
List<Article> filtered = template.search(Article.class)
        .vector(DenseVector.of(0f, 0f, 1f, 0f))
        .where("category").eq("y")
        .limit(Limit.of(5))
        .result();
```

Shared producer (`QuarkusVectorManagerProducer`, abbreviated):

```java
@Produces
@Priority(1)
@Alternative
@Default
@ApplicationScoped
@Database(DatabaseType.VECTOR)
public synchronized VectorManager get() {
    var database = settings.get(VECTOR_DATABASE, String.class)
            .filter(name -> !name.isBlank())
            .orElseThrow(() -> new MappingException("Please, configure a non-blank database name using "
                    + VECTOR_DATABASE.get()));
    factory = configuration().apply(settings);
    var manager = factory.apply(database);
    // must be a VectorManager, otherwise the manager and factory are closed and MappingException is thrown
    ...
}
```

The producer also has a `@Disposes` method closing the manager and the factory.

### Vector kinds and search API (verified in `jnosql-mapping-vector` 1.2.0 sources)

| Type | Factory methods | Accessors |
|---|---|---|
| `DenseVector` | `DenseVector.of(float... values)` | `dimensions()`, `values()` |
| `SparseVector` | `SparseVector.of(int dimensions, Element... elements)`, `SparseVector.element(int index, float value)` | `dimensions()`, `indices()`, `values()` |
| `MultiVector` | `MultiVector.of(DenseVector... vectors)` | `size()`, `dimensions()`, `vectors()` |

Search chain (`VectorTemplate.search(Class<T>)` → `VectorSearch.MapperFrom<T>`): `vector(Vector)` → optional
`where(String)` with `eq`, `ne`, `gt`, `gte`, `lt`, `lte`, and `and`/`or` → optional `threshold(float)` → `limit(Limit)` → `result()` returning `List<T>`.

## 6. Design decisions and trade-offs

| Decision | Why | Reusable? |
|---|---|---|
| One shared `vector/` module with a provider-agnostic producer | All vector drivers expose `DatabaseConfiguration` → `VectorManager`; provider chosen via `jnosql.vector.provider` | Yes |
| Producer replaces `VectorManagerSupplier` | The Eclipse supplier does not keep the factory, so the connection could not be closed on shutdown | Yes |
| Qdrant runtime has no Java classes | Nothing provider-specific remains after the shared producer | Applies to providers that follow the same SPI |
| `grpc-netty` instead of the shaded transport, plus `quarkus-netty` | Quarkus' Netty is used; the driver's shaded gRPC would duplicate it | Specific to gRPC/Netty-based drivers |
| gRPC SPI and Netty holder registrations placed in the shared `Processor` | Needed by any gRPC-based vector driver | Yes for gRPC drivers; HTTP drivers may not need them |
| Dimension and collection checks stay in the driver | `QdrantVectorManager` creates the collection on first insert (needs `jnosql.qdrant.collection.distance`), rejects mismatching vector size and mixed representations | Database-specific |
| Qdrant representation: dense → unnamed vector; sparse → named vector `sparse` with dimensions kept in payload key `_sparse_dimensions`; multi → unnamed multivector | Qdrant storage model | Database-specific |

### Responsibilities: shared vs extension

- **Eclipse JNoSQL**: vector types, search DSL, `VectorTemplate`, entity mapping, repositories.
- **Driver**: translating vectors, filters, thresholds and limits to the provider; creating collections; validating sizes.
- **Quarkus shared module**: producing and closing the manager, build-time and native registration common to drivers.
- **Provider extension**: dependencies, provider-specific native/classpath concerns, codestart, tests.

## 7. Testing

- **Shared deployment test**: `VectorExtensionTest` (`QuarkusUnitTest` with a fake provider) verifies the manager and factory are closed on shutdown.
- **Test resource**: `QdrantTestResource` starts `org.testcontainers.qdrant.QdrantContainer("qdrant/qdrant:v1.20.0")` and sets `QdrantConfigurations.HOST` and `PORT` to the mapped gRPC port 6334.
- **`QdrantVectorTemplateTest`**: injects `VectorTemplate` and `VectorManager` (`manager.name()` is `"articles"`), maps payload and `find`, runs nearest-neighbour search with `limit` and a payload filter (`where("category").eq("y")`).
- **`QdrantArticleRepositoryTest`**: repository resolvable via CDI; `save`, `findById`, update by saving again, `deleteById`; derived query `findByCategory`.
- **`JNoSQLResourceTest`/`JNoSQLResourceIT`**: REST flow seeding three articles, `GET /jnosql/articles/{id}`, and `GET /jnosql/search` (expects ids `1` and `3`).
- **`CodestartTest`**: snapshot of generated `Article`, `ArticleRepository`, `ArticleRepositoryTest`.
- **Not covered by the Qdrant integration tests**: `SparseVector`, `MultiVector`, `threshold(...)`, dimension-mismatch and missing-distance error paths. These behaviours exist in the driver sources, but a new extension must add tests for those it claims.

```bash
mvn -pl vector,qdrant -am clean install                  # shared module and provider
mvn -pl qdrant/integration-tests verify -DskipITs=false
mvn -B clean install -Dno-format
```

Docker is required. See [`common.md`](common.md#5-testing) for native commands.

## 8. Definition of Done

Complete the [common checklist](common.md#7-definition-of-done) plus:

- [ ] Driver capabilities (dense/sparse/multi, filters, threshold, collection management) read from the driver and documented; unsupported ones not claimed.
- [ ] Shared `vector/` module reused; any new provider-specific registration lives in the provider extension.
- [ ] `jnosql.vector.database` and `jnosql.vector.provider` documented and set in the test app.
- [ ] Transport dependencies align with Quarkus (for example Netty) and native registrations verified.
- [ ] Tests: template insert/find, similarity search with limit, filtered search, repository CRUD and derived query against a real container.
- [ ] Tests for each vector kind and error path the extension claims (dimension mismatch, missing collection settings).
- [ ] Codestart and `CodestartTest` snapshots present when the conventions require them.
- [ ] Native build run or limitation noted.
- [ ] Conventional Commits, one logical change per commit.
