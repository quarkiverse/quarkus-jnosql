package org.eclipse.jnosql.databases.redis.communication;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import org.eclipse.jnosql.communication.Settings;
import org.eclipse.jnosql.communication.keyvalue.KeyValueConfiguration;

import io.vertx.mutiny.redis.client.RedisAPI;

@Singleton
public class QuarkusRedisKeyValueConfiguration implements KeyValueConfiguration {

    @Inject
    protected RedisAPI client;

    @Override
    public RedisBucketManagerFactory apply(Settings settings) throws NullPointerException {
        return new DefaultRedisBucketManagerFactory(
                new QuarkusJedisPool(
                        new QuarkusJedis(client)));
    }

}
