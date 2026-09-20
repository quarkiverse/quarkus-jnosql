package io.quarkiverse.jnosql.questdb.it;

import java.util.Map;

import org.eclipse.jnosql.databases.questdb.communication.QuestDBTimeSeriesConfigurations;
import org.testcontainers.containers.QuestDBContainer;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class QuestDBTestResource implements QuarkusTestResourceLifecycleManager {

    private static final int QWP_PORT = 9000;

    private QuestDBContainer container;

    @Override
    public Map<String, String> start() {
        container = new QuestDBContainer("questdb/questdb:10.0.1");
        container.start();

        return Map.of(
                QuestDBTimeSeriesConfigurations.URL.get(),
                "ws::addr=" + container.getHost() + ":" + container.getMappedPort(QWP_PORT) + ";");
    }

    @Override
    public void stop() {
        if (container != null) {
            container.stop();
        }
    }
}
