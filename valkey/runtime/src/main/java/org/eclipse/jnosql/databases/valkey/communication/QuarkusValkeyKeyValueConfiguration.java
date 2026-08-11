package org.eclipse.jnosql.databases.valkey.communication;

import jakarta.annotation.PreDestroy;
import jakarta.inject.Singleton;

import org.eclipse.jnosql.communication.Settings;
import org.eclipse.jnosql.communication.keyvalue.KeyValueConfiguration;

@Singleton
public class QuarkusValkeyKeyValueConfiguration implements KeyValueConfiguration {

    private final ValkeyConfiguration configuration = new ValkeyConfiguration();

    // A single factory is shared by every injection point so the whole
    // application uses one connection pool.
    private ValkeyBucketManagerFactory factory;

    @Override
    public synchronized ValkeyBucketManagerFactory apply(Settings settings) {
        if (factory == null) {
            factory = configuration.apply(settings);
        }
        return factory;
    }

    @PreDestroy
    synchronized void close() {
        if (factory != null) {
            factory.close();
            factory = null;
        }
    }

}
