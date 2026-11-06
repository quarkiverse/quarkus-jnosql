# Time-Series Extension Development Guide

Blueprint for adding a Quarkus JNoSQL extension for a **time-series** database. Reference implementation:
**InfluxDB** (`influxdb/`). Read [`common.md`](common.md) first for the generic module layout, build items, CDI/Arc,
testing commands and the generic Definition of Done. This guide covers only what is specific to the time-series model.

InfluxDB is an architectural example. Other time-series extensions in this repository (`iotdb/`, `questdb/`) are
separate and must be verified on their own.

## 1. Purpose

A time-series extension lets an application inject, for a time-series database:

- `org.eclipse.jnosql.mapping.timeseries.TimeSeriesTemplate`, qualified with `@Database(DatabaseType.TIME_SERIES)`,
- Jakarta Data repositories (`NoSQLRepository`) over timestamp-identified entities,
- the communication manager (`InfluxDBTimeSeriesManager`).

The extension provides Quarkus wiring: it produces the manager from MicroProfile configuration, excludes the Eclipse
supplier, closes the factory at shutdown, and registers the native-image requirements of the driver's transitive gRPC and Arrow stack.

## 2. Architecture

| Layer | Owner | InfluxDB example |
|---|---|---|
| Server communication, point writing, query execution | Driver `org.eclipse.jnosql.databases:jnosql-influxdb` | `InfluxDBTimeSeriesConfiguration`, `InfluxDBTimeSeriesManagerFactory`, `InfluxDBTimeSeriesManager`, `InfluxDBEntityConverter` |
| Configuration keys | Driver | `InfluxDBTimeSeriesConfigurations.URL` = `jnosql.influxdb.url`, `.TOKEN` = `jnosql.influxdb.token` |
| Template and temporal query API | Eclipse JNoSQL `jnosql-mapping-timeseries` | `TimeSeriesTemplate`, `TimeSeriesQuery` |
| Producer, build steps, native registration, tests | **This extension** | `influxdb/runtime`, `influxdb/deployment` |

Unlike key-value or Cassandra, no Quarkus client extension exists in this reference: the driver creates its own client
from `Settings`, so the producer uses the driver's configuration class directly (`createFactory()` from
`AbstractDatabaseManagerProducer`, shown in section 5).

### Temporal entity identity

The driver maps the entity `@Id` to the point **timestamp**. In `InfluxDBEntityConverter` the identifier must be an
`Instant`, `LocalDateTime`, `OffsetDateTime` or `ZonedDateTime` (otherwise `IllegalArgumentException`); on read the `time`
column is mapped back to the `_id` field. The integration entity therefore declares `@Id private Instant timestamp;`.
Consequences: identity is a point in time, not a surrogate key; two writes with the same timestamp refer to the same identity,
and precision matters (tests truncate to milliseconds).

## 3. Reference implementation

### 3.1 Maven modules

```text
influxdb/
├── pom.xml                        # quarkus-jnosql-influxdb-parent
├── runtime/pom.xml                # quarkus-jnosql-influxdb
├── deployment/pom.xml             # quarkus-jnosql-influxdb-deployment
└── integration-tests/pom.xml      # quarkus-jnosql-influxdb-integration-tests
```

Runtime dependencies ([`influxdb/runtime/pom.xml`](../influxdb/runtime/pom.xml)): `quarkus-jnosql-core` (excluding
`jnosql-mapping-key-value`), `jnosql-influxdb` (excluding `jnosql-mapping-reflection`), `mapping-lite-processor`.
The deployment module depends on `quarkus-jnosql-core-deployment` and the runtime module. No codestart is shipped.

### 3.2 Source map

| Concern | File |
|---|---|
| Producer | [`influxdb/runtime/src/main/java/org/eclipse/jnosql/databases/influxdb/communication/QuarkusInfluxDBDatabaseManagerProducer.java`](../influxdb/runtime/src/main/java/org/eclipse/jnosql/databases/influxdb/communication/QuarkusInfluxDBDatabaseManagerProducer.java) |
| Build steps | [`influxdb/deployment/src/main/java/io/quarkiverse/jnosql/timeseries/influxdb/deployment/Processor.java`](../influxdb/deployment/src/main/java/io/quarkiverse/jnosql/timeseries/influxdb/deployment/Processor.java) |
| Metadata | [`influxdb/runtime/src/main/resources/META-INF/quarkus-extension.yaml`](../influxdb/runtime/src/main/resources/META-INF/quarkus-extension.yaml) |
| Entity, repository, REST resource | [`influxdb/integration-tests/src/main/java/io/quarkiverse/jnosql/influxdb/it/`](../influxdb/integration-tests/src/main/java/io/quarkiverse/jnosql/influxdb/it) (`Metric`, `Metrics`, `JNoSQLResource`) |
| Tests | [`influxdb/integration-tests/src/test/java/io/quarkiverse/jnosql/influxdb/it/`](../influxdb/integration-tests/src/test/java/io/quarkiverse/jnosql/influxdb/it) (`MetricsRepositoryTest`, `JNoSQLResourceTest`, `JNoSQLResourceIT`, `InfluxDBTestResource`) |
| Test app config | [`application.properties`](../influxdb/integration-tests/src/main/resources/application.properties), [`beans.xml`](../influxdb/integration-tests/src/main/resources/META-INF/beans.xml) |

