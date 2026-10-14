package br.com.flashsale.checkout.application.service;

import br.com.flashsale.checkout.application.dto.CreateOrderRequest;
import br.com.flashsale.checkout.application.dto.OrderResponse;
import br.com.flashsale.checkout.application.mapper.OrderMapper;
import br.com.flashsale.checkout.domain.exceptions.InsufficientStockException;
import br.com.flashsale.checkout.domain.exceptions.OrderNotFoundException;
import br.com.flashsale.checkout.domain.model.Order;
import br.com.flashsale.checkout.domain.repository.OrderDomainRepository;
import br.com.flashsale.checkout.infrastructure.messaging.OrderEventProducer;
import br.com.flashsale.checkout.infrastructure.redis.RedisCacheService;
import br.com.flashsale.checkout.infrastructure.redis.RedisStockService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class OrderCheckoutService {

    @Inject
    RedisStockService redisStockService;

    @Inject
    RedisCacheService redisCacheService;

    @Inject
    OrderDomainRepository orderRepository;

    @Inject
    OrderEventProducer eventProducer;

    @Inject
    OrderMapper mapper;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        // 1. Reserva atômica no Redis via Script Lua
        boolean reserved = redisStockService.reserveStock(request.produto_id(), request.quantidade());
        if (!reserved) {
            throw new InsufficientStockException(request.produto_id());
        }

        // 2. Instanciação do objeto do Domínio
        Order order = Order.createNew(request.produto_id(), request.quantidade(), request.cliente_cpf(), request.cliente_nome());

        // 3. Persistência local no banco db_checkout
        orderRepository.save(order);

        // 4. Publicação na fila order-created do RabbitMQ
        eventProducer.publishOrderCreated(mapper.toEvent(order));

        return mapper.toResponse(order);
    }

    public OrderResponse getOrderById(UUID orderId) {
        // 1. Tenta buscar no Redis Cache
        Optional<OrderResponse> cached = redisCacheService.getOrder(orderId);
        if (cached.isPresent()) {
            return cached.get();
        }

        // 2. Realiza até 3 tentativas no banco de dados relacional
        for (int attempt = 1; attempt <= 3; attempt++) {
            Optional<Order> dbOrder = orderRepository.findDomainById(orderId);
            if (dbOrder.isPresent()) {
                OrderResponse response = mapper.toResponse(dbOrder.get());
                redisCacheService.putOrder(response); // Guarda no Redis com TTL de 5 min
                return response;
            }

            if (attempt < 3) {
                try {
                    Thread.sleep(100); // Aguarda 100ms antes de tentar novamente
                } catch (InterruptedException ignored) {}
            }
        }

        // 3. Se não encontrar em 3 tentativas, lança exceção (404)
        throw new OrderNotFoundException("Pedido não encontrado");
    }
}
