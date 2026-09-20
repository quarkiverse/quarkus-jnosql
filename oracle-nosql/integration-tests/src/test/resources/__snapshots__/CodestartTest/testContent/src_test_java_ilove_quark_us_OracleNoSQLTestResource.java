package ilove.quark.us;

import java.util.Map;

import org.eclipse.jnosql.databases.oracle.communication.OracleNoSQLConfigurations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.TestcontainersConfiguration;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class OracleNoSQLTestResource implements QuarkusTestResourceLifecycleManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(OracleNoSQLTestResource.class);
    private GenericContainer<?> container;
    private static final String CONTAINER_NAME = "oracle-nosql";
    private static final String CONTAINER_IMAGE = "ghcr.io/oracle/nosql:2025-12-ce";
    private static final Integer PORT_DEFAULT = 8080;

    @Override
    public Map<String, String> start() {
        LOGGER.info(TestcontainersConfiguration.getInstance().toString());

        try {
            container = new GenericContainer<>(CONTAINER_IMAGE)
                    .withExposedPorts(PORT_DEFAULT)
                    .withNetworkAliases(CONTAINER_NAME)
                    .waitingFor(Wait.forLogMessage(".*proxyVersion=.*\\n", 1));
            container.start();

            return Map.of(
                    OracleNoSQLConfigurations.HOST.get(),
                    String.format(
                            "http://%s:%s",
                            container.getHost(),
                            container.getMappedPort(PORT_DEFAULT).toString()));

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