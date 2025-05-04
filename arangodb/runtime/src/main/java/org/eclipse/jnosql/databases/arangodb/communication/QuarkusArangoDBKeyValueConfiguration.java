package org.eclipse.jnosql.databases.arangodb.communication;

import jakarta.inject.Singleton;

import org.eclipse.jnosql.communication.Settings;
import org.eclipse.jnosql.communication.keyvalue.KeyValueConfiguration;

@Singleton
public class QuarkusArangoDBKeyValueConfiguration implements KeyValueConfiguration {

    private final ArangoDBKeyValueConfiguration configuration = new ArangoDBKeyValueConfiguration();

    @Override
    public ArangoDBBucketManagerFactory apply(Settings settings) throws NullPointerException {
        return configuration.apply(settings);
    }

}
