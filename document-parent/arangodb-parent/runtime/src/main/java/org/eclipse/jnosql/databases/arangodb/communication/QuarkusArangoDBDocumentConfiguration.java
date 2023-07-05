package org.eclipse.jnosql.databases.arangodb.communication;

import jakarta.inject.Singleton;

import org.eclipse.jnosql.communication.Settings;
import org.eclipse.jnosql.communication.document.DocumentConfiguration;

@Singleton
public class QuarkusArangoDBDocumentConfiguration implements DocumentConfiguration {

    private final ArangoDBDocumentConfiguration configuration = new ArangoDBDocumentConfiguration();

    @Override
    public ArangoDBDocumentManagerFactory apply(Settings settings) throws NullPointerException {
        return configuration.apply(settings);
    }

}
