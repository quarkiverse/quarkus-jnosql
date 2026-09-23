package io.quarkiverse.jnosql.keyvalue.memcached.it;

import java.util.Map;

import org.eclipse.jnosql.databases.memcached.communication.MemcachedConfigurations;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class MemcachedTestResource implements QuarkusTestResourceLifecycleManager {

    private static final DockerImageName IMAGE = DockerImageName.parse("memcached:latest");

    private GenericContainer<?> container;

    @Override
    public Map<String, String> start() {

        container = new GenericContainer<>(IMAGE)
                .withExposedPorts(11211)
                .waitingFor(Wait.defaultWaitStrategy());
        container.start();

        return Map.of(
                MemcachedConfigurations.HOST.get() + ".1",
                container.getHost() + ':' + container.getFirstMappedPort());
    }

    @Override
    public void stop() {
        if (container != null) {
            container.stop();
        }
    }

}
