# Common Guide: Developing a Database Extension

Shared guide for every Quarkus JNoSQL database extension, regardless of NoSQL model. It describes how the repository is
structured, how an extension is built, and what must be verified before it is merged.

All paths are relative to the repository root. Examples use `mongodb` (document), `redis` (key-value), `cassandra`
(column), `influxdb` (time series), `tinkerpop` (graph) and `qdrant`/`vector` (vector) because they exist in the tree.
When this guide and the source disagree, the source wins.

## 1. Architecture

### 1.1 Responsibilities

| Concern | Eclipse JNoSQL | Quarkus JNoSQL (this repository) |
|---|---|---|
| Database protocol, client, query translation | Driver `org.eclipse.jnosql.databases:jnosql-<db>` | Never reimplemented |
| Communication layer (`DatabaseManager`, `DatabaseManagerFactory`, `DatabaseConfiguration`, `KeyValueConfiguration`) | Defines the contracts and the driver implementation | Wraps/uses them from CDI beans |
| Mapping layer (entity metadata, converters, templates, repositories) | `jnosql-mapping-*` artifacts | Consumed; entity metadata is generated at compile time by the JNoSQL Lite processor |
| Configuration | `Settings`, `MappingConfigurations`, driver `*Configurations` enums | `MicroProfileSettings` bridges MicroProfile Config (`application.properties`) into `Settings` |
| CDI wiring | Vanilla CDI producers/suppliers (`...ManagerSupplier`) | Arc-compatible producers, bean registration, exclusion of the Eclipse producers |
| Native image | None | Reflection, service providers, runtime-initialized classes |
| Tooling | None | `quarkus-extension.yaml`, codestarts, integration tests |

Why this split matters: the drivers are shared with every Jakarta EE/MicroProfile runtime. Anything Quarkus-specific must
live here, and anything database-specific must live in the driver, otherwise the same behavior is implemented twice and
drifts.

### 1.2 Communication and mapping layers

- **Communication layer**: talks to the database through a manager (for example `MongoDBDocumentManager`,
  `InfluxDBTimeSeriesManager`, a `BucketManager` for key-value, `VectorManager` for vector) created by a factory
  (`...ManagerFactory`) that a configuration class builds from `Settings`. Example in this repository:
  [`QuarkusMongoDBDocumentConfiguration`](../mongodb/runtime/src/main/java/org/eclipse/jnosql/databases/mongodb/communication/QuarkusMongoDBDocumentConfiguration.java)
  implements `DatabaseConfiguration` and returns a `MongoDBDocumentManagerFactory` built on the Quarkus-managed `MongoClient`.
- **Mapping layer**: maps `@Entity` classes to the communication API and provides templates and repositories. Entity
  metadata comes from the JNoSQL Lite annotation processor (`org.eclipse.jnosql.lite:mapping-lite-processor`, declared in
  [`mongodb/runtime/pom.xml`](../mongodb/runtime/pom.xml)) instead of reflection. That is why `jnosql-mapping-reflection`
  is excluded in the runtime POMs (see [`core/runtime/pom.xml`](../core/runtime/pom.xml)).

### 1.3 Templates, repositories and queries

- **Jakarta NoSQL templates** (`DocumentTemplate`, `KeyValueTemplate`, `ColumnTemplate`, `GraphTemplate`,
  `TimeSeriesTemplate`, `VectorTemplate`) and the `@Database(DatabaseType.X)` qualifier are provided by Eclipse JNoSQL
  and resolve the manager produced by the extension. Exercise them in
  [`mongodb/integration-tests/.../JNoSQLResource.java`](../mongodb/integration-tests/src/main/java/io/quarkiverse/jnosql/mongodb/it/JNoSQLResource.java).
- **Jakarta Data repositories** (`@Repository` interfaces) are created by Eclipse JNoSQL's CDI extension. See
  `People.java` in the same module, `PersonBasicRepository` in
  [`redis/integration-tests`](../redis/integration-tests/src/main/java/io/quarkiverse/jnosql/redis/it/PersonBasicRepository.java),
  and [`TinkerpopRepositoryCreator`](../tinkerpop/runtime/src/main/java/io/quarkiverse/jnosql/tinkerpop/runtime/TinkerpopRepositoryCreator.java)
  for a case where the extension supplies its own repository creation.
- **Jakarta Query** is available only where the driver and model support it. Do not assume a query that works on a
  document database works on a key-value, time-series or vector store. Verify support in the driver and test it.

### 1.4 CDI and Quarkus processing

