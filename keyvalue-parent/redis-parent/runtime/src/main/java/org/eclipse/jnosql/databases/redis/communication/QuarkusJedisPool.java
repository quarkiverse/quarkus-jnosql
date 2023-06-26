package org.eclipse.jnosql.databases.redis.communication;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class QuarkusJedisPool extends JedisPool {

    private final QuarkusJedis jedis;

    public QuarkusJedisPool(QuarkusJedis jedis) {
        this.jedis = jedis;
    }

    @Override
    public Jedis getResource() {
        return this.jedis;
    }

    @Override
    public void close() {
    }

}
