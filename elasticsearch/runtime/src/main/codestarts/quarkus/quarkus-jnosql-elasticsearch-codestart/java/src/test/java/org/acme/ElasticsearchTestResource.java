package org.acme;

import java.util.Map;

import jakarta.nosql.MappingException;

import org.apache.http.HttpHost;
import org.eclipse.jnosql.databases.elasticsearch.communication.ElasticsearchConfigurations;
import org.eclipse.jnosql.mapping.core.config.MappingConfigurations;
import org.elasticsearch.client.RestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.json.jsonb.JsonbJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import io.quarkiverse.jnosql.core.runtime.MicroProfileSettings;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class ElasticsearchTestResource implements QuarkusTestResourceLifecycleManager {

    private static final DockerImageName IMAGE = DockerImageName.parse("docker.io/elastic/elasticsearch:8.15.0");

    private GenericContainer container;

    @Override
    public Map<String, String> start() {

        var database = new MicroProfileSettings()
                .get(MappingConfigurations.DOCUMENT_DATABASE.get(), String.class)
                .orElseThrow(() -> new MappingException("Please, inform the database filling up the property "
                        + MappingConfigurations.DOCUMENT_DATABASE.get()));

        container = new GenericContainer(IMAGE)
                .withExposedPorts(9200, 9300)
                .withEnv("discovery.type", "single-node")
                .withEnv("ES_JAVA_OPTS", "-Xms1g -Xmx1g")
                .withEnv("xpack.security.enabled", "false")
                .waitingFor(Wait.forHttp("/")
                        .forPort(9200)
                        .forStatusCode(200));
        container.start();

        String httpHost = String.format(
                "%s:%s",
                container.getHost(), container.getFirstMappedPort());

        var httpClient = RestClient
                .builder(HttpHost.create(httpHost))
                .build();

        try (var elasticsearchClient = new ElasticsearchClient(new RestClientTransport(httpClient, new JsonbJsonpMapper()))) {
            elasticsearchClient.indices().create(CreateIndexRequest.of(b -> b.index(database)));
        } catch (Exception e) {
            throw new RuntimeException("Failed to create index: " + database, e);
        }

        return Map.of(
                ElasticsearchConfigurations.HOST.get(),
                httpHost);
    }

    @Override
    public void stop() {
        container.stop();
    }

}