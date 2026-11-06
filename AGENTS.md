# AGENTS.md

Entry point for AI coding agents working in this repository. Humans can use it as a short contributor checklist.
Scope: implementing and maintaining database extensions. The Antora website (`docs/`) and end-user documentation are out of scope unless a task says otherwise.

## Project overview

Quarkus JNoSQL (`io.quarkiverse.jnosql`) integrates [Eclipse JNoSQL](https://github.com/eclipse/jnosql) with Quarkus.
It is a Maven multi-module build (root `pom.xml`, version `3.8.1-SNAPSHOT`, Quarkus and JNoSQL versions defined by the
`quarkus.version` and `jnosql.version` properties).

### Relationship with Eclipse JNoSQL

- **Eclipse JNoSQL** owns the database drivers (`org.eclipse.jnosql.databases:jnosql-*`), the communication layer, the
  mapping layer, templates, and Jakarta Data repository support. This repository must reuse them.
- **Quarkus JNoSQL** owns only the Quarkus integration: CDI/Arc wiring, MicroProfile Config bridging, build-time
  processing, native-image registration, extension metadata, codestarts, and integration tests.

### Specifications

- **Jakarta NoSQL**: entities (`@Entity`, `@Embeddable`) and templates.
- **Jakarta Data**: repositories (`@Repository`) backed by JNoSQL.
- **Jakarta Query**: query language used by Jakarta Data/NoSQL where the underlying database supports it. Support is
  database-dependent; never assume every database executes every query.

### The six supported NoSQL models

| Model | Example extensions |
|---|---|
| Key-value | `redis`, `valkey`, `hazelcast`, `infinispan`, `memcached`, `dynamodb` |
| Document | `mongodb`, `couchdb`, `solr`, `elasticsearch`, `arangodb`, `oracle-nosql` |
| Column | `cassandra`, `scylladb` |
| Graph | `neo4j`, `tinkerpop` |
| Time series | `influxdb`, `iotdb`, `questdb` |
| Vector | `qdrant`, `milvus`, `weaviate` |

The list of modules is authoritative in the root `pom.xml` (`<modules>`).

### Driver versus Quarkus extension

- A **driver** (Eclipse JNoSQL module) speaks the database protocol and implements the communication/mapping contracts.
  It does not belong in this repository.
- A **Quarkus extension** (`<db>/runtime` + `<db>/deployment`) adapts that driver to Quarkus. It supplies CDI producers
  and configuration objects at runtime, and build steps at deployment time.
- If a capability is missing in the driver, it is an Eclipse JNoSQL change, not an extension change.

## Agent workflow

When asked to implement or modify a database integration, follow these steps in order. They are mandatory.

1. **Identify the database model** (key-value, document, column, graph, time series, vector).
2. **Read [`development-guides/common.md`](development-guides/common.md).**
3. **Read [`development-guides/testing.md`](development-guides/testing.md).** Read it BEFORE generating any test; it defines
   naming, structure, assertions and the test infrastructure to use.
4. **Read the model-specific guide** (see [Documentation routing](#documentation-routing)).
5. **Inspect the selected reference implementation** and existing extensions of the same model (modules, `Processor`,
   producers, `quarkus-extension.yaml`, integration tests, codestart). Also confirm the Eclipse JNoSQL driver artifact and
   its configuration classes exist for the `jnosql.version` in the root `pom.xml`, and check the Quarkus client extension
   (for example `quarkus-mongodb-client`) if one exists.
6. **Define the implementation plan** (modules, dependencies, beans to register or exclude, native-image needs, tests,
   codestart) before writing code. Share it when the task is ambiguous.
7. **Implement** following established repository patterns.
8. **Create tests** following `development-guides/testing.md`, running against a real database (container or Quarkus Dev
   Services).
9. **Execute the applicable validation commands** (see `common.md`, section Testing).
10. **Report test results and remaining limitations** (unsupported operations, untested paths, native-image status), and
    commit using Conventional Commits.

## Engineering rules

- Reuse existing Eclipse JNoSQL drivers when available.
- Do not reimplement features already provided by Eclipse JNoSQL.
- Respect Quarkus build-time processing: discovery, bean registration, and exclusions belong in `deployment`; runtime
  classes must stay free of build-time APIs.
- Follow established Maven module conventions (`<db>/pom.xml`, `runtime`, `deployment`, `integration-tests`, artifact
  names `quarkus-jnosql-<db>`, `-deployment`, `-integration-tests`).
- Do not introduce unnecessary abstractions; shared code goes to `core` or `vector` only when two or more extensions need it.
- Preserve compatibility and existing behavior of other extensions.
- Never invent APIs, configuration properties, or unsupported database capabilities. Verify against source code and the
  driver's classes.
- Use integration tests to validate database behavior; mocks do not prove driver integration.
- Follow the repository's formatting and licensing rules: formatting is applied by the build (inherited from
  `quarkiverse-parent`) unless `-Dno-format` is passed, and the project is licensed under the `LICENSE` file at the
  repository root. Match the header conventions of neighboring files; existing Java sources carry no per-file header.

## Git rules

- Use a dedicated branch for each implementation task (for example `feat/<db>-extension`, `docs/<topic>`).
- Use [Conventional Commits](https://www.conventionalcommits.org/) (`feat(redis): ...`, `fix(core): ...`,
  `docs: ...`, `test(qdrant): ...`), following the history of the repository.
- Prefer one logical change per commit.
- For documentation tasks, commit exactly one file per commit.
- Do not automatically push changes or open pull requests.

## Documentation routing

| Guide | Status |
|---|---|
| [`development-guides/common.md`](development-guides/common.md) | Available: shared guide for all extensions |
| [`development-guides/testing.md`](development-guides/testing.md) | Available: shared testing guidelines; read before writing tests |
| [`development-guides/key-value.md`](development-guides/key-value.md) | Available: reference Redis |
| [`development-guides/document.md`](development-guides/document.md) | Available: reference MongoDB |
| [`development-guides/column.md`](development-guides/column.md) | Available: reference Cassandra |
| [`development-guides/graph.md`](development-guides/graph.md) | Available: reference Apache TinkerPop |
| [`development-guides/time-series.md`](development-guides/time-series.md) | Available: reference InfluxDB |
| [`development-guides/vector.md`](development-guides/vector.md) | Available: reference Qdrant |