### 3.3 Build steps (`Processor`, feature `jnosql-influxdb`)

- `build`: `AdditionalBeanBuildItem.unremovableOf` for `InfluxDBTimeSeriesConfiguration` and `QuarkusInfluxDBDatabaseManagerProducer`.
- `buildExcludedType`: `ExcludedTypeBuildItem("org.eclipse.jnosql.mapping.timeseries.configuration.TimeSeriesManagerSupplier")`.
- `configureNativeImage`: `ServiceProviderRegister.registerService(services, LoadBalancerProvider.class, NameResolverProvider.class)` and `RuntimeInitializedClassBuildItem` for
  `io.grpc.netty.Utils$ByteBufAllocatorPreferHeapHolder`, `io.grpc.netty.Utils$ByteBufAllocatorPreferDirectHolder`,
  `org.apache.arrow.memory.netty.NettyAllocationManager`.

### 3.4 Configuration

Test app: `jnosql.timeseries.database=metrics` (database name; `MappingConfigurations.TIME_SERIES_DATABASE`). The test
resource sets `jnosql.influxdb.url` and `jnosql.influxdb.token` through `InfluxDBTimeSeriesConfigurations.URL.get()`
and `.TOKEN.get()`. `quarkus-extension.yaml` declares the prefixes `jnosql.`, `jnosql.timeseries.` and `jnosql.influxdb.`.

## 4. Implementation workflow

Steps match [`common.md`](common.md) section 3. **R** = required, **O** = optional.

1. **Prerequisites (R).** Confirm `jnosql-<db>` exists. Read its manager: which operations throw `UnsupportedOperationException`
   (for InfluxDB: update, delete, per-point TTL), which `@Id` types are accepted, and its configuration enum.
2. **Maven modules (R).** As in `common.md`.
3. **Dependencies (R).** Driver (excluding `jnosql-mapping-reflection`), core (excluding `jnosql-mapping-key-value`), `mapping-lite-processor`.
4. **Runtime (R).** One `@Singleton` producer extending `AbstractDatabaseManagerProducer<Manager, Factory, Configuration>`.
5. **Deployment (R).** `Processor` with feature, unremovable beans, excluded `TimeSeriesManagerSupplier`.
6. **CDI (R).** `@Produces @Priority(1) @Alternative @Default @ApplicationScoped @Database(DatabaseType.TIME_SERIES)`; close the factory in `@PreDestroy`.
7. **Configuration (R).** `jnosql.timeseries.database` plus the driver's own keys. No new properties are defined by the extension.
8. **Native image (R if the driver pulls gRPC/Arrow; O otherwise).** Copy only the registrations the driver stack needs; verify with a native build.
9. **Integration tests (R).** Section 7.
10. **Build validation (R).** Section 7 and the checklist in section 8.
11. **Codestart (O).** None for InfluxDB, IoTDB or QuestDB; add only if the conventions for your extension require one.

## 5. Reference code

```java
@Singleton
public class QuarkusInfluxDBDatabaseManagerProducer extends
        AbstractDatabaseManagerProducer<InfluxDBTimeSeriesManager, InfluxDBTimeSeriesManagerFactory, InfluxDBTimeSeriesConfiguration> {

    private InfluxDBTimeSeriesManagerFactory factory;

    @Produces
    @Priority(1)
    @Alternative
    @Default
    @ApplicationScoped
    @Database(DatabaseType.TIME_SERIES)
    public synchronized InfluxDBTimeSeriesManager get() {
        factory = createFactory();
        return get(factory, MappingConfigurations.TIME_SERIES_DATABASE);
    }

    @PreDestroy
    void close() {
        if (factory != null) {
            factory.close();
        }
    }
}
```

Native-image registration (`Processor.configureNativeImage`, abbreviated):

```java
ServiceProviderRegister.registerService(services, LoadBalancerProvider.class, NameResolverProvider.class);
runtimeInitializedClasses.produce(
        new RuntimeInitializedClassBuildItem("io.grpc.netty.Utils$ByteBufAllocatorPreferHeapHolder"));
```

Temporal entity and repository (test app):

