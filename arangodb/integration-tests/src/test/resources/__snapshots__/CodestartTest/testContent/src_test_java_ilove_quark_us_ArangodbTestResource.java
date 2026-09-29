package ilove.quark.us;

import java.util.Map;

import org.eclipse.jnosql.databases.arangodb.communication.ArangoDBConfigurations;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class ArangodbTestResource implements QuarkusTestResourceLifecycleManager {

    private GenericContainer<?> container;
    private static final String ARANGO_IMAGE = "arangodb/arangodb:latest";
    private static final String ARANGO_NO_AUTH = "ARANGO_NO_AUTH";
    private static final Integer PORT_DEFAULT = 8529;

    @Override
    public Map<String, String> start() {
        container = new GenericContainer<>(ARANGO_IMAGE)
                .withExposedPorts(PORT_DEFAULT)
                .withEnv(ARANGO_NO_AUTH, "1")
                .waitingFor(Wait.forHttp("/").forStatusCode(200));
        container.start();

        return Map.of(ArangoDBConfigurations.HOST.get(),
                container.getHost() + ":" + container.getFirstMappedPort());
    }

    @Override
    public void stop() {
        if (container != null) {
            container.stop();
        }
    }
}
