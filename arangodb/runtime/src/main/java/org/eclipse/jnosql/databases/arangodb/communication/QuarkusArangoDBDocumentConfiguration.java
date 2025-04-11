package org.eclipse.jnosql.databases.arangodb.communication;

import jakarta.inject.Singleton;

import org.eclipse.jnosql.communication.Settings;
import org.eclipse.jnosql.communication.semistructured.DatabaseConfiguration;

@Singleton
public class QuarkusArangoDBDocumentConfiguration implements DatabaseConfiguration {

    private final ArangoDBDocumentConfiguration configuration = new ArangoDBDocumentConfiguration();

    @Override
    public ArangoDBDocumentManagerFactory apply(Settings settings) throws NullPointerException {
        return configuration.apply(settings);
    }

}
