package io.quarkiverse.jnosql.column.hbase.it;

import java.util.Map;

import org.slf4j.LoggerFactory;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.TestcontainersConfiguration;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class HBaseTestResource implements QuarkusTestResourceLifecycleManager {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(HBaseTestResource.class);
    private GenericContainer<?> container;
    private static final String ARANGO_IMAGE = "dajobe/hbase:latest";
    private static final Integer PORT_DEFAULT = 2181;

    @Override
    public Map<String, String> start() {
        LOGGER.info(TestcontainersConfiguration.getInstance().toString());

        try {
            container = new FixedHostPortGenericContainer<>(ARANGO_IMAGE)
                    .withFixedExposedPort(PORT_DEFAULT, PORT_DEFAULT);

            container.start();

            return Map.of();
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
