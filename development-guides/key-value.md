# Key-Value Extension Development Guide

Blueprint for adding a Quarkus JNoSQL extension for a **key-value** database. Reference implementation: **Redis**
(`redis/`). Read [`common.md`](common.md) first: it covers the generic module layout, build items, CDI/Arc, native image
and the Definition of Done. This guide only covers what is specific to the key-value model.

Redis is an architectural example, not a template to copy blindly. Section 6 separates reusable decisions from
Redis-specific ones.

## 1. Purpose

A key-value extension makes a driver from Eclipse JNoSQL usable in Quarkus so that an application can inject:

- `org.eclipse.jnosql.communication.keyvalue.BucketManager` (low-level `put`/`get`/`delete` by key),
- `jakarta.nosql.Template` (Jakarta NoSQL template, used in `redis/integration-tests/.../JNoSQLResource.java`),
- Jakarta Data repositories that extend `jakarta.data.repository.BasicRepository`.

The extension supplies the Quarkus-side wiring only: CDI producers, a configuration object that creates and owns the
driver factory, build steps, metadata, codestart and tests.

## 2. Architecture

| Layer | Owner | Redis example |
|---|---|---|
| Wire protocol and client (Jedis, pooling) | Driver | `org.eclipse.jnosql.databases:jnosql-redis` (`RedisConfiguration`, `RedisBucketManagerFactory`, `RedisBucketManager`) |
| Key-value contracts | Eclipse JNoSQL communication | `KeyValueConfiguration`, `BucketManagerFactory`, `BucketManager` |
| Mapping, template, repository | Eclipse JNoSQL mapping | `jnosql-mapping-key-value` (pulled through `quarkus-jnosql-core`) |
| CDI producers for Quarkus, bean registration, exclusions, native image | **This extension** | `redis/runtime`, `redis/deployment` |
| Configuration source | MicroProfile Config via `MicroProfileSettings` | `core/runtime/.../MicroProfileSettings.java` |

Flow at runtime:

1. The application sets `jnosql.keyvalue.database` (key `MappingConfigurations.KEY_VALUE_DATABASE`) and driver properties
   (`jnosql.redis.*`) in `application.properties`.
2. `QuarkusRedisBucketManagerProducer` (extends `AbstractBucketManagerProducer`) reads the database name through
   `MicroProfileSettings`, asks `QuarkusRedisKeyValueConfiguration` for the factory, and returns a `RedisBucketManager`.
3. The mapping layer's template and repositories resolve the manager by type from CDI.

Responsibility rule: query semantics, serialization of values and repository behavior come from Eclipse JNoSQL and the
driver. The extension must not re-implement them.

## 3. Reference implementation

### 3.1 Maven modules

```text
redis/
├── pom.xml                         # quarkus-jnosql-redis-parent: modules runtime, deployment; `it` profile -> integration-tests
├── runtime/pom.xml                 # quarkus-jnosql-redis
├── deployment/pom.xml              # quarkus-jnosql-redis-deployment
└── integration-tests/pom.xml       # quarkus-jnosql-redis-integration-tests (skipITs=true unless native profile)
```

Key dependencies in [`redis/runtime/pom.xml`](../redis/runtime/pom.xml):

- `io.quarkiverse.jnosql:quarkus-jnosql-core`, excluding `org.eclipse.jnosql.mapping:jnosql-mapping-document` (the
  unneeded model; key-value mapping stays).
- `io.quarkus:quarkus-jsonb` (and `quarkus-jsonb-deployment` in
  [`redis/deployment/pom.xml`](../redis/deployment/pom.xml)): Redis declares it; the repository does not document why, so check whether your driver needs it.
- `org.eclipse.jnosql.databases:jnosql-redis`, excluding `jnosql-mapping-reflection`.
- `org.eclipse.jnosql.lite:mapping-lite-processor`.

Redis has no Quarkus client extension, so the driver creates its own Jedis pool.

### 3.2 Source map

