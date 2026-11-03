package io.quarkiverse.jnosql.tinkerpop.it;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;

import org.apache.commons.configuration2.BaseConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.util.GraphFactory;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.arangodb.tinkerpop.gremlin.structure.ArangoDBGraph;
import com.arangodb.tinkerpop.gremlin.structure.ArangoDBGraphConfig;

/**
 * Application-owned Graph: the extension only consumes this bean.
 */
@ApplicationScoped
public class ArangoGraphProducer {

    @ConfigProperty(name = "app.arangodb.hosts")
    String hosts;

    @Produces
    @ApplicationScoped
    public Graph graph() {
        Configuration configuration = new BaseConfiguration();
        configuration.addProperty("gremlin.graph", ArangoDBGraph.class.getName());
        configuration.addProperty("gremlin.arangodb.conf.graph.enableDataDefinition", true);
        configuration.addProperty("gremlin.arangodb.conf.graph.type", ArangoDBGraphConfig.GraphType.COMPLEX.name());
        configuration.addProperty("gremlin.arangodb.conf.graph.edgeDefinitions", List.of("knows:[Person]->[Person]"));
        configuration.addProperty("gremlin.arangodb.conf.driver.hosts", hosts);
        return GraphFactory.open(configuration);
    }

    public void close(@Disposes Graph graph) throws Exception {
        graph.close();
    }
}
