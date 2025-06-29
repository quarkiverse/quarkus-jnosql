package org.acme;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.eclipse.jnosql.databases.elasticsearch.communication.ElasticsearchConfigurations;
import org.slf4j.Logger;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;

public class ElasticsearchTestResource implements QuarkusTestResourceLifecycleManager {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(ElasticsearchTestResource.class);
    private static final DockerImageName IMAGE = DockerImageName.parse("docker.io/elastic/elasticsearch:8.15.0");

    private GenericContainer container;

    @Override
    public Map<String, String> start() {

        container = new GenericContainer(IMAGE)
                .withExposedPorts(9200, 9300)
                .withEnv("discovery.type", "single-node")
                .withEnv("ES_JAVA_OPTS", "-Xms1g -Xmx1g")
                .withEnv("xpack.security.enabled", "false")
                .waitingFor(Wait.forHttp("/")
                        .forPort(9200)
                        .forStatusCode(200));
        container.start();

        String httpHost = String.format(
                "%s:%s",
                container.getHost(), container.getFirstMappedPort());

        return Map.of(
                ElasticsearchConfigurations.HOST.get(),
                httpHost);
    }

    @Override
    public void stop() {
        container.stop();
    }

}
