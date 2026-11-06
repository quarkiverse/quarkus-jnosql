# Graph Extension Development Guide

Blueprint for adding a Quarkus JNoSQL extension for a **graph** database. Reference implementation:
**Apache TinkerPop** (`tinkerpop/`). Read [`common.md`](common.md) first for the generic module layout, build items,
CDI/Arc, testing commands and the generic Definition of Done. This guide covers only what is specific to the graph model.

The TinkerPop extension is deliberately different from the other references: **it does not create or configure the
database client.** The application owns the `org.apache.tinkerpop.gremlin.structure.Graph`; the extension consumes it.

## 1. Purpose

The graph extension lets an application inject, on top of a `Graph` it provides:

- `TinkerpopTemplate` (`org.eclipse.jnosql.databases.tinkerpop.mapping.TinkerpopTemplate`), a graph-aware template with edge and traversal operations,
- repositories extending `TinkerPopRepository` (`org.eclipse.jnosql.databases.tinkerpop.mapping.TinkerPopRepository`) or Jakarta Data `BasicRepository`,
- the `GraphDatabaseManager` bean of the communication layer.

The extension's job is to make these Eclipse JNoSQL components work under Quarkus Arc and to bind them to the
application's `Graph`. It is not to support specific TinkerPop providers.

## 2. Architecture

| Concern | Owner |
|---|---|
| Choosing a TinkerPop provider, its dependency, its configuration, `Graph` creation and `close()` | **Application** (CDI producer for `Graph`) |
| `TinkerpopGraphDatabaseManager`, `TinkerpopTemplate`, `TinkerPopRepository`, `TinkerpopRepositoryBean`, `GraphTemplateProducer`, `DefaultTinkerpopTemplate` | Eclipse JNoSQL (`jnosql-tinkerpop`) |
| Binding the above to the application `Graph`, replacing driver suppliers, repository registration, native proxies, fail-fast validation | **This extension** |

### Why the boundary is here

TinkerPop is an API with many provider implementations, each with its own artifact, configuration keys and
constraints. The driver's own `GraphSupplier` builds a `Graph` from MicroProfile settings; the extension excludes it so
there is exactly one `Graph` bean, the application's. This avoids the extension being responsible for configuring every
provider, and it keeps lifecycle (including `close()`) in one place.

Repositories: Eclipse JNoSQL registers `TinkerPopRepository` beans via a portable CDI extension, which Arc does not
execute (see the Javadoc in `Processor.registerRepositories`). The extension registers them as Arc synthetic beans instead.

## 3. Reference implementation

### 3.1 Maven modules

```text
tinkerpop/
├── pom.xml                        # quarkus-jnosql-tinkerpop-parent
├── runtime/pom.xml                # quarkus-jnosql-tinkerpop
├── deployment/pom.xml             # quarkus-jnosql-tinkerpop-deployment
└── integration-tests/pom.xml      # quarkus-jnosql-tinkerpop-integration-tests
```

Runtime dependencies ([`tinkerpop/runtime/pom.xml`](../tinkerpop/runtime/pom.xml)): `quarkus-jnosql-core` (excluding
`jnosql-mapping-key-value`), `jnosql-tinkerpop`, `gremlin-core`, `mapping-lite-processor`. There is **no provider
dependency** in the runtime module. The integration-test module adds `arangodb-tinkerpop-provider` and `testcontainers`.

### 3.2 Source map

