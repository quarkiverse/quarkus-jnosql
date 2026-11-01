package io.quarkiverse.jnosql.weaviate.it;

import java.util.Map;

import org.eclipse.jnosql.databases.weaviate.communication.WeaviateConfigurations;
import org.testcontainers.weaviate.WeaviateContainer;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class WeaviateTestResource implements QuarkusTestResourceLifecycleManager {

    private WeaviateContainer container;

    @Override
    public Map<String, String> start() {
        container = new WeaviateContainer("semitechnologies/weaviate:1.38.21");
        container.start();

        // both the HTTP and the gRPC channels are required by the Weaviate client
        return Map.of(
                WeaviateConfigurations.HOST.get(), container.getHost(),
                WeaviateConfigurations.HTTP_PORT.get(), String.valueOf(container.getHttpPort()),
                WeaviateConfigurations.GRPC_PORT.get(), String.valueOf(container.getGrpcPort()));
    }

    @Override
    public void stop() {
        if (container != null) {
            container.stop();
        }
    }
}