Quarkus uses Arc, a build-time CDI implementation. Consequences for an extension:

- Beans are discovered at build time from the Jandex index. Library beans that are not in an indexed archive with a bean
  defining annotation/`beans.xml` are **not** found unless an `AdditionalBeanBuildItem` registers them.
- Unused beans are removed unless marked unremovable (`AdditionalBeanBuildItem.unremovableOf`).
- The Eclipse JNoSQL producers (`...ManagerSupplier`) read configuration reflectively and would create ambiguous beans
  with the extension's producers, so they are excluded with `ExcludedTypeBuildItem`.

Reference: [`mongodb/deployment/.../Processor.java`](../mongodb/deployment/src/main/java/io/quarkiverse/jnosql/document/mongodb/deployment/Processor.java).

### 1.5 Build-time and runtime split

| Where | What it contains | Example |
|---|---|---|
| `core/runtime`, `<db>/runtime` | Beans and classes loaded by the application | [`AbstractDatabaseManagerProducer`](../core/runtime/src/main/java/io/quarkiverse/jnosql/core/runtime/AbstractDatabaseManagerProducer.java), [`MicroProfileSettings`](../core/runtime/src/main/java/io/quarkiverse/jnosql/core/runtime/MicroProfileSettings.java) |
| `core/deployment`, `<db>/deployment` | `@BuildStep` methods that run during the Quarkus build | [`core Processor`](../core/deployment/src/main/java/io/quarkiverse/jnosql/core/deployment/Processor.java), [`ServiceProviderRegister`](../core/deployment/src/main/java/io/quarkiverse/jnosql/core/deployment/ServiceProviderRegister.java) |

`core` contributes the model-independent build steps: reflective hierarchy for `@Entity`/`@Embeddable` classes and
`ServiceProviderBuildItem` registration for the JNoSQL SPI (`ClassScanner`, `CollectionSupplier`, `ClassConverter`,
`ConstructorBuilderSupplier`, `ValueReader`, `ValueWriter`, `TypeReferenceReader`). A database extension depends on
`core` and must not repeat that work.

## 2. Module structure

The root [`pom.xml`](../pom.xml) lists one module per database (plus `core` and `vector`). Each database module is a pom
parent with up to three children. Real example, `mongodb`:

```text
mongodb/
├── pom.xml                                  # quarkus-jnosql-mongodb-parent (packaging pom; modules runtime, deployment; `it` profile adds integration-tests)
├── runtime/
│   ├── pom.xml                              # quarkus-jnosql-mongodb
│   └── src/main/
│       ├── java/org/eclipse/jnosql/databases/mongodb/communication/
│       │   ├── QuarkusMongoDBDocumentConfiguration.java
│       │   └── QuarkusMongoDBDatabaseManagerProducer.java
│       ├── resources/META-INF/quarkus-extension.yaml
│       └── codestarts/quarkus/quarkus-jnosql-mongodb-codestart/codestart.yml
├── deployment/
│   ├── pom.xml                              # quarkus-jnosql-mongodb-deployment
│   └── src/main/java/io/quarkiverse/jnosql/document/mongodb/deployment/Processor.java
└── integration-tests/
    ├── pom.xml                              # quarkus-jnosql-mongodb-integration-tests
    └── src/
        ├── main/java/.../it/                # sample entities, repositories, REST resource
        ├── main/resources/application.properties
        └── test/java/.../it/ and .../codestart/CodestartTest.java
```

| Module | Purpose | Why it exists |
|---|---|---|
| Parent (`<db>/pom.xml`) | Aggregates children, inherits `quarkus-jnosql-parent` (version, dependency management) | One Maven reactor entry per database |
| Runtime (`<db>/runtime`) | Packaged extension jar: CDI producers/configuration, `quarkus-extension.yaml`, codestart jar | Code that runs inside the application. It is built with the `quarkus-extension-maven-plugin` (goal `extension-descriptor`) which links it to the deployment artifact |
| Deployment (`<db>/deployment`) | `@BuildStep` processor, depends on the runtime and on the `-deployment` artifacts of the Quarkus extensions it relies on | Build-time code must not be shipped to the runtime classpath |
| Integration tests (`<db>/integration-tests`) | A Quarkus application using the extension and tests against a real database | Proves wiring, CDI and native behavior |
| Codestarts | `codestarts/` (shared templates, `key-value` and `semistructured`) and `<db>/runtime/src/main/codestarts/...` (per-extension descriptor) | Generated sample project for `quarkus create` / code.quarkus.io |

