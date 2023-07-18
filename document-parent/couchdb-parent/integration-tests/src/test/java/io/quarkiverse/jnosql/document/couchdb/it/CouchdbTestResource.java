package io.quarkiverse.jnosql.document.couchdb.it;

import java.util.Map;

import org.eclipse.jnosql.databases.couchdb.communication.CouchDBConfigurations;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.TestcontainersConfiguration;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class CouchdbTestResource implements QuarkusTestResourceLifecycleManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(CouchdbTestResource.class);
    private static final int PORT = 5984;
    private static final String IMAGE = "couchdb:3.3.2";

    private GenericContainer container;

    @Override
    public Map<String, String> start() {
        LOGGER.info(TestcontainersConfiguration.getInstance().toString());

        try {
            Config config = ConfigProvider.getConfig();

            container = new GenericContainer(IMAGE)
                    .withExposedPorts(PORT)
                    .withEnv(
                            "COUCHDB_USER",
                            config.getValue(
                                    CouchDBConfigurations.USER.get(),
                                    String.class))
                    .withEnv(
                            "COUCHDB_PASSWORD",
                            config.getValue(
                                    CouchDBConfigurations.PASSWORD.get(),
                                    String.class));
            container.start();

            return Map.of(
                    CouchDBConfigurations.PORT.get(),
                    container.getMappedPort(
                            PORT).toString());
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
