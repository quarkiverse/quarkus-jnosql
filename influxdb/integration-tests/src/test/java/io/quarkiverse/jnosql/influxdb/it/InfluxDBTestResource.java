package io.quarkiverse.jnosql.influxdb.it;

import java.util.Map;

import org.eclipse.jnosql.databases.influxdb.communication.InfluxDBTimeSeriesConfigurations;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class InfluxDBTestResource implements QuarkusTestResourceLifecycleManager {

    private static final DockerImageName IMAGE = DockerImageName.parse("influxdb:3.11.0-core");
    private static final int PORT = 8181;
    private static final String DATABASE = "metrics";
    private static final String TOKEN = "jnosql-influxdb-test-token";

    private GenericContainer<?> container;

    @Override
    public Map<String, String> start() {
        container = new GenericContainer<>(IMAGE)
                .withExposedPorts(PORT)
                .withCommand("influxdb3", "serve",
                        "--node-id", "jnosql",
                        "--object-store", "memory",
                        "--without-auth")
                .waitingFor(Wait.forHttp("/health").forPort(PORT).forStatusCode(200));
        container.start();

        try {
            Container.ExecResult result = container.execInContainer(
                    "influxdb3", "create", "database",
                    "--host", "http://localhost:8181", DATABASE);
            if (result.getExitCode() != 0) {
                throw new IllegalStateException("Could not create InfluxDB test database: " + result.getStderr());
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Could not initialize InfluxDB test database", exception);
        }

        return Map.of(
                InfluxDBTimeSeriesConfigurations.URL.get(),
                "http://" + container.getHost() + ":" + container.getMappedPort(PORT),
                InfluxDBTimeSeriesConfigurations.TOKEN.get(),
                TOKEN);
    }

    @Override
    public void stop() {
        if (container != null) {
            container.stop();
        }
    }
}
