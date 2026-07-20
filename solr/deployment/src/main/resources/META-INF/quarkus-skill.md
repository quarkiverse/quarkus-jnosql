### Required Setup Procedure

1. First load this `quarkus-jnosql-solr` skill.
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
- Solr uses the Document API and supports Jakarta Data repositories.
- Configure the document database name with `jnosql.document.database`.
- Inject document repositories and templates with `@Database(DatabaseType.DOCUMENT)` when CDI needs model disambiguation.
- Use repository methods for Jakarta Data queries and `Template` for direct document operations.

### Testing

- Use `@QuarkusTest` with the custom `SolrTestResource` pattern.
- The test resource starts a Testcontainers `SolrContainer` with image `solr:9.2.1` and collection `test`.
- Return the Solr host as `http://<host>:<port>/solr` using `SolrDocumentConfigurations.HOST`.

### Common Pitfalls

- Do not run Solr document tests without creating the collection used by the configured database.
- Do not pass only the raw host and port; the Solr host used by this extension includes the `/solr` path.