| Concern | File |
|---|---|
| Configuration + factory ownership | [`redis/runtime/src/main/java/org/eclipse/jnosql/databases/redis/communication/QuarkusRedisKeyValueConfiguration.java`](../redis/runtime/src/main/java/org/eclipse/jnosql/databases/redis/communication/QuarkusRedisKeyValueConfiguration.java) |
| `BucketManager` producer | [`.../QuarkusRedisBucketManagerProducer.java`](../redis/runtime/src/main/java/org/eclipse/jnosql/databases/redis/communication/QuarkusRedisBucketManagerProducer.java) |
| `BucketManagerFactory` producer | [`.../QuarkusRedisBucketManagerFactoryProducer.java`](../redis/runtime/src/main/java/org/eclipse/jnosql/databases/redis/communication/QuarkusRedisBucketManagerFactoryProducer.java) |
| Shared base producer | [`core/runtime/.../AbstractBucketManagerProducer.java`](../core/runtime/src/main/java/io/quarkiverse/jnosql/core/runtime/AbstractBucketManagerProducer.java) |
| Build steps | [`redis/deployment/src/main/java/io/quarkiverse/jnosql/keyvalue/redis/deployment/Processor.java`](../redis/deployment/src/main/java/io/quarkiverse/jnosql/keyvalue/redis/deployment/Processor.java) |
| Extension metadata | [`redis/runtime/src/main/resources/META-INF/quarkus-extension.yaml`](../redis/runtime/src/main/resources/META-INF/quarkus-extension.yaml) |
| Codestart | [`redis/runtime/src/main/codestarts/quarkus/quarkus-jnosql-redis-codestart/`](../redis/runtime/src/main/codestarts/quarkus/quarkus-jnosql-redis-codestart) |
| Integration app | [`redis/integration-tests/src/main/java/io/quarkiverse/jnosql/redis/it/`](../redis/integration-tests/src/main/java/io/quarkiverse/jnosql/redis/it) (`JNoSQLResource`, `Person`, `PersonRecord`, `PersonBasicRepository`) |
| Integration tests | [`redis/integration-tests/src/test/java/io/quarkiverse/jnosql/keyvalue/redis/it/`](../redis/integration-tests/src/test/java/io/quarkiverse/jnosql/keyvalue/redis/it) (`JNoSQLResourceTest`, `JNoSQLResourceIT`, `PersonBasicRepositoryTest`, `RedisTestResource`) and `.../keyvalue/redis/codestart/CodestartTest.java` |

### 3.3 CDI registration and build items

`Processor` in the deployment module (feature name `jnosql-redis`) produces:

- `AdditionalBeanBuildItem.unremovableOf(...)` for `QuarkusRedisKeyValueConfiguration`,
  `QuarkusRedisBucketManagerProducer` and `QuarkusRedisBucketManagerFactoryProducer`.
- `ExcludedTypeBuildItem` for the Eclipse suppliers that would create a competing bean:
  `org.eclipse.jnosql.mapping.keyvalue.configuration.BucketManagerSupplier`,
  `org.eclipse.jnosql.mapping.keyvalue.configuration.BucketManagerFactorySupplier`,
  `org.eclipse.jnosql.databases.redis.mapping.BucketManagerFactorySupplier` and
  `org.eclipse.jnosql.databases.redis.mapping.CollectionSupplier`.
- `RuntimeInitializedClassBuildItem` for `org.eclipse.jnosql.databases.redis.communication.RedisConfiguration` (Redis-specific native-image need).

### 3.4 Configuration

Values come from MicroProfile Config. The integration app uses
[`application.properties`](../redis/integration-tests/src/main/resources/application.properties):

```properties
jnosql.keyvalue.database=test
jnosql.redis.max.total=8
```

Host and port are injected into the test by `RedisTestResource` using `RedisConfigurations.HOST.get()` and
`RedisConfigurations.PORT.get()` from the driver, so use the driver's enum instead of string literals. The extension
declares the prefixes `jnosql.`, `jnosql.keyvalue.` and `jnosql.redis.` under `metadata.config` in `quarkus-extension.yaml`.

## 4. Implementation workflow

Steps match section 3 of [`common.md`](common.md). **R** = required, **O** = optional or conditional.

1. **Prerequisites (R).** Confirm that `jnosql-<db>` exists for the managed `jnosql.version`, and find: the
   `KeyValueConfiguration` implementation, the `BucketManagerFactory`/`BucketManager` types, the `*Configurations` enum,
   and any CDI suppliers it ships (the Redis ones are listed in 3.3). Check whether Quarkus already ships a client extension.