Shared versus per-extension codestarts:

- `mongodb/runtime/pom.xml` copies [`codestarts/semistructured/`](../codestarts/semistructured) (resource-copy
  executions `01-copy-global-java-sources` and `02-copy-local-resources`) and packages the result into a jar with the
  `codestarts` classifier (execution `03-generate-codestart-jar`).
- `redis/runtime/pom.xml` and `qdrant/runtime/pom.xml` package only their own `src/main/codestarts` directory; Redis keeps its
  templates under `redis/runtime/src/main/codestarts/quarkus/quarkus-jnosql-redis-codestart/java/`.
- The shared directories under `codestarts/` are `key-value` and `semistructured`.

Variations to be aware of:

- `vector` is a shared runtime/deployment pair (`quarkus-jnosql-vector`) used by `qdrant`, `milvus`, `weaviate`. Their
  `runtime` modules hold mostly POM, `quarkus-extension.yaml` and codestart descriptor (see `qdrant/runtime/src`); the
  producer is in [`vector/runtime`](../vector/runtime/src/main/java/io/quarkiverse/jnosql/vector/runtime/QuarkusVectorManagerProducer.java).
- `tinkerpop` has its own runtime package (`io.quarkiverse.jnosql.tinkerpop.runtime`) because the application owns the
  `Graph`.
- The Maven module directories `*-parent/` at the root (for example `document-parent/`) hold only stale `target/`
  directories and are not tracked by Git. Do not use them as a reference.

## 3. Creating a database extension

Not every step applies to every extension. Steps marked **Required** are needed by all; others depend on the model and on what the driver or the underlying Quarkus extension already provides.

### Step 1: Verify database support in Eclipse JNoSQL (Required)

- Confirm that `org.eclipse.jnosql.databases:jnosql-<db>` exists for the `jnosql.version` set in the root `pom.xml`
  and that the dependency is managed there (see the `dependencyManagement` block).
- Identify which interfaces it implements (`DatabaseConfiguration`, `KeyValueConfiguration`, a `...Configurations` enum,
  its own `...Supplier` CDI beans). These determine what the extension must produce and what it must exclude.
- If the driver does not exist, stop: the work belongs to Eclipse JNoSQL.

### Step 2: Select the model and a reference implementation (Required)

| Model | Reference | Pattern |
|---|---|---|
| Key-value | `redis` | Configuration implements `KeyValueConfiguration`; producers for `BucketManager` and factory |
| Document | `mongodb` (reuses a Quarkus-managed client); `couchdb`, `solr`, `elasticsearch`, `arangodb` | `DatabaseConfiguration` + `AbstractDatabaseManagerProducer` subclass |
| Column | `cassandra`, `scylladb` | Same as document, with column excluded type |
| Graph | `neo4j` (communication-based), `tinkerpop` (application-owned `Graph`) | Different from the other models; read both |
| Time series | `influxdb`, `iotdb`, `questdb` | Producer with `@Database(DatabaseType.TIME_SERIES)` and `@PreDestroy` factory close |
| Vector | `qdrant`, `milvus`, `weaviate` on `vector` | Only POM, extension metadata and codestart; configuration through `jnosql.vector.provider` |

Read the reference's runtime, deployment, integration tests and codestart before writing code.

### Step 3: Create the Maven modules (Required)

1. Create `<db>/pom.xml` with parent `quarkus-jnosql-parent`, packaging `pom`, `artifactId` `quarkus-jnosql-<db>-parent`,
   modules `runtime` and `deployment`, and the `it` profile that adds `integration-tests` (copy from
   [`mongodb/pom.xml`](../mongodb/pom.xml)).
2. Create `runtime`, `deployment`, `integration-tests` modules with artifact IDs `quarkus-jnosql-<db>`,
   `quarkus-jnosql-<db>-deployment`, `quarkus-jnosql-<db>-integration-tests`.
3. Add `<module><db></module>` to the root `pom.xml` (core and vector must stay before the modules that depend on them).

### Step 4: Configure dependencies (Required)

Runtime POM, following [`mongodb/runtime/pom.xml`](../mongodb/runtime/pom.xml):

- `io.quarkiverse.jnosql:quarkus-jnosql-core` (or `quarkus-jnosql-vector`) at `${project.version}`. Exclude the mapping
  artifacts you do not need (for instance `jnosql-mapping-key-value` for a document database).
