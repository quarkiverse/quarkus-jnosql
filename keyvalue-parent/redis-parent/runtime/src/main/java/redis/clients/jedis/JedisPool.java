package redis.clients.jedis;

public class JedisPool {

    private final Jedis jedis;

    public JedisPool(Jedis jedis) {
        this.jedis = jedis;
    }

    public Jedis getResource() {
        return this.jedis;
    }

    public void close() {
    }

}
