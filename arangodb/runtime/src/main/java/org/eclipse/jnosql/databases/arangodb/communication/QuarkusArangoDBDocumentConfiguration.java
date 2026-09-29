package org.eclipse.jnosql.databases.arangodb.communication;

import jakarta.inject.Singleton;

import org.eclipse.jnosql.communication.Settings;
import org.eclipse.jnosql.communication.semistructured.DatabaseConfiguration;

@Singleton
public class QuarkusArangoDBDocumentConfiguration implements DatabaseConfiguration {

    @Override
    public ArangoDBDocumentManagerFactory apply(Settings settings) {
        return new ArangoDBDocumentConfiguration().apply(settings);
    }

}
