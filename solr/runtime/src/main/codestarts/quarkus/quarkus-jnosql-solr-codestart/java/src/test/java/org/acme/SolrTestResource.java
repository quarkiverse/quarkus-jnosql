package org.acme;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.eclipse.jnosql.databases.solr.communication.SolrDocumentConfigurations;
import org.eclipse.jnosql.mapping.core.config.MappingConfigurations;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.slf4j.Logger;
import org.testcontainers.containers.SolrContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.util.Map;

public class SolrTestResource implements QuarkusTestResourceLifecycleManager {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SolrTestResource.class);

    private static final DockerImageName SOLR_IMAGE = DockerImageName.parse("solr:9.8.1");

    private SolrContainer container;

    @Override
    public Map<String, String> start() {

        Config config = ConfigProvider.getConfig();

        String collection = config.getValue(
                MappingConfigurations.DOCUMENT_DATABASE.get(),
                String.class);

        container = new SolrContainer(SOLR_IMAGE)
                .withCollection(collection);
        container.start();

        createSchema(container.getHost(), container.getSolrPort(), collection);

        return Map.of(
                SolrDocumentConfigurations.HOST.get(),
                String.format(
                        "http://%s:%s/solr",
                        container.getHost(),
                        container.getSolrPort()));
    }

    private void createSchema(String host, int port, String collection) {
        try {
            Http2SolrClient solrClient = new Http2SolrClient
                    .Builder("http://%s:%s/solr/%s".formatted(host, port, collection)).build();

            LOGGER.info("Added Field: {}", solrClient.request(new SchemaRequest.AddField(
                    Map.of("name", "_entity",
                            "type", "string",
                            "stored", Boolean.TRUE,
                            "indexed", Boolean.TRUE)
            )));

            LOGGER.info("Added Field: {}", solrClient.request(new SchemaRequest.AddField(
                    Map.of("name", "transmission",
                            "type", "string",
                            "stored", Boolean.TRUE,
                            "indexed", Boolean.TRUE)
            )));
            LOGGER.info("Commit changes: {}", solrClient.commit());
            LOGGER.info("Schema Created");
        } catch (SolrServerException e) {
            LOGGER.error("Solr server exception occurred while starting the test resource", e);
            throw new RuntimeException("Failed to start Solr test resource", e);
        } catch (IOException e) {
            LOGGER.error("IO or Interrupted exception occurred while starting the test resource", e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to start Solr test resource", e);
        }
    }

    @Override
    public void stop() {
        container.stop();
    }

}
