### Required Setup Procedure

1. First load this `quarkus-jnosql-neo4j` skill.
2. Then immediately load and follow the `quarkus-jnosql-core` skill before making code or build changes.
3. Treat the `quarkus-jnosql-core` skill as authoritative for:
   - installation steps
   - annotation processor configuration
   - entity metadata generation
   - repository setup
   - generated-code troubleshooting
4. Use codestarts only as fallback references if the Core skill is unavailable or incomplete.

### Usage Patterns

- This extension brings `quarkus-jnosql-core` transitively. Use the `quarkus-jnosql-core` skill for shared installation, entity mapping, generic `Template` and repository patterns, Java 21+ annotation processor setup, and generated-code troubleshooting.
- Neo4j uses the Graph API and supports graph-oriented Jakarta Data usage.
- Configure the graph database name with `jnosql.graph.database`.
- Inject `org.eclipse.jnosql.mapping.graph.GraphTemplate` with `@Database(DatabaseType.GRAPH)` for graph template operations.
- Use repository interfaces for graph entities when using the repository pattern.

### Testing

- Use `@QuarkusTest` for Neo4j extension tests.
- The current integration tests do not attach a custom `QuarkusTestResource`; rely on the configured Neo4j service or Dev Services behavior.
- Clean repository data in `@BeforeEach` with `repository.findAll().forEach(repository::delete)` when repository tests insert data.
- Test `GraphTemplate` operations separately from repository operations when both are used.

### Common Pitfalls

- Do not inject the generic `Template` for graph operations; use `GraphTemplate`.
- Do not use document, column, or key-value configuration properties for Neo4j graph tests.
