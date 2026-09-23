package org.eclipse.jnosql.databases.memcached.communication;

import jakarta.annotation.PreDestroy;
import jakarta.inject.Singleton;

import org.eclipse.jnosql.communication.Settings;
import org.eclipse.jnosql.communication.keyvalue.KeyValueConfiguration;

@Singleton
public class QuarkusMemcachedKeyValueConfiguration implements KeyValueConfiguration {

    private final MemcachedKeyValueConfiguration configuration = new MemcachedKeyValueConfiguration();

    // Share the driver's client across all manager and factory injection points.
    private MemcachedBucketManagerFactory factory;

    @Override
    public synchronized MemcachedBucketManagerFactory apply(Settings settings) {
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
