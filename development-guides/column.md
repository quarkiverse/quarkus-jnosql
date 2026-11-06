# Column Extension Development Guide

Blueprint for adding a Quarkus JNoSQL extension for a **column** (wide-column) database. Reference implementation:
**Cassandra** (`cassandra/`). Read [`common.md`](common.md) first for the generic module layout, build items, CDI/Arc,
native image, testing commands and the generic Definition of Done. This guide covers only what is specific to the column model.

Cassandra is an architectural example, not a template to copy blindly. Other column databases (for example `scylladb/`
in this repository) are separate extensions and must be verified on their own; section 6 separates reusable decisions
from Cassandra-specific ones.

## 1. Purpose

A column extension lets an application inject, for a column database:

- `jakarta.nosql.Template` qualified with `@Database(DatabaseType.COLUMN)`,
- Jakarta Data repositories (`NoSQLRepository`),
- the communication-layer manager (here `CassandraColumnManager`).

The extension supplies Quarkus wiring: it reuses the session managed by the Cassandra Quarkus client, adapts it to the
JNoSQL factory contract, produces the manager, excludes the Eclipse supplier, and ships metadata, codestart and tests.

## 2. Architecture

| Layer | Owner | Cassandra example |
|---|---|---|
| Client and session lifecycle | Quarkus client extension | `com.datastax.oss.quarkus:cassandra-quarkus-client`, bean `QuarkusCqlSession` |
| Column communication contracts | Eclipse JNoSQL (`org.eclipse.jnosql.communication.semistructured`) | `DatabaseConfiguration`, `DatabaseManagerFactory` |
| Driver implementation | `org.eclipse.jnosql.databases:jnosql-cassandra` | `CassandraColumnManager`, `DefaultCassandraColumnManager` |
| Entity mapping, `Template`, repositories | Eclipse JNoSQL mapping (semistructured), metadata from `mapping-lite-processor` | `Person`, `People` in the integration app |
| Quarkus wiring | **This extension** | `cassandra/runtime`, `cassandra/deployment` |

Column and document models share the `semistructured` communication/mapping API, which is why the producer base class
(`AbstractDatabaseManagerProducer`) and the shared codestart templates (`codestarts/semistructured`) are the same. The
differences are the qualifier (`DatabaseType.COLUMN`), the property (`jnosql.column.database`, `MappingConfigurations.COLUMN_DATABASE`),
and the Eclipse supplier to exclude (`ColumnManagerSupplier`).

In Cassandra the "database" name passed to the factory is the **keyspace**: `QuarkusCassandraColumnManagerFactory.apply(String keyspace)`.
Other column databases may map the name differently; read the driver.

## 3. Reference implementation

### 3.1 Maven modules

```text
cassandra/
├── pom.xml                        # quarkus-jnosql-cassandra-parent
├── runtime/pom.xml                # quarkus-jnosql-cassandra
├── deployment/pom.xml             # quarkus-jnosql-cassandra-deployment
└── integration-tests/pom.xml      # quarkus-jnosql-cassandra-integration-tests
```

Dependencies in [`cassandra/runtime/pom.xml`](../cassandra/runtime/pom.xml):

- `com.datastax.oss.quarkus:cassandra-quarkus-client` (and `cassandra-quarkus-client-deployment` in
  [`cassandra/deployment/pom.xml`](../cassandra/deployment/pom.xml)); its version is managed by `cassandra-quarkus-bom.version` in the root `pom.xml`.
- `io.quarkiverse.jnosql:quarkus-jnosql-core`, excluding `jnosql-mapping-key-value`.
- `org.eclipse.jnosql.databases:jnosql-cassandra`, excluding `jnosql-mapping-reflection`.
- `org.eclipse.jnosql.lite:mapping-lite-processor`.
- Integration tests add `com.datastax.oss.quarkus:cassandra-quarkus-test-framework` (test scope).

### 3.2 Source map

