package io.quarkiverse.jnosql.tinkerpop.runtime;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.eclipse.jnosql.communication.graph.GraphDatabaseManager;
import org.eclipse.jnosql.databases.tinkerpop.communication.TinkerpopGraphDatabaseManager;

/**
 * Exposes the application-owned {@link Graph} to Jakarta Data repositories.
 * The Graph is closed by its own producer, so no disposer is declared here.
 */
@ApplicationScoped
public class GraphDatabaseManagerProducer {

    @Produces
    @ApplicationScoped
    public GraphDatabaseManager graphDatabaseManager(Graph graph) {
        return TinkerpopGraphDatabaseManager.of(graph);
    }
}
