package io.quarkiverse.jnosql.iotdb.it;

import java.util.List;
import java.util.Map;

import org.apache.iotdb.isession.ITableSession;
import org.apache.iotdb.isession.pool.ITableSessionPool;
import org.apache.iotdb.session.pool.TableSessionPoolBuilder;
import org.eclipse.jnosql.databases.iotdb.communication.IoTDBTimeSeriesConfigurations;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class IoTDBTestResource implements QuarkusTestResourceLifecycleManager {

    private static final DockerImageName IMAGE = DockerImageName.parse("apache/iotdb:2.0.11-standalone");
    private static final int RPC_PORT = 6667;
    private static final String DATABASE = "jnosql";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    private GenericContainer<?> container;

    @Override
    public Map<String, String> start() {
        container = new GenericContainer<>(IMAGE)
                .withExposedPorts(RPC_PORT)
                .withEnv("dn_rpc_address", "0.0.0.0")
                .waitingFor(Wait.forLogMessage(".*DataNode started.*\\n", 1));
        container.start();

        try (ITableSessionPool pool = new TableSessionPoolBuilder()
                .nodeUrls(List.of(nodeUrl()))
                .user(USERNAME)
                .password(PASSWORD)
                .enableRedirection(false)
                .maxSize(1)
                .build();
                ITableSession session = pool.getSession()) {
            session.executeNonQueryStatement("CREATE DATABASE IF NOT EXISTS " + DATABASE);
        } catch (Exception exception) {
            throw new IllegalStateException("Could not initialize the IoTDB test database", exception);
        }

        return Map.of(
                IoTDBTimeSeriesConfigurations.HOST.get(), container.getHost(),
                IoTDBTimeSeriesConfigurations.PORT.get(), Integer.toString(container.getMappedPort(RPC_PORT)),
                IoTDBTimeSeriesConfigurations.USERNAME.get(), USERNAME,
                IoTDBTimeSeriesConfigurations.PASSWORD.get(), PASSWORD,
                IoTDBTimeSeriesConfigurations.ENABLE_REDIRECTION.get(), Boolean.FALSE.toString());
    }

    @Override
    public void stop() {
        if (container != null) {
            container.stop();
        }
    }

    private String nodeUrl() {
        return container.getHost() + ":" + container.getMappedPort(RPC_PORT);
    }
}
