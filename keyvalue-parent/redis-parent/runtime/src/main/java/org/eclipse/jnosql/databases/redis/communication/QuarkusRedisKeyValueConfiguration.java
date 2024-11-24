package org.eclipse.jnosql.databases.redis.communication;

import jakarta.inject.Singleton;

import org.eclipse.jnosql.communication.Settings;
import org.eclipse.jnosql.communication.keyvalue.KeyValueConfiguration;

@Singleton
public class QuarkusRedisKeyValueConfiguration implements KeyValueConfiguration {

    private final RedisConfiguration configuration = new RedisConfiguration();

    @Override
    public RedisBucketManagerFactory apply(Settings settings) throws NullPointerException {
        return configuration.apply(settings);
    }

}