```java
@Entity
public class Metric {
    @Id
    private Instant timestamp;
    @Column
    private String source;
    @Column
    private double value;
    // constructors, getters, setters
}

@Repository
public interface Metrics extends NoSQLRepository<Metric, Instant> {
    List<Metric> findBySource(String source);
}
```

### Temporal operations (verified against `jnosql-mapping-timeseries` 1.2.0 sources)

`TimeSeriesTemplate.select(Class<T>)` returns `TimeSeriesQuery.From`, which extends `TimeSeriesQuery.Interval` (itself a
`Chronological`). Signatures in `org.eclipse.jnosql.mapping.timeseries.TimeSeriesQuery`:

| Method | Declared in | Signature |
|---|---|---|
| `latest` | `Chronological` | `<T> List<T> latest(int count)` |
| `earliest` | `Chronological` | `<T> List<T> earliest(int count)` |
| `since` | `Interval` | `Where since(java.time.temporal.Temporal start)` |
| `until` | `Interval` | `Where until(java.time.temporal.Temporal end)` |
| `last` | `Interval` | `Where last(Duration duration)` |

`Where` extends `Interval`, so the calls can be chained after `where(...)`/`and(...)` conditions. **The repository's
integration tests do not call any of these methods**; whether a given database honors them is determined by the driver,
and a new extension must test them before claiming support.

## 6. Design decisions and trade-offs

| Decision | Why | Reusable? |
|---|---|---|
| `@ApplicationScoped` manager, `@Singleton` producer holding the factory | The factory owns the network client; closing it in `@PreDestroy` releases it at shutdown | Yes |
| `synchronized get()` | The producer stores the factory in a field; serializing creation prevents two factories | Yes |
| Use the driver's `Configuration` via `AbstractDatabaseManagerProducer.createFactory()` | No Quarkus client extension for InfluxDB 3 exists in this module, so settings are read from MicroProfile config | Specific to databases without a Quarkus client |
| `@Id` is an `Instant` | Driver requires a temporal identifier mapped to the point timestamp | Time-series-wide pattern; accepted types are driver-specific |
| gRPC `ServiceProviderBuildItem` and runtime-initialized Netty/Arrow classes | The driver's transitive gRPC/Arrow stack needs them for native | Reusable for gRPC-based drivers; the exact class list is InfluxDB-specific |
| Excluding `TimeSeriesManagerSupplier` | It would build a second manager from settings through the Eclipse JNoSQL supplier | Yes (supplier name is model-specific) |
| Accept driver limitations (no update/delete) | They come from the database semantics; the extension must not emulate them | Verify per database |

## 7. Testing

- **Test resource**: `InfluxDBTestResource` (Testcontainers `GenericContainer`, image `influxdb:3.11.0-core`, port 8181,
  `--without-auth`, in-memory object store). It waits on `/health`, creates database `metrics` with
  `influxdb3 create database`, and returns `jnosql.influxdb.url` and `jnosql.influxdb.token`.
- **`JNoSQLResourceTest`** / **`JNoSQLResourceIT`**: REST endpoints `/jnosql/using-timeseries-template` (`TimeSeriesTemplate.insert` and `find`) and `/jnosql/using-jakarta-data` (`Metrics.insert` and `findById`).
- **`MetricsRepositoryTest`**: insert and `findById`, derived query `findBySource`, and **negative scenarios** asserting `UnsupportedOperationException` for `update` and `deleteById`.
- The test app enables `beans.xml` with `bean-discovery-mode="all"`.
- Not covered by tests: `latest`, `earliest`, `since`, `until`, `last`, TTL, count.

```bash
mvn -pl influxdb -am clean install
mvn -pl influxdb/integration-tests verify -DskipITs=false
mvn -B clean install -Dno-format
```

Docker is required. See [`common.md`](common.md#5-testing) for native commands.

## 8. Definition of Done

Complete the [common checklist](common.md#7-definition-of-done) plus:

- [ ] Driver behavior for update, delete, TTL and query operations is read from the driver and expressed in tests (including negative cases).
- [ ] Identity type accepted by the driver is documented in the test entity; precision issues are handled in tests.
- [ ] Producer is `@Database(DatabaseType.TIME_SERIES)`, uses `MappingConfigurations.TIME_SERIES_DATABASE`, and closes the factory.
- [ ] `TimeSeriesManagerSupplier` excluded.
- [ ] Native registrations match the driver's transitive stack and are verified by a native build or the limitation is noted.
- [ ] Template and repository paths run against a real server; setup creates the database the test uses.
- [ ] Temporal operations (`latest`, `earliest`, `since`, `until`, `last`) are tested before being claimed as supported.
- [ ] Conventional Commits, one logical change per commit.
