package io.quarkiverse.jnosql.qdrant.it;

import java.util.Map;

import org.eclipse.jnosql.databases.qdrant.communication.QdrantConfigurations;
import org.testcontainers.qdrant.QdrantContainer;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class QdrantTestResource implements QuarkusTestResourceLifecycleManager {

    private static final int GRPC_PORT = 6334;

    private QdrantContainer container;

    @Override
    public Map<String, String> start() {
        container = new QdrantContainer("qdrant/qdrant:v1.20.0");
        container.start();

        return Map.of(
                QdrantConfigurations.HOST.get(), container.getHost(),
                QdrantConfigurations.PORT.get(), String.valueOf(container.getMappedPort(GRPC_PORT)));
    }

    @Override
    public void stop() {
        if (container != null) {
            container.stop();
        }
    }
}
