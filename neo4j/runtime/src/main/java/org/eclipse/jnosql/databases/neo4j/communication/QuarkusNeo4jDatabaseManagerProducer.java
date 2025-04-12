package org.eclipse.jnosql.databases.neo4j.communication;

import jakarta.annotation.Priority;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

import org.eclipse.jnosql.mapping.Database;
import org.eclipse.jnosql.mapping.DatabaseType;
import org.eclipse.jnosql.mapping.core.config.MappingConfigurations;

import io.quarkiverse.jnosql.core.runtime.AbstractDatabaseManagerProducer;

@Singleton
public class QuarkusNeo4jDatabaseManagerProducer extends
        AbstractDatabaseManagerProducer<Neo4JDatabaseManager, Neo4JDatabaseManagerFactory, QuarkusNeo4jDocumentConfiguration> {

    @Produces
    @Priority(1)
    @Alternative
    @Default
    @Database(DatabaseType.GRAPH)
    public Neo4JDatabaseManager get() {
        return get(MappingConfigurations.GRAPH_DATABASE);
    }

}
