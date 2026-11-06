# Testing Guidelines for Database Extensions

Shared conventions for tests in Quarkus JNoSQL extensions. They adapt the
[Eclipse JNoSQL testing guideline](https://github.com/eclipse-jnosql/jnosql/blob/main/TESTING-GUIDELINE.adoc) to this
repository. [`common.md`](common.md#5-testing) explains *where* tests live and which commands run them; this document
defines *how* tests are written. The model guides define *what* each model must test (section 8).

## 1. Purpose

These rules apply to every database extension, regardless of its NoSQL model. Consistent tests are easier to review,
and show which behaviors an extension really supports.

| Kind | What it proves | Needs Quarkus? | Needs a database? | Repository examples |
|---|---|---|---|---|
| Unit test | Logic of a single class | No | No | Plain JUnit tests; used sparingly because most code here is wiring |
| Quarkus integration test (extension/deployment test) | Bootstrap, bean discovery, build-time behavior, shutdown | Yes (`QuarkusUnitTest`) | No (fake provider or none) | [`VectorExtensionTest`](../vector/deployment/src/test/java/io/quarkiverse/jnosql/vector/deployment/VectorExtensionTest.java), [`MissingGraphTest`](../tinkerpop/deployment/src/test/java/io/quarkiverse/jnosql/tinkerpop/deployment/MissingGraphTest.java) |
| Database integration test | Real behavior of the driver and database through the extension | Yes (`@QuarkusTest`) | Yes | `<db>/integration-tests`, e.g. [`QdrantVectorTemplateTest`](../qdrant/integration-tests/src/test/java/io/quarkiverse/jnosql/qdrant/it/QdrantVectorTemplateTest.java) |

## 2. Testing tools

| Tool | Use | Status in this repository |
|---|---|---|
| JUnit 5 | Framework for all tests | Used everywhere |
| Quarkus test support | `@QuarkusTest`, `@QuarkusIntegrationTest`, `@QuarkusTestResource`, `QuarkusTestResourceLifecycleManager`, `@TestHTTPEndpoint` (integration tests, `quarkus-junit5`); `QuarkusUnitTest` (deployment modules, `quarkus-junit5-internal`); `QuarkusCodestartTest` for codestarts | Used |
| REST Assured | Assertions on HTTP responses of the test application (`given().when().get(...).then()...`) | Used in `<db>/integration-tests` (`io.rest-assured:rest-assured`) |
| Testcontainers | Database provisioning in test resources | Used; version in `testcontainers.version` (root `pom.xml`) |
| AssertJ | Preferred assertion library for Java objects | Version property `assertj.version` exists in the root `pom.xml` and is used by codestart templates; most extension integration-test modules do **not** declare it yet |
| Mockito | Only where mocking is justified (section 5) | `quarkus-junit-mockito` is declared in a few integration-test POMs (couchdb, oracle-nosql, arangodb, solr, elasticsearch); no test sources use it |

Rules:

- Inspect the module's `pom.xml` before writing a test. If a recommended library is absent, adding it to test scope with the version managed by the root `pom.xml` (`assertj.version`) is acceptable; do not add libraries that are not already used or managed in the repository.
- REST Assured keeps its own `.then().statusCode(...).body(...)` response assertions. AssertJ does **not** replace them. Use AssertJ for the Java objects (entities, lists, exceptions) obtained from injected beans.
- Existing tests contain JUnit assertions (`assertEquals`, `assertTrue`, `assertThrows`). When you write or touch a test class, use the conventions below; do not rewrite unrelated tests.

## 3. Naming conventions

| Element | Convention | Example |
|---|---|---|
| Test class | `<Subject>Test` | `MetricsRepositoryTest` |
| Native/packaged variant | `<Subject>IT` extending the test (existing repository convention for `@QuarkusIntegrationTest`) | `JNoSQLResourceIT` |
| Test method | `should<ExpectedBehavior>`; for negative scenarios the expected result is mandatory | `shouldInsert`, `shouldFindById`, `shouldReturnEmptyWhenEntityDoesNotExist`, `shouldRejectInvalidIdentifier` |
| Nested class | `WhenThe<Operation>` | `WhenTheInsertion`, `WhenTheRemoval`, `WhenTheSearch` |
| `@DisplayName` | Required on every nested class and test method; a behavioral sentence, not the identifier repeated | `"When inserting a metric"`, `"Should return the stored metric"` |

Prefer domain names (`WhenTheMetricIsInserted`) over implementation names (`WhenTheSaveMethodIsCalled`). Avoid `testInsert`,
`test1`, `InsertTests`, `HappyPathTests`, and names that repeat the nested class (`shouldInsertMetricWhenInsertingMetric`).
Because the nested class provides the operation, method names stay short.

## 4. Test organization

- Group tests by operation with `@Nested`, one nested class per operation.
- Use Given / When / Then inside each test; comments may be omitted when the separation is obvious.
- One behavioral scenario per test. Several assertions describing the same resulting state belong together.
- Keep setup minimal; share fixtures only through small helpers. Remove duplicated assertions.
- Deterministic: unique identifiers per test (`UUID.randomUUID()`, as in `TinkerpopIntegrationTest.uniqueName()`), no ordering dependence, no sleeping for data.
- When refactoring a test class, list the scenarios it covers first and preserve them; add tests only for behavior the production code really has.

```java
@QuarkusTest
@QuarkusTestResource(InfluxDBTestResource.class)
class MetricsRepositoryTest {

    @Inject
    @Database(DatabaseType.TIME_SERIES)
    Metrics repository;

    @Nested
    @DisplayName("When inserting a metric")
    class WhenTheInsertion {

        @Test
        @DisplayName("Should return the stored metric by its timestamp")
        void shouldFindById() {
            // Given
            Instant id = Instant.now().truncatedTo(ChronoUnit.MILLIS);

            // When
            repository.insert(new Metric(id, "repo-find", 1.5));

            // Then
            Metric found = repository.findById(id).orElseThrow();
            assertSoftly(softly -> {
                softly.assertThat(found.getTimestamp()).as("timestamp is preserved").isEqualTo(id);
                softly.assertThat(found.getSource()).as("source is preserved").isEqualTo("repo-find");
                softly.assertThat(found.getValue()).as("value is preserved").isEqualTo(1.5);
            });
        }
    }

    @Nested
    @DisplayName("When removing a metric")
    class WhenTheRemoval {

        @Test
        @DisplayName("Should reject the removal because the provider does not support it")
        void shouldRejectDeleteById() {
            Instant id = Instant.now().truncatedTo(ChronoUnit.MILLIS).plusSeconds(180);
            repository.insert(new Metric(id, "repo-delete", 1.0));

            assertThatThrownBy(() -> repository.deleteById(id))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }
}
```

The scenarios come from the existing `MetricsRepositoryTest`, restructured here to the convention. `assertSoftly` and
`assertThatThrownBy` come from AssertJ (`org.assertj.core.api.SoftAssertions.assertSoftly`,
`org.assertj.core.api.Assertions.assertThatThrownBy`); this requires AssertJ on the module's test classpath (section 2).

## 5. Assertions

- Prefer AssertJ over JUnit assertions: `assertThat(actual).isEqualTo(expected)`, `isTrue()`, `isNull()`, `isNotNull()`, `containsExactly(...)`, `isEmpty()`.
- Use `SoftAssertions.assertSoftly` when checking several attributes of one object or state, and add `as("...")` to each assertion so the failing one is identifiable.
- Exceptions: use the specific AssertJ helper when it exists (for example `assertThatIllegalArgumentException()`), otherwise `assertThatThrownBy(...).isInstanceOf(...).hasMessageContaining(...)`. Do not use `assertThrows`.
- Assert observable behavior (returned data, persisted state, thrown exception, closed resource) rather than internal calls.
- **Mocking** is justified only when the interaction is the contract and no database is involved, for example verifying that a collaborator is closed or not called after a validation failure. It is *not* a substitute for the database: driver integration, mapping and queries must be proven against a real database (see [`AGENTS.md`](../AGENTS.md) engineering rules).
- **Real database integration** is the default for anything touching entity mapping, templates, repositories, or configuration of the driver.

## 6. Quarkus integration testing

| Goal | How, based on existing code |
|---|---|
| Extension bootstrap and CDI discovery | `QuarkusUnitTest` with `.setArchiveProducer(...)` in `<db>/deployment/src/test`; `VectorExtensionTest` checks the manager and factory beans resolve with a fake provider |
| Build-time failures | `QuarkusUnitTest.assertException(...)` as in `MissingGraphTest` |
| Injection and CDI integration in an application | Inject with `@Inject @Database(DatabaseType.X)` in `@QuarkusTest` classes (`QdrantVectorTemplateTest`) or in the REST resource of the test app (`JNoSQLResource`) |
| Database configuration | Properties in `<db>/integration-tests/src/main/resources/application.properties` (for example `jnosql.column.database=test`) plus values returned by the `QuarkusTestResourceLifecycleManager` |
| Connectivity | Any successful operation through the injected template or repository against the provisioned database; Dev Services (MongoDB) or a test resource (`QdrantTestResource`, `InfluxDBTestResource`, `ArangoDBTestResource`) |
| Jakarta NoSQL templates | Inject `Template` or the model template (`TimeSeriesTemplate`, `VectorTemplate`, `TinkerpopTemplate`) |
| Jakarta Data repositories | Inject the `@Repository` interface; the repository must resolve via CDI (`QdrantArticleRepositoryTest.shouldResolveGeneratedRepository`) |
| HTTP surface | `@TestHTTPEndpoint(JNoSQLResource.class)` with REST Assured, usually parameterized with `@ParameterizedTest` and `@CsvSource` |
| Runtime behavior on the packaged artifact | `*IT` class extending the test and annotated with `@QuarkusIntegrationTest` |
| Native compilation | Run the CI command from [`common.md`](common.md#57-commands); `skipITs` is true by default and the `native-image` profile (`-Dnative`) enables the `*IT` classes. Only where the driver supports native image |
| Codestarts | `CodestartTest` (`QuarkusCodestartTest`) with snapshots, see [`common.md`](common.md#54-codestart-tests) |

Only these annotations and infrastructure exist in the repository; do not assume other test resources or Maven profiles.
Reuse the extension's `*TestResource` and use `@QuarkusTestResource` to attach it.

## 7. Database integration testing

Validate a new extension against a real database:

- **Startup and shutdown**: Testcontainers inside a `QuarkusTestResourceLifecycleManager` (`start()` returns the driver properties, `stop()` stops the container), or Quarkus Dev Services when the client extension supplies them. Pin image versions as the existing resources do.
- **Provisioning**: create anything the entities need (database, keyspace, schema) in the test resource or init script, as `InfluxDBTestResource` and `cassandra/.../init_script.cql` do.
- **Create, retrieve, update, remove**: cover each operation the database supports, through both the template and a repository when both exist.
- **Invalid identifiers and missing records**: assert the real result (empty `Optional`, empty list, or exception) the driver produces; do not guess it.
- **Unsupported operations**: assert the exception the driver throws, as `MetricsRepositoryTest` does for `update` and `deleteById`, and `PersonBasicRepositoryTest` in Redis for `findAll()`. Do not hide them and do not claim them as supported.
- **Failures**: test missing or blank configuration where the extension validates it (the vector producer throws `MappingException` for a blank database name) and missing prerequisites at build time (`MissingGraphTest`). Simulating a database outage is not done in the existing tests; add it only if the extension adds failure handling.
- **Cleanup and isolation**: use unique identifiers per test and never rely on data from another test; the containers are shared by the test class, not reset between methods.

Do not assume all models expose the same operations; the model guides list what the reference implementation proves.

## 8. Model-specific testing considerations

Short pointers; implementation details are in the guides.

| Model | Additional concerns | Guide |
|---|---|---|
| Key-value | Key identity, value persistence, only the repository methods actually supported (no derived queries; `findAll()` is unsupported in the Redis reference) | [`key-value.md`](key-value.md) |
| Document | Entity mapping (classes and records), query behavior | [`document.md`](document.md) |
| Column | Identity, mapping, schema pre-creation, which query operations the database accepts | [`column.md`](column.md) |
| Graph | Application-provided `Graph`, vertices, edges, repository behavior; do not claim providers that are not tested | [`graph.md`](graph.md) |
| Time series | Temporal identity and precision, ordering, time boundaries (`latest`, `earliest`, `since`, `until`, `last`) | [`time-series.md`](time-series.md) |
| Vector | Dimensions, similarity search, supported vector types (dense, sparse, multi) | [`vector.md`](vector.md) |

## 9. Coverage and mutation testing

- Cover the main scenario and the corner cases: positive scenarios, negative scenarios, nulls and empty results, boundaries (empty collection, minimum/maximum values, time boundaries, dimensions), duplicates, invalid identifiers, and exception behavior.
- Add a regression test for every fixed bug.
- Prioritize meaningful behavioral coverage over a percentage; there is no 100% requirement. Add tests only for behavior the production code and the database really provide.
- **Mutation testing (PIT)**: the Eclipse JNoSQL guideline uses `mvn clean test-compile -P pitest`. This repository's `pom.xml` files define **no** `pitest` profile or plugin, so there is no supported PIT command here. If PIT is added to the build, document the command from that configuration; do not use the Eclipse JNoSQL command until then.

## 10. Definition of Done

- [ ] Test classes are named `<Subject>Test` (`*IT` only for `@QuarkusIntegrationTest` subclasses).
- [ ] Test methods start with `should`; negative scenarios state the expected result.
- [ ] Operations are grouped in `@Nested` classes named `WhenThe<Operation>`.
- [ ] Nested classes and test methods have meaningful `@DisplayName` annotations.
- [ ] Assertions use AssertJ (REST Assured keeps its HTTP assertions), `assertSoftly` with `as()` for multi-attribute checks, and AssertJ exception helpers.
- [ ] Positive scenarios are covered for each supported operation.
- [ ] Negative and corner scenarios are covered (missing records, invalid identifiers, boundaries).
- [ ] CDI integration is verified: template, repository and manager beans inject.
- [ ] Database behavior is verified against a real database.
- [ ] Unsupported operations are asserted and not claimed as supported.
- [ ] Tests are isolated and deterministic.
- [ ] Required Maven commands pass (see [`common.md`](common.md#57-commands)): `mvn -pl <db> -am clean install`, and the native build where supported.
- [ ] No unnecessary mocks, test resources, or dependencies were introduced.
