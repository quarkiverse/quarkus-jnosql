package org.acme;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import jakarta.json.Json;
import org.eclipse.jnosql.databases.couchdb.communication.CouchDBConfigurations;
import org.eclipse.jnosql.mapping.core.config.MappingConfigurations;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

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
            LOGGER.info("CouchDB started at {}:{}", container.getHost(), container.getMappedPort(PORT));

            settingUpDatabase(config);

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

    record Settings(String user, String password, String databaseName) {

        static Settings of(Config config) {
            return new Settings(
                    requireNonNull(config.getValue(CouchDBConfigurations.USER.get(), String.class),
                            "CouchDB user must not be null. Please set the '%s' configuration property."
                                    .formatted(CouchDBConfigurations.USER.get())),
                    requireNonNull(config.getValue(CouchDBConfigurations.PASSWORD.get(), String.class),
                            "CouchDB password must not be null. Please set the '%s' configuration property."
                                    .formatted(CouchDBConfigurations.PASSWORD.get())),
                    requireNonNull(config.getValue(MappingConfigurations.DOCUMENT_DATABASE.get(), String.class),
                            "CouchDB database name must not be null. Please set the '%s' configuration property."
                                    .formatted(MappingConfigurations.DOCUMENT_DATABASE.get())));
        }
    }

    private void settingUpDatabase(Config config) {

        Settings settings = Settings.of(config);

        createDatabase(settings);

        createIndexes(settings);

        LOGGER.info("Database '{}' created and indexes set up.", settings.databaseName());
    }

    private void createDatabase(Settings settings) {
        execute(settings,
                httpRequest -> httpRequest.PUT(java.net.http.HttpRequest.BodyPublishers.noBody()),
                response -> LOGGER.info("Database Creation Result: {}", response.body()),
                throwable -> LOGGER.error("Failed to create database", throwable));

    }

    private void createIndexes(Settings settings) {
        createIndex(settings, "number_index", "number");
        createIndex(settings, "guest_document_index", "guest.document");
    }

    private void createIndex(Settings settings, String indexName, String... fields) {

        var fieldsUsedByIndex = Arrays.stream(fields)
                .filter(Objects::nonNull)
                .filter(Predicate.not(String::isBlank))
                .reduce(Json.createArrayBuilder(),
                        (arrayFields, field) -> arrayFields.add(field),
                        (a, b) -> a.addAll(b)).build();

        var indexCreationPayload = Json.createObjectBuilder()
                .add("index", Json.createObjectBuilder()
                        .add("fields", fieldsUsedByIndex).build())
                .add("name", indexName)
                .add("type", "json")
                .build().toString();

        execute(settings,
                httpRequest -> httpRequest.POST(java.net.http.HttpRequest.BodyPublishers.ofString(indexCreationPayload)),
                response -> LOGGER.info("Index Creation Result: {}", response.body()),
                throwable -> LOGGER.error("Failed to create index", throwable));
    }


    private void execute(Settings settings,
                         Function<HttpRequest.Builder, HttpRequest.Builder> httpRequestBuilderFunction,
                         Consumer<HttpResponse<String>> httpResponseConsumer,
                         Consumer<Throwable> throwableConsumer
    ) {

        HttpClient client = HttpClient.newHttpClient();
        String url = String.format("http://%s:%d/%s", container.getHost(),
                container.getMappedPort(PORT), settings.databaseName());

        try {
            client.sendAsync(
                            Objects.requireNonNull(httpRequestBuilderFunction, "httpRequestBuilderFunction must not be null")
                                    .apply(java.net.http.HttpRequest.newBuilder()
                                            .uri(java.net.URI.create(url))
                                            .header("Content-Type", "application/json")
                                            .header("Authorization", "Basic " + Base64.getEncoder()
                                                    .encodeToString("%s:%s".formatted(settings.user(), settings.password()).getBytes())))
                                    .build(),
                            java.net.http.HttpResponse.BodyHandlers.ofString())
                    .thenAccept(Objects.requireNonNull(httpResponseConsumer, "httpResponseConsumer must not be null"))
                    .join();
        } catch (Exception e) {
            Optional.ofNullable(throwableConsumer)
                    .ifPresentOrElse(
                            consumer -> consumer.accept(e),
                            () -> LOGGER.error("Something unexpected has happened!", e));
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