2. **Maven modules (R).** Create `<db>/pom.xml`, `runtime`, `deployment`, `integration-tests`; register the module in the root `pom.xml`.
3. **Dependencies (R).** Mirror `redis/runtime/pom.xml`: core (exclude unused mapping modules), the driver (exclude
   `jnosql-mapping-reflection`), `mapping-lite-processor`. Add `quarkus-jsonb` only if your driver needs it.
   (O) add the Quarkus client extension if it exists.
4. **Runtime integration (R).**
   - A `@Singleton` configuration implementing `KeyValueConfiguration` whose `apply(Settings)` returns the driver's factory.
     Cache the factory and close it with `@PreDestroy` so the application uses one connection pool.
   - A `@Singleton` producer extending `AbstractBucketManagerProducer<Manager, Factory, Configuration>`.
   - (O) A producer for the concrete `BucketManagerFactory` type when applications or driver beans inject the factory.
   - Place both in the driver's package (`org.eclipse.jnosql.databases.<db>.communication`) if package-private driver types are needed.
5. **Deployment integration (R).** `Processor` with `FeatureBuildItem`, `AdditionalBeanBuildItem`s and `ExcludedTypeBuildItem`s. (O) `RuntimeInitializedClassBuildItem` or other native items.
6. **CDI registration (R).** Producers need `@Produces @Alternative @Priority(1) @Default`. Key-value producers carry no
   `@Database` qualifier in the reference; `BucketManager` is selected by type.
7. **Configuration (R).** Use `jnosql.keyvalue.database` and the driver's properties. Do not create new properties. Update `quarkus-extension.yaml`.
8. **Native image (O).** Add registrations only for failures seen in a native build. Redis needed one runtime-initialized class.
9. **Integration tests (R).** See section 7.
10. **Build validation (R).** Commands in section 7 and the checklist in section 8.
11. **Codestart (O, but expected for key-value).** Redis has a codestart descriptor and templates under its own `src/main/codestarts`; the repository also has a shared `codestarts/key-value` directory. Follow the neighbor's pom copy executions.

## 5. Reference code

Configuration with a shared, owned factory (adapted from `QuarkusRedisKeyValueConfiguration`):

```java
@Singleton
public class QuarkusRedisKeyValueConfiguration implements KeyValueConfiguration {

    private final RedisConfiguration configuration = new RedisConfiguration();
    private RedisBucketManagerFactory factory;

    @Override
    public synchronized RedisBucketManagerFactory apply(Settings settings) {
        if (factory == null) {
            factory = configuration.apply(settings);
        }
        return factory;
    }

    @PreDestroy
    synchronized void close() {
        if (factory != null) {
            factory.close();
            factory = null;
        }
    }
}
```

Producer (`QuarkusRedisBucketManagerProducer`):

```java
@Singleton
public class QuarkusRedisBucketManagerProducer extends
        AbstractBucketManagerProducer<RedisBucketManager, RedisBucketManagerFactory, QuarkusRedisKeyValueConfiguration> {

    @Override
    @Produces
    @Alternative
    @Priority(1)
    @Default
    public RedisBucketManager get() {
        return get(MappingConfigurations.KEY_VALUE_DATABASE);
    }
}
```

Deployment (`Processor`, abbreviated):

```java
@BuildStep
void build(BuildProducer<AdditionalBeanBuildItem> additionalBeanProducer) {
    additionalBeanProducer.produce(AdditionalBeanBuildItem.unremovableOf(QuarkusRedisKeyValueConfiguration.class));
    additionalBeanProducer.produce(AdditionalBeanBuildItem.unremovableOf(QuarkusRedisBucketManagerProducer.class));
    additionalBeanProducer.produce(AdditionalBeanBuildItem.unremovableOf(QuarkusRedisBucketManagerFactoryProducer.class));
}

@BuildStep
void markRuntimeInitializedClasses(BuildProducer<RuntimeInitializedClassBuildItem> runtimeInitializedClassesProducer) {
    runtimeInitializedClassesProducer.produce(new RuntimeInitializedClassBuildItem(RedisConfiguration.class.getName()));
}
```

Repository (`PersonBasicRepository`):

```java
@Repository
public interface PersonBasicRepository extends BasicRepository<Person, String> {
}
```

## 6. Design decisions and trade-offs