| Concern | File |
|---|---|
| Configuration (injects `QuarkusCqlSession`) | [`cassandra/runtime/src/main/java/org/eclipse/jnosql/databases/cassandra/communication/QuarkusCassandraConfiguration.java`](../cassandra/runtime/src/main/java/org/eclipse/jnosql/databases/cassandra/communication/QuarkusCassandraConfiguration.java) |
| Factory adapter | [`.../QuarkusCassandraColumnManagerFactory.java`](../cassandra/runtime/src/main/java/org/eclipse/jnosql/databases/cassandra/communication/QuarkusCassandraColumnManagerFactory.java) |
| Manager producer | [`.../QuarkusCassandraDatabaseManagerProducer.java`](../cassandra/runtime/src/main/java/org/eclipse/jnosql/databases/cassandra/communication/QuarkusCassandraDatabaseManagerProducer.java) |
| Build steps | [`cassandra/deployment/src/main/java/io/quarkiverse/jnosql/column/cassandra/deployment/Processor.java`](../cassandra/deployment/src/main/java/io/quarkiverse/jnosql/column/cassandra/deployment/Processor.java) |
| Extension metadata | [`cassandra/runtime/src/main/resources/META-INF/quarkus-extension.yaml`](../cassandra/runtime/src/main/resources/META-INF/quarkus-extension.yaml) |
| Codestart | [`cassandra/runtime/src/main/codestarts/quarkus/quarkus-jnosql-cassandra-codestart/codestart.yml`](../cassandra/runtime/src/main/codestarts/quarkus/quarkus-jnosql-cassandra-codestart/codestart.yml) |
| Integration app | [`cassandra/integration-tests/src/main/java/io/quarkiverse/jnosql/cassandra/it/`](../cassandra/integration-tests/src/main/java/io/quarkiverse/jnosql/cassandra/it) (`Person`, `PersonRecord`, `People`, `PeopleRecord`, `JNoSQLResource`) |
| Integration tests | [`cassandra/integration-tests/src/test/java/io/quarkiverse/jnosql/cassandra/it/`](../cassandra/integration-tests/src/test/java/io/quarkiverse/jnosql/cassandra/it) (`JNoSQLResourceTest`, `JNoSQLResourceIT`), [`.../cassandra/codestart/CodestartTest.java`](../cassandra/integration-tests/src/test/java/io/quarkiverse/jnosql/cassandra/codestart/CodestartTest.java) |
| Schema | [`cassandra/integration-tests/src/test/resources/init_script.cql`](../cassandra/integration-tests/src/test/resources/init_script.cql) |

### 3.3 CDI registration and build items

`Processor` (feature `jnosql-cassandra`) produces:

- `AdditionalBeanBuildItem.unremovableOf` for `QuarkusCassandraDatabaseManagerProducer` and `QuarkusCassandraConfiguration`.
- `ExcludedTypeBuildItem("org.eclipse.jnosql.mapping.column.configuration.ColumnManagerSupplier")`.

No native-image build items are produced in the Cassandra `Processor`.

### 3.4 Configuration

[`application.properties`](../cassandra/integration-tests/src/main/resources/application.properties) of the integration app:

```properties
jnosql.column.database=test
quarkus.http.test-port=8083
```

The keyspace name (`test`) comes from `jnosql.column.database`. Cassandra connection settings are owned by
`cassandra-quarkus-client`; the extension declares the prefixes `jnosql.` and `jnosql.column.` in `quarkus-extension.yaml`.

## 4. Implementation workflow

Steps match section 3 of [`common.md`](common.md). **R** = required, **O** = optional/conditional.

1. **Prerequisites (R).** Confirm `jnosql-<db>`; identify its column manager type and factory, how the database name
   maps to the driver (keyspace, namespace, ...), and the supplier to exclude. Check for a Quarkus client extension that
   exposes a session or client bean.
2. **Maven modules (R).** As in `common.md`.
3. **Dependencies (R).** As 3.1. (O) Client extension and its test framework when they exist.
4. **Runtime integration (R).**
   - `@Singleton` `Quarkus<Db>Configuration implements DatabaseConfiguration`, `@Inject`ing the client bean and returning
     the factory.
   - A `DatabaseManagerFactory` implementation when the driver factory cannot take the Quarkus-managed client (Cassandra's
     `QuarkusCassandraColumnManagerFactory` builds `DefaultCassandraColumnManager` from the injected session). If the
     driver's own factory already accepts the client (MongoDB's does, see [`document.md`](document.md)), skip this class.
   - Producer extending `AbstractDatabaseManagerProducer<Manager, Factory, Configuration>`, qualified `@Database(DatabaseType.COLUMN)`.
5. **Deployment integration (R).** `Processor` with `FeatureBuildItem`, unremovable beans, and the excluded `ColumnManagerSupplier`.
6. **CDI registration (R).** `@Produces @Priority(1) @Alternative @Default @Database(DatabaseType.COLUMN)`.
7. **Configuration (R).** `jnosql.column.database`; connection settings stay with the client extension.
8. **Native image (O).** Add only after a native failure; Cassandra's processor has none.
9. **Integration tests (R).** Section 7.
10. **Build validation (R).** Section 7 and the checklist in section 8.
11. **Codestart (O, expected for column).** `database-type: "column"`; it adds `cassandra-quarkus-client` and the test framework dependencies and the `@QuarkusTestResource(com.datastax.oss.quarkus.test.CassandraTestResource.class)` annotation through `additional-quarkus-test-annotations`.

## 5. Reference code

Configuration (`QuarkusCassandraConfiguration`):

```java
@Singleton
public class QuarkusCassandraConfiguration implements DatabaseConfiguration {

    @Inject
    protected QuarkusCqlSession quarkusCqlSession;

    @Override
    public DatabaseManagerFactory apply(Settings settings) throws NullPointerException {
        return new QuarkusCassandraColumnManagerFactory(quarkusCqlSession);
    }
}
```

Factory adapter (`QuarkusCassandraColumnManagerFactory`, abbreviated):

