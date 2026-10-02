package org.eclipse.jnosql.databases.infinispan.communication;

import jakarta.annotation.PreDestroy;
import jakarta.inject.Singleton;

import org.eclipse.jnosql.communication.Settings;
import org.eclipse.jnosql.communication.keyvalue.KeyValueConfiguration;

@Singleton
public class QuarkusInfinispanKeyValueConfiguration implements KeyValueConfiguration {

    private final InfinispanKeyValueConfiguration configuration = new InfinispanKeyValueConfiguration();

    // Share the driver's cache container across manager and factory injection points.
    private InfinispanBucketManagerFactory factory;

    @Override
    public synchronized InfinispanBucketManagerFactory apply(Settings settings) {
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