| Decision | Why | Reusable? |
|---|---|---|
| Configuration class caches the factory and closes it on `@PreDestroy` | `AbstractBucketManagerProducer.get(...)` calls `configuration.apply(settings)` on every call; without caching each manager would create a new Jedis pool. `JNoSQLResource#fromBucketConcurrently` verifies concurrent use of one pool | Yes, for any driver whose factory owns connections |
| Separate `BucketManagerFactory` producer | Makes the same cached factory injectable | Yes, when the driver exposes a factory type that applications use |
| Producers use `@Alternative @Priority(1) @Default` | Wins over the Eclipse supplier beans during resolution | Yes |
| Excluding the four Eclipse suppliers | the Eclipse suppliers create their own manager/factory, which would compete with the extension's beans and its owned factory | Pattern yes; class names are driver-specific |
| `RuntimeInitializedClassBuildItem` for `RedisConfiguration` | Native-image initialization requirement of the Redis driver | No, Redis-specific |
| `quarkus-jsonb` dependency | Declared by the Redis extension; rationale not documented in the repo | No, driver-specific |
| No Quarkus client extension dependency | Redis has none in this repository | Depends on the database |

## 7. Testing

Follow the structure of `redis/integration-tests`:

- **App module** (`src/main`): `Person` (`@Entity`, `@Id`, `@Column`), `PersonRecord`, `PersonBasicRepository`, and a
  `JNoSQLResource` exposing `Template` paths (`/template/using-pojo`, `/template/using-record`) and `BucketManager`
  paths (`/keyvalue/using-pojo`, `/keyvalue/using-record`, `/keyvalue/concurrent`).
- **`JNoSQLResourceTest`**: `@QuarkusTest`, `@TestHTTPEndpoint(JNoSQLResource.class)`, `@QuarkusTestResource(RedisTestResource.class)`.
  `JNoSQLResourceIT` extends it with `@QuarkusIntegrationTest`.
- **`RedisTestResource`**: a `QuarkusTestResourceLifecycleManager` starting a Testcontainers `GenericContainer` and
  returning driver properties (`RedisConfigurations.HOST`, `RedisConfigurations.PORT`).
- **`PersonBasicRepositoryTest`**: covers `save`, `saveAll`, `findById`, update by saving the same id, `deleteById`,
  `delete`, `deleteAll(entities)` and the negative cases below.
- **`CodestartTest`**: snapshot and build of the generated project.

### Query limitations

Key-value stores address data by key. The reference tests assert that the following are **not** supported:

- `repository.findAll()` throws `UnsupportedOperationException`.
- Paginated `findAll(PageRequest, Order)` throws `UnsupportedOperationException`.
- Existence is checked through `findById(...)`.

Redis tests do not demonstrate any derived query (`findByName`) or Jakarta Query. Do not document or test them for a new key-value
extension unless the driver implements them and an integration test proves them. Keep the repository interface to
`BasicRepository` operations that your database really supports, and add a negative test for each unsupported one.

### Commands

From [`common.md`](common.md#57-commands):

```bash
mvn -pl redis -am clean install                    # replace redis with <db>; formatting applied
mvn -B clean install -Dno-format                   # whole reactor, as CI
mvn -pl redis/integration-tests verify -DskipITs=false
mvn -B install -Dnative -Dquarkus.native.container-build -Dnative.surefire.skip
```

Docker is required for Testcontainers.

## 8. Definition of Done

Complete the [common checklist](common.md#7-definition-of-done) plus:

- [ ] Driver confirmed for `jnosql.version`; configuration, factory and manager types identified.
- [ ] Configuration implements `KeyValueConfiguration`, caches the factory, closes it on shutdown.
- [ ] Producer extends `AbstractBucketManagerProducer` and uses `MappingConfigurations.KEY_VALUE_DATABASE`.
- [ ] Factory producer added only if the factory type is injected.
- [ ] Eclipse suppliers (`BucketManagerSupplier`, `BucketManagerFactorySupplier`, driver suppliers) excluded; names verified in the driver jar.
- [ ] Unnecessary mapping modules and `jnosql-mapping-reflection` excluded in the runtime POM.
- [ ] Integration tests cover `Template`, `BucketManager` and `BasicRepository` against a real database.
- [ ] Unsupported operations have explicit negative tests and are not claimed in documentation.
- [ ] A concurrency test shows one shared connection pool.
- [ ] Codestart and `CodestartTest` present, matching the neighbors.
- [ ] Native build run, or limitation noted.
- [ ] Commits follow Conventional Commits, one logical change per commit.