- The Eclipse driver `org.eclipse.jnosql.databases:jnosql-<db>`, excluding `jnosql-mapping-reflection`.
- `org.eclipse.jnosql.lite:mapping-lite-processor`.
- If Quarkus already has an extension for the client (`quarkus-mongodb-client`), depend on it so that the client is shared
  and configured by Quarkus; add its `-deployment` artifact to the deployment POM.
- Plugins: `maven-resources-plugin` + `maven-jar-plugin` for the codestart jar, `quarkus-extension-maven-plugin` with the
  `extension-descriptor` goal, and `quarkus-extension-processor` as an annotation processor in `maven-compiler-plugin`.

Deployment POM: `quarkus-jnosql-core-deployment`, the runtime artifact, and the `-deployment` artifacts of used Quarkus
extensions. Why: the deployment module must see everything the build steps can reference.

Do not add versions that are already managed by the parent. Add new managed versions to the root `pom.xml` instead of the
module.

### Step 5: Implement runtime integration (Required)

Typical shape (document/column/time series):

1. A configuration `@Singleton` implementing the communication contract and returning the driver factory. Name it
   `Quarkus<Db><...>Configuration` and place it in the driver's package so it can use package-private types
   (`org.eclipse.jnosql.databases.<db>.communication`), as `QuarkusMongoDBDocumentConfiguration` and
   [`QuarkusRedisKeyValueConfiguration`](../redis/runtime/src/main/java/org/eclipse/jnosql/databases/redis/communication/QuarkusRedisKeyValueConfiguration.java) do.
2. A producer `@Singleton` extending `AbstractDatabaseManagerProducer<Manager, Factory, Configuration>` that exposes the
   manager with the same qualifiers used in Eclipse JNoSQL, as in `QuarkusMongoDBDatabaseManagerProducer`:
   `@Produces @Priority(1) @Alternative @Default @Database(DatabaseType.<MODEL>)` and calling
   `get(MappingConfigurations.<MODEL>_DATABASE)`. The `@Alternative @Priority(1)` pair is what makes this bean win over
   any other candidate.
3. Own the lifecycle of the connection: if the factory holds resources, close it (`@PreDestroy` in
   `QuarkusInfluxDBDatabaseManagerProducer`, `@Disposes` in `QuarkusVectorManagerProducer`, a shared factory closed in
   `QuarkusRedisKeyValueConfiguration`). Share one factory per application instead of creating one per injection.

Variations: key-value uses `AbstractBucketManagerProducer` (see `core/runtime`); vector and tinkerpop have dedicated
producers.

### Step 6: Implement deployment build steps (Required)

