package org.eclipse.jnosql.databases.redis.communication;

import jakarta.annotation.PreDestroy;
import jakarta.inject.Singleton;

import org.eclipse.jnosql.communication.Settings;
import org.eclipse.jnosql.communication.keyvalue.KeyValueConfiguration;

@Singleton
public class QuarkusRedisKeyValueConfiguration implements KeyValueConfiguration {

    private final RedisConfiguration configuration = new RedisConfiguration();

    // A single factory is shared by every injection point so the whole
    // application uses one Jedis connection pool.
    private RedisBucketManagerFactory factory;

    @Override
    public synchronized RedisBucketManagerFactory apply(Settings settings) {
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
