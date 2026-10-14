package br.com.flashsale.checkout.infrastructure.redis;

import br.com.flashsale.checkout.application.dto.OrderResponse;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.value.ValueCommands;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class RedisCacheService {

    @Inject
    RedisDataSource redisDataSource;

    private static final long TTL_SECONDS = 300; // 5 minutos

    public Optional<OrderResponse> getOrder(UUID orderId) {
        ValueCommands<String, OrderResponse> valueCommands = redisDataSource.value(OrderResponse.class);
        OrderResponse cached = valueCommands.get("order:" + orderId.toString());
        return Optional.ofNullable(cached);
    }

    public void putOrder(OrderResponse orderResponse) {
        ValueCommands<String, OrderResponse> valueCommands = redisDataSource.value(OrderResponse.class);
        String key = "order:" + orderResponse.pedido_id().toString();
        valueCommands.setex(key, TTL_SECONDS, orderResponse);
    }
}