Create `Processor` in `io.quarkiverse.jnosql.<model>.<db>.deployment` (follow the neighbors' package convention) with:

- `@BuildStep FeatureBuildItem feature()` returning a stable name such as `jnosql-redis`. Every database extension uses
  `jnosql-<db>`; `core` uses `jnosql`.
- `@BuildStep void build(BuildProducer<AdditionalBeanBuildItem>)` registering the configuration and producers with
  `unremovableOf` (Step 7).
- `@BuildStep void buildExcludedType(BuildProducer<ExcludedTypeBuildItem>)` excluding the Eclipse suppliers (Step 7).
- Native-image steps from Step 8 when needed.

Keep it free of runtime logic: no network, no driver instantiation.

### Step 7: Register CDI beans and providers (Required)

1. Register each runtime bean class with `AdditionalBeanBuildItem.unremovableOf(...)`. Without it Arc may not discover
   the class, or removes it as unused because no injection point is visible at build time (the managers are looked up
   by qualifier in Eclipse code).
2. Exclude the duplicates provided by the mapping module and the driver using fully qualified names, so the build does not
   fail on the missing optional class. Examples in this repository:
   - Document: `org.eclipse.jnosql.mapping.document.configuration.DocumentManagerSupplier` and
     `org.eclipse.jnosql.databases.mongodb.mapping.DocumentManagerSupplier`.
   - Column: `org.eclipse.jnosql.mapping.column.configuration.ColumnManagerSupplier`.
   - Key-value: `org.eclipse.jnosql.mapping.keyvalue.configuration.BucketManagerSupplier`,
     `...BucketManagerFactorySupplier` and the driver's `redis.mapping.*` suppliers.
   - Vector: `org.eclipse.jnosql.mapping.vector.configuration.VectorManagerSupplier`.
   Look at the driver jar for the exact class names of the extension you are adding; do not copy these blindly.
3. Provider/SPI files: JNoSQL discovers implementations with `ServiceLoader`. The `core` Processor already registers the
   mapping SPI. Register extra driver SPI with `ServiceProviderRegister.registerService` (a static helper in the
   `io.quarkiverse.jnosql.core.deployment` package of `core/deployment`; see how [`vector`'s Processor](../vector/deployment/src/main/java/io/quarkiverse/jnosql/vector/deployment/Processor.java)
   registers `DatabaseConfiguration`) or `ServiceProviderBuildItem.allProvidersFromClassPath` for third-party interfaces (gRPC in `vector`).

### Step 8: Handle reflection and native-image requirements (Conditional)

Needed when the driver or its client uses reflection, resources, service loading, or classes initialized statically that
cannot be initialized at build time.

- Entities: handled by `core` (`ReflectiveHierarchyBuildItem` for `@Entity`/`@Embeddable`). Do not duplicate.
- Services: Step 7.3.
- Static-initialization problems: `RuntimeInitializedClassBuildItem`, e.g. `RedisConfiguration` in
  [`redis Processor`](../redis/deployment/src/main/java/io/quarkiverse/jnosql/keyvalue/redis/deployment/Processor.java) and the
  gRPC netty holders in the `vector` Processor.
- Proxies/reflective classes: `NativeImageProxyDefinitionBuildItem` and `ReflectiveClassBuildItem`, used in the
  [`tinkerpop` Processor](../tinkerpop/deployment/src/main/java/io/quarkiverse/jnosql/tinkerpop/deployment/Processor.java).

Add native entries only in response to a concrete native build failure or a known driver behavior. Unneeded registrations
increase image size and hide mistakes.

### Step 9: Provide configuration integration (Required)

- The runtime reads values through `MicroProfileSettings`, so Eclipse JNoSQL property names (for example
  `jnosql.document.database`, set in
  [`mongodb/integration-tests/src/main/resources/application.properties`](../mongodb/integration-tests/src/main/resources/application.properties);
  `jnosql.vector.database` and `jnosql.vector.provider` in the Qdrant module) work unchanged in `application.properties`.
- Driver properties come from the driver's `...Configurations` enum (for example `RedisConfigurations.HOST`, which
  [`RedisTestResource`](../redis/integration-tests/src/test/java/io/quarkiverse/jnosql/keyvalue/redis/it/RedisTestResource.java) uses).
- List the config prefixes in `quarkus-extension.yaml` under `metadata.config` (`jnosql.` and `jnosql.<model>.` in
  `mongodb`).
- If a Quarkus extension owns the client (MongoDB), its own `quarkus.*` properties configure the connection; avoid
  introducing parallel properties.
- Do not add new configuration properties unless a driver or Quarkus requirement exists. If you must add a Quarkus
  `@ConfigRoot`, follow Quarkus' documentation and document it.

Create `src/main/resources/META-INF/quarkus-extension.yaml` using
[`mongodb`'s](../mongodb/runtime/src/main/resources/META-INF/quarkus-extension.yaml) as a template.

### Step 10: Implement integration tests (Required)

See [section 5](#5-testing). In short: a Quarkus application in `integration-tests` plus tests that run against a real
database, with a `*Test` class for the JVM run and a `*IT` subclass annotated `@QuarkusIntegrationTest` for packaged/native
runs (`JNoSQLResourceIT` extends `JNoSQLResourceTest`).

### Step 11: Add codestarts (Conditional)

Required when the neighboring extensions of the same model have one (the time-series extensions and `tinkerpop`
currently have none: check `*/runtime/src/main/codestarts`).

1. Add `<db>/runtime/src/main/codestarts/quarkus/quarkus-jnosql-<db>-codestart/codestart.yml` (see MongoDB's: `ref`
   points to the extension artifact, `data.database-type` and `database-name` feed the templates).
2. Reuse `codestarts/semistructured` or `codestarts/key-value` through the runtime POM, or add templates under the extension's `java/` directory (Redis).
3. Add `quarkus-extension.yaml` `metadata.codestart` with `name`, `languages` and `artifact` (`...:codestarts:jar:${project.version}`).
4. Add `CodestartTest` and snapshots in the integration tests (Step 12 below / section 5).

### Step 12: Verify the build and extension behavior (Required)

Run the commands in [section 5.7](#57-commands) and the checklist in [section 7](#7-definition-of-done).

## 4. Quarkus-specific concerns

### 4.1 Build steps and build items

A build step is a method annotated with `@BuildStep` in a deployment class; its parameters are consumed build items and
its return/`BuildProducer` parameters produce build items. Items used in this repository:

| Build item | Used for | Where |
|---|---|---|
| `FeatureBuildItem` | Names the feature, shown in startup logs | every `Processor` |
| `AdditionalBeanBuildItem` | Register classes as beans (`unremovableOf`) | mongodb, redis, cassandra, vector |
| `ExcludedTypeBuildItem` | Remove Eclipse JNoSQL beans from discovery | all database extensions |
| `ServiceProviderBuildItem` | Native `ServiceLoader` registration | core, vector |
| `ReflectiveHierarchyBuildItem` | Reflection for entity graphs | core |
| `RuntimeInitializedClassBuildItem` | Defer static initialization to runtime | redis, vector |
| `NativeImageProxyDefinitionBuildItem`, `ReflectiveClassBuildItem` | Proxies and reflection | tinkerpop |
| `CombinedIndexBuildItem` | Read the Jandex index to find `@Entity` and similar | core, tinkerpop |
| `UnremovableBeanBuildItem` | Keep beans found by predicate | tinkerpop |

Why build-time: Quarkus resolves what it can once during the build so the application starts faster and native image has
a closed world. Anything depending on runtime config must be deferred to runtime beans, not build steps.

### 4.2 CDI/Arc bean discovery

- The Jandex index includes the application and archives that contain `META-INF/beans.xml` or a Jandex index. Application
  test modules here add `src/main/resources/META-INF/beans.xml` (for example in `mongodb/integration-tests`).
- Producer methods using `@Alternative` need `@Priority` to be enabled globally; extension producers use `@Priority(1)`.
- Scope matters: `@ApplicationScoped` producers get client proxies (needed when the produced type is proxyable, and used by
  `QuarkusVectorManagerProducer` and the InfluxDB producer); `MongoDBDocumentManager` producer is dependent-scoped. Mirror the neighbor of the same model.
- A missing bean is a build-time error (unsatisfied dependency), an ambiguous one is also a build-time error. Both are
  caught by building the integration tests module.

### 4.3 Build-time versus runtime initialization

- Do not create a database connection in a build step or a static initializer.
- `Settings` are read at runtime (`MicroProfileSettings` uses `ConfigProvider.getConfig()`).
- Classes that open connections statically must be registered as runtime-initialized for native (Step 8).

### 4.4 Native-image registration

Native support is validated by the native profile (section 5.5). The key rule: register what is demonstrably needed by a
failing native run. Entities are covered by `core`; SPIs by `ServiceProviderRegister`; driver specifics by the
extension's `Processor`.

### 4.5 Configuration handling

Configuration enters through MicroProfile Config only (`MicroProfileSettings`). The base database name key comes from
`MappingConfigurations` (`DOCUMENT_DATABASE`, `TIME_SERIES_DATABASE`, ...). `AbstractDatabaseManagerProducer.get(...)`
throws `MappingException` ("Please, inform the database filling up the property ...") when it is missing; tests should
cover that.

### 4.6 Dependency management

- Versions (`quarkus.version`, `jnosql.version`, `testcontainers.version`, Jakarta APIs) are properties/BOM entries in the
  root `pom.xml`. Do not hardcode versions in modules for managed artifacts.
- Exclude `jnosql-mapping-reflection` and unused mapping modules as the neighbors do, otherwise the reflection-based
  metadata and the Lite metadata both appear on the classpath.
- Prefer the Quarkus extension for a client library when one exists (`quarkus-mongodb-client`), since it provides native
  support and configuration.

### 4.7 Provider discovery and service registration

- JNoSQL providers are discovered by `ServiceLoader` (`META-INF/services/...`). In native mode the implementation
  classes must be registered: `ServiceProviderRegister.registerService` reads the service files from the classpath and
  generates `ServiceProviderBuildItem`s.
- The vector extension additionally supports an explicit provider class via `jnosql.vector.provider`
  (see `qdrant/integration-tests/src/main/resources/application.properties`) and registers the `DatabaseConfiguration` SPI.
- Third-party SPI used by the driver (gRPC) is registered with `ServiceProviderBuildItem.allProvidersFromClassPath`.

## 5. Testing

### 5.1 Unit tests

Plain JUnit tests for logic that does not need Arc. Use sparingly: most of this code is wiring, which only integration tests
prove.

### 5.2 Quarkus extension (deployment) tests

`QuarkusUnitTest` (`io.quarkus.test.QuarkusUnitTest`) starts a minimal application at build/test time and validates
extension behavior. Examples:
[`VectorExtensionTest`](../vector/deployment/src/test/java/io/quarkiverse/jnosql/vector/deployment/VectorExtensionTest.java)
(beans resolve without ambiguity, resources are closed on shutdown, with a fake provider and no database) and
[`MissingGraphTest`](../tinkerpop/deployment/src/test/java/io/quarkiverse/jnosql/tinkerpop/deployment/MissingGraphTest.java)
(build failure message, a negative scenario).

### 5.3 Integration tests against a real database

Located in `<db>/integration-tests`. Pattern:

- `src/main/java/.../it`: entity (`Person`), repository (`People`), and a REST resource
  (`JNoSQLResource`) exposing Jakarta NoSQL template and Jakarta Data repository paths.
- `src/test/java/.../it/JNoSQLResourceTest`: `@QuarkusTest` calling the endpoints with RestAssured (MongoDB's is
  parameterized over `/using-jakarta-nosql`, `/using-jakarta-nosql-record`, `/using-jakarta-data`,
  `/using-jakarta-data-record`).
- `JNoSQLResourceIT`: `@QuarkusIntegrationTest` subclass, runs against the built artifact.
- Database provisioning, choose in this order:
  1. Quarkus Dev Services when the Quarkus client extension provides it (MongoDB: only `jnosql.document.database=test`
     in `application.properties`).
  2. A `QuarkusTestResourceLifecycleManager` that starts a Testcontainers container and returns the driver properties, for
     example [`QdrantTestResource`](../qdrant/integration-tests/src/test/java/io/quarkiverse/jnosql/qdrant/it/QdrantTestResource.java)
     and `RedisTestResource`, attached with `@QuarkusTestResource`.
  3. Initialization scripts when the schema is needed (`cassandra/integration-tests/src/test/resources/init_script.cql`).
- Failing tests that use mocks to simulate the database are not acceptable replacements.

### 5.4 Codestart tests

`CodestartTest` uses `QuarkusCodestartTest` from `quarkus-devtools-testing` to compare generated sources against
snapshots under `src/test/resources/__snapshots__/CodestartTest/testContent/` and to build the generated projects
(`checkGeneratedSource`, `checkGeneratedTestSource`, `buildAllProjects`). When templates change, regenerate the snapshots
by running the test and reviewing the diff.

### 5.5 Native-image verification

The integration-test POMs define a `native-image` profile (activated by `-Dnative`) that sets `skipITs=false` and
`quarkus.package.type=native`, and the failsafe plugin runs `*IT` classes against `${project.build.finalName}-runner`.
CI runs `mvn -B install -Dnative -Dquarkus.native.container-build -Dnative.surefire.skip` (`.github/workflows/build.yml`).
Native verification is run where the driver supports native image; if it does not, document it instead of hiding it.

### 5.6 Negative scenarios and compatibility checks

- Missing or blank database configuration should produce a clear error (see `AbstractDatabaseManagerProducer`,
  `QuarkusVectorManagerProducer`).
- Resources are closed on shutdown (`VectorExtensionTest`).
- Missing application-owned prerequisites fail at build with an actionable message (`MissingGraphTest`).
- Compatibility: build with the `quarkus.version` and `jnosql.version` of the root `pom.xml`; `quarkus-snapshot.yaml` runs the
  build against Quarkus snapshots. Check that adding the module does not change other extensions' behavior (a full reactor
  build).
- Do not assume other databases of the same model support the same operations: test the operations you document.

### 5.7 Commands

Run from the repository root. These come from the POMs and `.github/workflows/build.yml`.

| Goal | Command |
|---|---|
| Full build, as CI does it | `mvn -B clean install -Dno-format` |
| Full build with formatting applied | `mvn clean install` |
| One extension (and what it needs) | `mvn -pl <db> -am clean install` (for `<db>` such as `mongodb`; add `-Dno-format` to skip the formatter) |
| One module's tests | `mvn -pl <db>/integration-tests test` |
| Native verification | `mvn -B install -Dnative -Dquarkus.native.container-build -Dnative.surefire.skip` |
| Run `*IT` tests in JVM mode | `mvn -pl <db>/integration-tests verify -DskipITs=false` |

Notes:

- Integration tests are in the `it` profile, active unless `performRelease=true`.
- `skipITs` is `true` by default in the integration-test POMs; the native profile or `-DskipITs=false` enables it.
- Docker (or a compatible runtime) is required for Testcontainers/Dev Services.
- Formatting is done by the inherited quarkiverse parent configuration; `-Dno-format` disables it. Run the build without
  `-Dno-format` locally and commit the resulting formatting changes.

## 6. Common mistakes

1. **Reimplementing Eclipse JNoSQL functionality.** Query building, entity conversion, templates and repositories belong
   to Eclipse JNoSQL. The extension only produces managers and registers beans. If a feature is missing, fix it upstream.
2. **Assuming every database supports the same query operations.** Key-value, time series, graph and vector stores each
   restrict the operations of the shared APIs. Read the driver and test the exact operations you expose; do not copy a
   document test to a key-value module.
3. **Incorrect CDI registration.** Forgetting `unremovableOf`, forgetting to exclude the Eclipse `*Supplier` beans
   (ambiguous resolution at build), or omitting `@Alternative @Priority(1) @Default @Database(...)` on the producer.
   Symptoms are build-time unsatisfied/ambiguous dependency errors or the wrong manager being injected.
4. **Ignoring native-image constraints.** Static connections, `ServiceLoader` providers and reflection used by drivers fail
   only in native builds. Run the native profile when the driver supports it.
5. **Confusing runtime and deployment responsibilities.** Runtime must not depend on `io.quarkus.deployment.*`; the deployment
   module must not contain code the application calls. Build steps never touch the database.
6. **Introducing provider-specific logic into shared modules.** `core` and `vector` are shared. A driver-specific class or
   condition in `core/*` affects every extension; keep it in the extension's `runtime` or `deployment` module.
7. **Writing tests that do not exercise a real database integration.** A test that stubs the manager proves nothing about
   the producer, configuration, Jakarta Data or the native build. Use Dev Services or Testcontainers.
8. **Leaking connections.** Create a factory once and close it on shutdown.
9. **Inventing configuration properties or capabilities.** Reuse driver `*Configurations` and Quarkus properties.
10. **Leaving the reflection exclusion out of POMs**, which pulls conflicting mapping metadata onto the classpath.

## 7. Definition of Done

### Architecture
- [ ] Driver support confirmed in Eclipse JNoSQL; no functionality duplicated.
- [ ] Module layout follows section 2 (`<db>/pom.xml`, `runtime`, `deployment`, `integration-tests`).
- [ ] Runtime has no build-time APIs; deployment has no application logic.
- [ ] No provider-specific code added to `core` or `vector`.

### Dependencies
- [ ] Driver, `quarkus-jnosql-core`/`-vector` and `mapping-lite-processor` declared; `jnosql-mapping-reflection` excluded.
- [ ] Versions managed in the root `pom.xml`; module added to root `<modules>`.
- [ ] Quarkus client extension (and its `-deployment` artifact) used where available.

### CDI integration
- [ ] Configuration and producers registered with `AdditionalBeanBuildItem.unremovableOf`.
- [ ] Eclipse suppliers excluded with `ExcludedTypeBuildItem` and verified against the driver jar.
- [ ] Producer qualifiers match the model (`@Database(DatabaseType.<MODEL>)`); template and repository injection work.
- [ ] Resources are closed on shutdown.

### Configuration
- [ ] Settings flow through MicroProfile Config; only documented existing properties are used.
- [ ] `quarkus-extension.yaml` lists name, keywords, config prefixes, guide and codestart (if any).
- [ ] Missing mandatory configuration fails with a clear message.

### Test coverage
- [ ] `*Test` and `*IT` integration tests against a real database (Dev Services or Testcontainers).
- [ ] Jakarta NoSQL template and Jakarta Data repository paths covered where the model supports them.
- [ ] Negative scenarios covered; `QuarkusUnitTest` used for build-time behavior when relevant.
- [ ] Codestart test and snapshots updated when a codestart exists.
- [ ] Native build verified, or the limitation documented.

### Documentation
- [ ] Extension described in the user documentation (`docs/modules/ROOT/pages`) following the existing pages when a
      user-facing extension is added; the README list is updated if it enumerates extensions.
- [ ] Model-specific guide under `development-guides/` updated if it exists and the model patterns changed.

### Build validation
- [ ] `mvn -B clean install -Dno-format` passes; the build passes with formatting enabled and no formatting diff remains.
- [ ] `mvn -B install -Dnative -Dquarkus.native.container-build -Dnative.surefire.skip` passes where supported.
- [ ] Other extensions still build (full reactor).

### Git history
- [ ] Dedicated branch; Conventional Commits (`feat(<db>): ...`, `test(<db>): ...`, `docs(<db>): ...`).
- [ ] One logical change per commit; no unrelated changes; nothing pushed automatically.
