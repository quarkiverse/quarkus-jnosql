package io.quarkiverse.jnosql.document.it;

import java.util.Map;

import org.eclipse.jnosql.databases.arangodb.communication.ArangoDBConfigurations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.TestcontainersConfiguration;

import com.github.dockerjava.api.model.Ulimit;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class ArangodbTestResource implements QuarkusTestResourceLifecycleManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArangodbTestResource.class);
    private GenericContainer<?> container;
    private static final String CONTAINER_NAME = "arango";
    private static final String ARANGO_IMAGE = "arangodb:latest";
    private static final String ARANGO_NO_AUTH = "ARANGO_NO_AUTH";
    private static final Integer PORT_DEFAULT = 8529;

    @Override
    public Map<String, String> start() {
        LOGGER.info(TestcontainersConfiguration.getInstance().toString());

        try {
            container = new GenericContainer<>(ARANGO_IMAGE)
                    .withExposedPorts(PORT_DEFAULT)
                    .withEnv(ARANGO_NO_AUTH, "1")
                    .withNetworkAliases(CONTAINER_NAME)
                    .waitingFor(Wait.forLogMessage(".*ArangoDB [(]version .*[)] is ready for business. Have fun!.*", 1))
                    .withCreateContainerCmdModifier(
                            cmd -> cmd.getHostConfig().withUlimits(new Ulimit[] { new Ulimit("nofile", 65535L, 65535L) }));

            container.start();

            return Map.of(
                    ArangoDBConfigurations.HOST.get(),
                    String.format(
                            "%s:%s",
                            container.getHost(),
                            container.getMappedPort(
                                    PORT_DEFAULT).toString()));
        } catch (Exception e) {
            throw new RuntimeException(e);
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
