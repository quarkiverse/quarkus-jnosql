package org.acme;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jnosql.databases.scylladb.communication.ScyllaDBConfigurations;
import org.testcontainers.scylladb.ScyllaDBContainer;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class ScyllaDBTestResource implements QuarkusTestResourceLifecycleManager {

    private ScyllaDBContainer container;

    @Override
    public Map<String, String> start() {
        container = new ScyllaDBContainer("${scylladb.image}")
                .withCommand("--developer-mode=1", "--overprovisioned=1", "--smp=1", "--memory=1G");
        container.start();
        Map<String, String> configuration = new HashMap<>(Map.of(
                ScyllaDBConfigurations.HOST.get() + ".1", container.getHost(),
                ScyllaDBConfigurations.PORT.get(), String.valueOf(container.getContactPoint().getPort()),
                ScyllaDBConfigurations.DATA_CENTER.get(), "datacenter1"));
        try (var input = getClass().getResourceAsStream("/init_script.cql")) {
            if (input == null) {
                throw new IllegalStateException("Missing init_script.cql");
            }
            String[] queries = new String(input.readAllBytes(), StandardCharsets.UTF_8).split(";");
            for (int i = 0; i < queries.length; i++) {
                if (!queries[i].isBlank()) {
                    configuration.put(ScyllaDBConfigurations.QUERY.get() + "." + String.format("%03d", i),
                            queries[i].strip());
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return configuration;
    }

    @Override
    public void stop() {
        if (container != null) {
            container.stop();
        }
    }
}
