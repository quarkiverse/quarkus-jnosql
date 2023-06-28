package redis.clients.jedis;

import java.util.List;

import io.vertx.mutiny.redis.client.RedisAPI;

public class Jedis {

    private final RedisAPI redis;

    public Jedis(RedisAPI redis) {
        this.redis = redis;
    }

    public String set(String key, String value) {
        return redis.setAndAwait(
                List.of(
                        key,
                        value))
                .toString();
    }

    public long expire(String key, long seconds) {
        return redis.expireAndAwait(
                List.of(
                        key,
                        String.valueOf(seconds)))
                .toLong();
    }

    public String get(String key) {
        return redis.getAndAwait(
                key).toString();
    }

    public long del(String key) {
        return redis.delAndAwait(
                List.of(
                        key))
                .toLong();
    }

    public void close() {
        this.redis.close();
    }

}
