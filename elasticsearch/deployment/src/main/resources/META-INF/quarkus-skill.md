### Usage Patterns

- This extension brings `quarkus-jnosql-core` transitively. Use the JNoSQL Core skill for shared installation, entity mapping, generic `Template` and repository patterns, Java 21+ annotation processor setup, and generated-code troubleshooting.
- Elasticsearch uses the Document API and supports Jakarta Data repositories.
- Configure the document database name with `jnosql.document.database`.
- Inject document repositories and templates with `@Database(DatabaseType.DOCUMENT)` when CDI needs model disambiguation.
- Use repository methods for Jakarta Data queries and `Template` for direct document operations.

### Testing

- Use `@QuarkusTest` with the custom `ElasticsearchTestResource` pattern.
- The test resource starts `docker.io/elastic/elasticsearch:8.15.0`, exposes ports `9200` and `9300`, sets `discovery.type=single-node`, sets `ES_JAVA_OPTS=-Xms1g -Xmx1g`, disables security with `xpack.security.enabled=false`, and waits for HTTP 200 on port `9200`.
- Wait for indexed data to become visible before asserting search/query results when tests depend on Elasticsearch refresh behavior.

### Common Pitfalls

- Do not enable native-image expectations for this extension; the docs state Elasticsearch support does not support native compilation.
- Do not omit the custom Elasticsearch test resource unless an external Elasticsearch service is configured.