| Concern | File |
|---|---|
| `GraphDatabaseManager` producer | [`tinkerpop/runtime/src/main/java/io/quarkiverse/jnosql/tinkerpop/runtime/GraphDatabaseManagerProducer.java`](../tinkerpop/runtime/src/main/java/io/quarkiverse/jnosql/tinkerpop/runtime/GraphDatabaseManagerProducer.java) |
| Synthetic-bean creator for repositories | [`.../runtime/TinkerpopRepositoryCreator.java`](../tinkerpop/runtime/src/main/java/io/quarkiverse/jnosql/tinkerpop/runtime/TinkerpopRepositoryCreator.java) |
| Build steps | [`tinkerpop/deployment/src/main/java/io/quarkiverse/jnosql/tinkerpop/deployment/Processor.java`](../tinkerpop/deployment/src/main/java/io/quarkiverse/jnosql/tinkerpop/deployment/Processor.java) |
| Missing-`Graph` test | [`tinkerpop/deployment/src/test/java/io/quarkiverse/jnosql/tinkerpop/deployment/MissingGraphTest.java`](../tinkerpop/deployment/src/test/java/io/quarkiverse/jnosql/tinkerpop/deployment/MissingGraphTest.java) |
| Application `Graph` producer (test app) | [`tinkerpop/integration-tests/src/main/java/io/quarkiverse/jnosql/tinkerpop/it/ArangoGraphProducer.java`](../tinkerpop/integration-tests/src/main/java/io/quarkiverse/jnosql/tinkerpop/it/ArangoGraphProducer.java) |
| Entity and repositories | [`.../it/Person.java`](../tinkerpop/integration-tests/src/main/java/io/quarkiverse/jnosql/tinkerpop/it/Person.java), [`PersonRepository.java`](../tinkerpop/integration-tests/src/main/java/io/quarkiverse/jnosql/tinkerpop/it/PersonRepository.java), [`PersonDataRepository.java`](../tinkerpop/integration-tests/src/main/java/io/quarkiverse/jnosql/tinkerpop/it/PersonDataRepository.java) |
| Tests | [`.../it/TinkerpopIntegrationTest.java`](../tinkerpop/integration-tests/src/test/java/io/quarkiverse/jnosql/tinkerpop/it/TinkerpopIntegrationTest.java), [`JakartaDataRepositoryTest.java`](../tinkerpop/integration-tests/src/test/java/io/quarkiverse/jnosql/tinkerpop/it/JakartaDataRepositoryTest.java), [`ArangoDBTestResource.java`](../tinkerpop/integration-tests/src/test/java/io/quarkiverse/jnosql/tinkerpop/it/ArangoDBTestResource.java) |
| Metadata | [`tinkerpop/runtime/src/main/resources/META-INF/quarkus-extension.yaml`](../tinkerpop/runtime/src/main/resources/META-INF/quarkus-extension.yaml) |

There is no codestart for this extension, and no `*IT` native-image test class in the integration module.

### 3.3 Build steps (`Processor`, feature `jnosql-tinkerpop`)

| Build step | Build items | Purpose |
|---|---|---|
| `feature` | `FeatureBuildItem` | Feature name |
| `excludeDriverGraphSupplier` | `ExcludedTypeBuildItem` for `org.eclipse.jnosql.databases.tinkerpop.mapping.configuration.GraphSupplier` | Prevent a second, settings-built `Graph` |
| `useApplicationGraphForRepositories` | `ExcludedTypeBuildItem` for `org.eclipse.jnosql.mapping.graph.configuration.GraphManagerSupplier`; `AdditionalBeanBuildItem.unremovableOf(GraphDatabaseManagerProducer.class)` | Replace the manager supplier with one built from the application `Graph` |
| `keepMappingBeansAndRequireGraph` | consumes `BeanDiscoveryFinishedBuildItem`; produces `UnremovableBeanBuildItem.beanClassNames(DefaultTinkerpopTemplate, GraphTemplateProducer)` | Fail the build with `MISSING_GRAPH_MESSAGE` when no default-qualified `Graph` bean exists; keep template beans |
| `registerRepositories` | consumes `CombinedIndexBuildItem`; produces `SyntheticBeanBuildItem` (`Singleton`, creator `TinkerpopRepositoryCreator`), `NativeImageProxyDefinitionBuildItem`, `ReflectiveClassBuildItem` per repository | Register each sub-interface of `TinkerPopRepository` |

