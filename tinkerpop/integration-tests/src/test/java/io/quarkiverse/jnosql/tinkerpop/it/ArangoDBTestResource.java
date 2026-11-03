package io.quarkiverse.jnosql.tinkerpop.it;

import java.util.Map;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class ArangoDBTestResource implements QuarkusTestResourceLifecycleManager {

    private static final String ARANGO_IMAGE = "arangodb:3.12.13";
    private static final int PORT = 8529;

    private GenericContainer<?> container;

    @Override
    public Map<String, String> start() {
        container = new GenericContainer<>(ARANGO_IMAGE)
                .withExposedPorts(PORT)
                .withEnv("ARANGO_NO_AUTH", "1")
                .waitingFor(Wait.forHttp("/_api/version").forStatusCode(200));
        container.start();
        return Map.of("app.arangodb.hosts", container.getHost() + ":" + container.getMappedPort(PORT));
    }

    @Override
    public void stop() {
        if (container != null) {
            container.stop();
        }
    }
}
