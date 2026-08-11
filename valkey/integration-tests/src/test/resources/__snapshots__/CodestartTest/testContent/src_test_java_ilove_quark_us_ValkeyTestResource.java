package ilove.quark.us;

import java.util.Map;

import org.eclipse.jnosql.databases.valkey.communication.ValkeyConfigurations;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class ValkeyTestResource implements QuarkusTestResourceLifecycleManager {

    private static final DockerImageName IMAGE = DockerImageName.parse("valkey/valkey:latest");

    private GenericContainer container;

    @Override
    public Map<String, String> start() {

        container = new GenericContainer(IMAGE)
                .withExposedPorts(6379);
        container.start();

        return Map.of(
                ValkeyConfigurations.HOST.get(),
                container.getHost(),
                ValkeyConfigurations.PORT.get(),
                String.valueOf(container.getFirstMappedPort()));
    }

    @Override
    public void stop() {
        container.stop();
    }

}
