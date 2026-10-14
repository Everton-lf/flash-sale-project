package br.com.flashsale.checkout.infrastructure.redis;

import io.quarkus.redis.datasource.RedisDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RedisStockService {

    @Inject
    RedisDataSource redisDataSource;

    // Script Lua Atômico: Verifica se há estoque e decrementa em 1 única operação no Redis
    private static final String LUA_DECREMENT_SCRIPT =
            "local current = tonumber(redis.call('get', KEYS[1])) " +
                    "local requested = tonumber(ARGV[1]) " +
                    "if current and current >= requested then " +
                    "    return redis.call('decrby', KEYS[1], requested) " +
                    "else " +
                    "    return -1 " +
                    "end";

    public boolean reserveStock(String productId, int quantity) {
        String key = "stock:" + productId;
        Long result = redisDataSource.execute("EVAL", LUA_DECREMENT_SCRIPT, "1", key, String.valueOf(quantity)).toLong();
        return result != null && result >= 0;
    }

    public void incrementStock(String productId, int quantity) {
        String key = "stock:" + productId;
        redisDataSource.value(String.class).incrby(key, quantity);
    }
}