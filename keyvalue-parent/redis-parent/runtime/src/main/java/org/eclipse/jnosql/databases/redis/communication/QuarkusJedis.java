package org.eclipse.jnosql.databases.redis.communication;

import java.util.List;

import io.vertx.mutiny.redis.client.RedisAPI;
import redis.clients.jedis.Jedis;

public class QuarkusJedis extends Jedis {

    private final RedisAPI redis;

    public QuarkusJedis(RedisAPI redis) {
        this.redis = redis;
    }

    @Override
    public String set(String key, String value) {
        return redis.setAndAwait(
                List.of(
                        key,
                        value))
                .toString();
    }

    @Override
    public long expire(String key, long seconds) {
        return redis.expireAndAwait(
                List.of(
                        key,
                        String.valueOf(seconds)))
                .toLong();
    }

    @Override
    public String get(String key) {
        return redis.getAndAwait(
                key).toString();
    }

    @Override
    public long del(String key) {
        return redis.delAndAwait(
                List.of(
                        key))
                .toLong();
    }

    @Override
    public void close() {
        this.redis.close();
    }

    @Override
    public String toString() {
        return this.redis.toString();
    }

}
