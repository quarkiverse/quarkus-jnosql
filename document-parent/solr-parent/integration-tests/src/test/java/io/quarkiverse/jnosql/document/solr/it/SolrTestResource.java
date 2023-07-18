package io.quarkiverse.jnosql.document.solr.it;

import java.util.Map;

import org.eclipse.jnosql.databases.solr.communication.SolrDocumentConfigurations;
import org.testcontainers.containers.SolrContainer;
import org.testcontainers.utility.DockerImageName;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class SolrTestResource implements QuarkusTestResourceLifecycleManager {

    private static final DockerImageName SOLR_IMAGE = DockerImageName.parse("solr:9.2.1");
    private static final String COLLECTION = "test";

    private SolrContainer container;

    @Override
    public Map<String, String> start() {

        container = new SolrContainer(SOLR_IMAGE)
                .withCollection(COLLECTION);
        container.start();
        return Map.of(
                SolrDocumentConfigurations.HOST.get(),
                String.format(
                        "http://%s:%s/solr",
                        container.getHost(),
                        container.getSolrPort()));
    }

    @Override
    public void stop() {
        container.stop();
    }

}