### 3.4 Configuration

The extension defines **no configuration properties**. `quarkus-extension.yaml` lists only the prefix `jnosql.`.
Provider settings belong to the application; the test app reads its own `app.arangodb.hosts` via `@ConfigProperty`
(set in `application.properties` and overridden by `ArangoDBTestResource`). That property is an application property of the
test app, not part of the extension.

## 4. Implementation workflow

Use this when extending the graph model (for example, supporting repositories for a different graph API). In most cases
a new TinkerPop *provider* needs **no new extension**: add the provider dependency and a `Graph` producer in the application.

1. **Prerequisites (R).** Check what `jnosql-<db>` provides (communication manager, template, repository bean, suppliers). Decide who owns the client.
2. **Maven modules (R).** As in `common.md`.
3. **Dependencies (R).** Driver, graph API (`gremlin-core`), `mapping-lite-processor`, core with `jnosql-mapping-key-value` excluded. Provider dependencies go to the *application*, not the runtime module.
4. **Runtime (R).** Producer for `GraphDatabaseManager` taking `Graph` as a parameter and calling `TinkerpopGraphDatabaseManager.of(graph)`; `BeanCreator` for repositories.
5. **Deployment (R).** The four build steps in 3.3.
6. **CDI (R).** `@ApplicationScoped` producer without `@Disposes` (the `Graph` producer closes the `Graph`). Synthetic beans for repositories if the driver depends on a portable extension.
7. **Configuration (O).** None for the reference; add only what the extension truly owns.
8. **Native image (R for repositories).** `NativeImageProxyDefinitionBuildItem` and `ReflectiveClassBuildItem` for each repository interface (done in `registerRepositories`).
9. **Integration tests (R).** Section 7.
10. **Build validation (R).** Section 7 and the Definition of Done.
11. **Codestart (O).** None exists for TinkerPop.

## 5. Reference code

Application-owned graph (`ArangoGraphProducer`, test app):

```java
@Produces
@ApplicationScoped
public Graph graph() {
    Configuration configuration = new BaseConfiguration();
    configuration.addProperty("gremlin.graph", ArangoDBGraph.class.getName());
    // ... provider-specific properties ...
    return GraphFactory.open(configuration);
}

public void close(@Disposes Graph graph) throws Exception {
    graph.close();
}
```

Extension manager producer (`GraphDatabaseManagerProducer`):

```java
@ApplicationScoped
public class GraphDatabaseManagerProducer {

    @Produces
    @ApplicationScoped
    public GraphDatabaseManager graphDatabaseManager(Graph graph) {
        return TinkerpopGraphDatabaseManager.of(graph);
    }
}
```

Excluding the driver supplier (`Processor`):

```java
excludedTypes.produce(
        new ExcludedTypeBuildItem("org.eclipse.jnosql.databases.tinkerpop.mapping.configuration.GraphSupplier"));
```

Repository registration (`Processor.registerRepositories`, abbreviated):

```java
for (ClassInfo repository : index.getIndex().getAllKnownSubinterfaces(REPOSITORY)) {
    beans.produce(SyntheticBeanBuildItem.configure(repository.name())
            .scope(Singleton.class)
            .unremovable()
            .creator(TinkerpopRepositoryCreator.class)
            .param(TinkerpopRepositoryCreator.REPOSITORY_TYPE, repository)
            .done());
    proxies.produce(new NativeImageProxyDefinitionBuildItem(name));
    reflection.produce(ReflectiveClassBuildItem.builder(name).methods().build());
}
```

Repository creation (`TinkerpopRepositoryCreator`):

```java
Class<?> repositoryType = (Class<?>) context.getParams().get(REPOSITORY_TYPE);
return new TinkerpopRepositoryBean(repositoryType).create(null);
```

Fail-fast check (`Processor.keepMappingBeansAndRequireGraph`, abbreviated):

