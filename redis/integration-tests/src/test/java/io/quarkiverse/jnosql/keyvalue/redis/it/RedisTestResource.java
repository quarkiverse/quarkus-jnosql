package io.quarkiverse.jnosql.keyvalue.redis.it;

import java.util.Map;

import org.eclipse.jnosql.databases.redis.communication.RedisConfigurations;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class RedisTestResource implements QuarkusTestResourceLifecycleManager {

    private static final DockerImageName IMAGE = DockerImageName.parse("docker.io/redis:latest");

    private GenericContainer container;

    @Override
    public Map<String, String> start() {

        container = new GenericContainer(IMAGE)
                .withExposedPorts(6379);
        container.start();

        return Map.of(
                RedisConfigurations.HOST.get(),
                container.getHost(),
                RedisConfigurations.PORT.get(),
                String.valueOf(container.getFirstMappedPort()));
    }

    @Override
    public void stop() {
        container.stop();
    }

}
