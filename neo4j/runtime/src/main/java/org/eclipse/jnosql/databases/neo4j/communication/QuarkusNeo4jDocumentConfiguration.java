package org.eclipse.jnosql.databases.neo4j.communication;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import org.eclipse.jnosql.communication.Settings;
import org.eclipse.jnosql.communication.semistructured.DatabaseConfiguration;
import org.neo4j.driver.Driver;

@Singleton
public class QuarkusNeo4jDocumentConfiguration implements DatabaseConfiguration {

    @Inject
    protected Driver driver;

    @Override
    public Neo4JDatabaseManagerFactory apply(Settings settings) {
        return new Neo4JDatabaseManagerFactory(this.driver);
    }

}
