package io.quarkiverse.jnosql.milvus.it;

import java.util.Map;

import org.eclipse.jnosql.databases.milvus.communication.MilvusConfigurations;
import org.testcontainers.milvus.MilvusContainer;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class MilvusTestResource implements QuarkusTestResourceLifecycleManager {

    private static final int GRPC_PORT = 19530;

    private MilvusContainer container;

    @Override
    public Map<String, String> start() {
        container = new MilvusContainer("milvusdb/milvus:v3.0.4");
        // Milvus 3.x requires an explicit standalone deployment mode to use the embedded etcd
        container.withEnv("DEPLOY_MODE", "STANDALONE");
        container.start();

        return Map.of(
                MilvusConfigurations.HOST.get(), container.getHost(),
                MilvusConfigurations.PORT.get(), String.valueOf(container.getMappedPort(GRPC_PORT)));
    }

    @Override
    public void stop() {
        if (container != null) {
            container.stop();
        }
    }
}