```java
public class QuarkusCassandraColumnManagerFactory implements DatabaseManagerFactory {

    private final CqlSession cqlSession;

    @Override
    public void close() {
        // the session is owned by Quarkus
    }

    @Override
    public CassandraColumnManager apply(String keyspace) {
        return new DefaultCassandraColumnManager(cqlSession, keyspace);
    }
}
```

(The source `close()` is empty; the comment above is an explanation added here.)

Producer (`QuarkusCassandraDatabaseManagerProducer`):

```java
@Singleton
public class QuarkusCassandraDatabaseManagerProducer extends
        AbstractDatabaseManagerProducer<CassandraColumnManager, QuarkusCassandraColumnManagerFactory, QuarkusCassandraConfiguration> {

    @Produces
    @Priority(1)
    @Alternative
    @Default
    @Database(DatabaseType.COLUMN)
    @Override
    public CassandraColumnManager get() {
        return get(MappingConfigurations.COLUMN_DATABASE);
    }
}
```

Exclusion (`Processor`):

```java
excludedTypeProducer.produce(
        new ExcludedTypeBuildItem("org.eclipse.jnosql.mapping.column.configuration.ColumnManagerSupplier"));
```

## 6. Design decisions and trade-offs

| Decision | Why | Reusable? |
|---|---|---|
| Inject `QuarkusCqlSession` rather than building a session from `Settings` | The Cassandra Quarkus client configures, pools and closes the session and supports native; a second session would duplicate connections and configuration | Yes, if a Quarkus client extension exists |
| Custom `DatabaseManagerFactory` with empty `close()` | The driver factory creates its own session; this one wraps the Quarkus-owned session and must not close it | Pattern yes; the driver classes (`DefaultCassandraColumnManager`) are Cassandra-specific |
| Producer type parameter is the custom factory class | `AbstractDatabaseManagerProducer` casts the factory to `F`; it must match the configuration's return type | Yes |
| `database` name = keyspace | `apply(String keyspace)` of the Cassandra manager | Cassandra-specific |
| No native build items | Native support is provided by `cassandra-quarkus-client` | Cassandra-specific |

### Modeling considerations

- The integration schema in `init_script.cql` creates the keyspace `test` and tables `person` and `personrecord` with a
  text primary key named `"_id"`, `name text`, and `phones list<text>`. The integration entities therefore rely on a
  pre-created schema; table and column names must match what the mapping layer generates for the entities.
  Do not assume the extension creates tables or keyspaces.
- Wide-column stores impose query constraints (partition/clustering keys). Which Jakarta Data and Jakarta Query operations
  work is decided by the driver and the database, not by this extension. The reference tests prove only `insert`, `save`,
  `findById` (template and repository) for classes and records.
- Do not apply Cassandra decisions (keyspace semantics, session ownership, schema naming) to another column database
  without reading its driver and testing it.

## 7. Testing

Follow `cassandra/integration-tests` and [`common.md`](common.md#5-testing):

- **App module**: `Person`, `PersonRecord`, `People`, `PeopleRecord`, `JNoSQLResource` using `@Database(DatabaseType.COLUMN)` injection.
- **`JNoSQLResourceTest`**: `@QuarkusTest`, `@QuarkusTestResource(CassandraTestResource.class)` (from the Cassandra Quarkus test
  framework), `@TestHTTPEndpoint(JNoSQLResource.class)`, parameterized over `/using-jakarta-nosql`,
  `/using-jakarta-nosql-record`, `/using-jakarta-data`, `/using-jakarta-data-record`. `JNoSQLResourceIT` runs it with `@QuarkusIntegrationTest`.
- **Schema**: `src/test/resources/init_script.cql`. The repository does not reference the file by name in Java or POM
  code; confirm in `cassandra-quarkus-test-framework` how a script is picked up before relying on it in a new module.
- **`CodestartTest`** with snapshots.
- Add tests for each query feature you claim to support; they are database-dependent.

```bash
mvn -pl cassandra -am clean install                      # replace cassandra with <db>
mvn -pl cassandra/integration-tests verify -DskipITs=false
mvn -B clean install -Dno-format                         # whole reactor, as CI
```

Docker is required for the test resource.

## 8. Definition of Done

Complete the [common checklist](common.md#7-definition-of-done) plus:

- [ ] Client ownership decided; Quarkus-managed client is reused and never closed by the extension.
- [ ] Meaning of the JNoSQL "database" name for this database verified in the driver and documented in code comments or tests.
- [ ] Producer qualified `@Database(DatabaseType.COLUMN)`, with `@Alternative`, `@Priority(1)`, `@Default`; uses `MappingConfigurations.COLUMN_DATABASE`.
- [ ] `ColumnManagerSupplier` (and any driver supplier) excluded; names verified.
- [ ] Schema required by the integration entities is created by the test setup.
- [ ] Integration tests cover template and repository paths for a class and a record against a real database.
- [ ] Supported query features are tested; unsupported ones are not claimed.
- [ ] Codestart (`database-type: "column"`) and `CodestartTest` snapshots present.
- [ ] Native build run or limitation noted.
- [ ] Conventional Commits, one logical change per commit.
