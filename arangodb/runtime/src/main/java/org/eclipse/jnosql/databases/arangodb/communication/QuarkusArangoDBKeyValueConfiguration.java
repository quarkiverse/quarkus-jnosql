package org.eclipse.jnosql.databases.arangodb.communication;

import jakarta.inject.Singleton;

import org.eclipse.jnosql.communication.Settings;
import org.eclipse.jnosql.communication.keyvalue.KeyValueConfiguration;

@Singleton
public class QuarkusArangoDBKeyValueConfiguration implements KeyValueConfiguration {

    @Override
    public ArangoDBBucketManagerFactory apply(Settings settings) {
        return new ArangoDBKeyValueConfiguration().apply(settings);
    }

}