```java
boolean present = discovery.beanStream().withBeanType(GRAPH).collect().stream()
        .anyMatch(BeanInfo::hasDefaultQualifiers);
if (!present) {
    throw new IllegalStateException(MISSING_GRAPH_MESSAGE);
}
```

## 6. Design decisions and trade-offs

| Decision | Why | Reusable? |
|---|---|---|
| Application owns `Graph` | Providers differ in dependencies and configuration; the extension cannot support them all generically, and the `Graph` lifecycle stays with whoever created it | Yes, for any "API with many providers" |
| Exclude `GraphSupplier` and `GraphManagerSupplier` | Avoid two `Graph` beans / a manager built from settings and provider SPI | Pattern yes; class names are driver-specific |
| Build-time check for a default `Graph` bean | Arc's generic unsatisfied-dependency message is not actionable; `MissingGraphTest` locks the message | Yes |
| Synthetic beans + `TinkerpopRepositoryCreator` | Driver registers repositories through a portable CDI extension that Arc does not run | Yes for any driver using a portable extension; creator is driver-specific |
| Manager producer has no `@Disposes` | The `Graph` producer closes the `Graph`; closing twice would be a bug (`shouldNotCloseApplicationGraph` guards this) | Yes |
| No `@Database` qualifier / `AbstractDatabaseManagerProducer` | The reference does not use them; `GraphDatabaseManager` and the `Graph` are injected by type. Other graph drivers may need the qualified producer pattern from `common.md` | Database-specific |

## 7. Testing

- **Deployment test**: `MissingGraphTest` (`QuarkusUnitTest.assertException`) asserts the build fails with `Processor.MISSING_GRAPH_MESSAGE` when no `Graph` is produced.
- **Integration tests** against a real server (`ArangoDBTestResource`, Testcontainers `GenericContainer` with image `arangodb:3.12.13`, publishing `app.arangodb.hosts`):
  - `TinkerpopIntegrationTest`: injection of the application `Graph` and `TinkerpopTemplate`; `template.find`; `template.edge`, `template.edges(entity, Direction, label)`; `template.traversalVertex(id).out(label).<Person> result()`; repository `findByName`; application `Graph` not closed by the extension.
  - `JakartaDataRepositoryTest`: `BasicRepository` derived query `findByName` and `findById` for an unknown id.
- **Provider limitation recorded in the tests**: the `arangodb-tinkerpop-provider` rejects the reserved `_id` property that the JNoSQL driver writes on insert, so tests create vertices through `graph.addVertex(...)` and do not exercise insert or id-based reads through the repository/template. Only ArangoDB is used in the integration tests; the repository has **no** tests for other TinkerPop providers and none should be claimed.
- Supported operations proven by tests: those listed above. Anything else (for example saving entities, edge deletion) is not covered by this extension's tests.

```bash
mvn -pl tinkerpop -am clean install
mvn -pl tinkerpop/integration-tests verify
mvn -B clean install -Dno-format
```

Docker is required for `ArangoDBTestResource`. See [`common.md`](common.md#5-testing) for native commands.

## 8. Definition of Done

Complete the [common checklist](common.md#7-definition-of-done) plus:

- [ ] Ownership of client/`Graph` creation is stated; the extension does not hard-code a provider.
- [ ] Driver suppliers that would create a second client or manager are excluded; class names verified in the driver jar.
- [ ] Missing application bean fails at build time with an actionable message, covered by a `QuarkusUnitTest`.
- [ ] Repository interfaces are registered as Arc beans and have native proxy/reflection items.
- [ ] The extension does not close resources it does not own.
- [ ] Integration tests inject the application `Graph`, the template and repositories against a real server.
- [ ] Provider-specific limitations discovered are documented in test Javadoc; no claim of support for untested providers.
- [ ] `mvn -pl tinkerpop -am clean install` and the full `mvn -B clean install -Dno-format` pass; native build run or limitation noted.
- [ ] Conventional Commits, one logical change per commit.
