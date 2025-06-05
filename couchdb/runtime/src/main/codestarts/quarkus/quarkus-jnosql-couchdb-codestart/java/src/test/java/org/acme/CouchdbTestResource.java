package org.acme;


import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import org.eclipse.jnosql.databases.couchdb.communication.CouchDBConfigurations;
import org.eclipse.jnosql.mapping.core.config.MappingConfigurations;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.net.http.HttpClient;
import java.util.Base64;
import java.util.Map;
import java.util.Set;

public class CouchdbTestResource implements QuarkusTestResourceLifecycleManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(CouchdbTestResource.class);
    private static final int PORT = 5984;
    private static final String IMAGE = "couchdb:latest";

    private GenericContainer container;

    @Override
    public Map<String, String> start() {
        LOGGER.info(TestcontainersConfiguration.getInstance().toString());

        try {
            Config config = ConfigProvider.getConfig();

            container = new GenericContainer(IMAGE)
                    .withExposedPorts(PORT)
                    .withEnv(
                            "COUCHDB_USER",
                            config.getValue(
                                    CouchDBConfigurations.USER.get(),
                                    String.class))
                    .withEnv(
                            "COUCHDB_PASSWORD",
                            config.getValue(
                                    CouchDBConfigurations.PASSWORD.get(),
                                    String.class));
            container.start();
            createDatabase(config);
            createIndexes(config);

            return Map.of(
                    CouchDBConfigurations.PORT.get(),
                    container.getMappedPort(
                            PORT).toString(),
                    CouchDBConfigurations.HOST.get(),
                    container.getHost());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void createDatabase(Config config) {
        String username = config.getValue(CouchDBConfigurations.USER.get(), String.class);
        String password = config.getValue(CouchDBConfigurations.PASSWORD.get(), String.class);
        String databaseName = config.getValue(MappingConfigurations.DOCUMENT_DATABASE.get(), String.class);

        HttpClient client = HttpClient.newHttpClient();
        String url = String.format("http://%s:%d/%s", container.getHost(),
                container.getMappedPort(PORT), databaseName);

        try {
            client.sendAsync(
                            java.net.http.HttpRequest.newBuilder()
                                    .uri(java.net.URI.create(url))
                                    .header("Content-Type", "application/json")
                                    .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("%s:%s".formatted(username, password).getBytes()))
                                    .PUT(java.net.http.HttpRequest.BodyPublishers.noBody())
                                    .build(),
                            java.net.http.HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> LOGGER.info("Database Creation Result: {}", response.body()))
                    .join();
        } catch (Exception e) {
            LOGGER.error("Failed to create database", e);
        }
    }

    private void createIndexes(Config config) {

        String username = config.getValue(CouchDBConfigurations.USER.get(), String.class);
        String password = config.getValue(CouchDBConfigurations.PASSWORD.get(), String.class);
        String databaseName = config.getValue(MappingConfigurations.DOCUMENT_DATABASE.get(), String.class);
        createIndex(databaseName, username, password, Set.of("transmission"), "transmission_index");
    }

    private void createIndex(String databaseName, String username, String password, Set<String> fields, String indexName) {
        HttpClient client = HttpClient.newHttpClient();
        String url = String.format("http://%s:%d/%s/_index", container.getHost(),
                container.getMappedPort(PORT), databaseName);

        JsonArrayBuilder arrayFields = Json.createArrayBuilder();
        fields.forEach(arrayFields::add);

        String indexCreationPayload = Json.createObjectBuilder()
                .add("index", Json.createObjectBuilder()
                        .add("fields", arrayFields.build()).build())
                .add("name", indexName)
                .add("type", "json")
                .build()
                .toString();
        try {
            client.sendAsync(
                            java.net.http.HttpRequest.newBuilder()
                                    .uri(java.net.URI.create(url))
                                    .header("Content-Type", "application/json")
                                    .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("%s:%s".formatted(username, password).getBytes()))
                                    .POST(java.net.http.HttpRequest.BodyPublishers.ofString(indexCreationPayload))
                                    .build(),
                            java.net.http.HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> LOGGER.info("Index Creation Result: {}", response.body()))
                    .join();
        } catch (Exception e) {
            LOGGER.error("Failed to create index", e);
        }
    }

    @Override
    public void stop() {
        try {
            if (container != null) {
                container.stop();
            }
        } catch (Exception e) {
            // ignored
        }
    }
}
