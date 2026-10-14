package br.com.flashsale.checkout.application.consumer;

import br.com.flashsale.checkout.application.dto.CompensationMessage;
import br.com.flashsale.checkout.domain.model.OrderStatus;
import br.com.flashsale.checkout.infrastructure.persistence.OrderEntity;
import br.com.flashsale.checkout.infrastructure.persistence.OrderPanacheRepository;
import br.com.flashsale.checkout.infrastructure.redis.RedisStockService;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
public class CompensationConsumer {

    private static final Logger LOG = Logger.getLogger(CompensationConsumer.class);

    @Inject
    RedisStockService redisStockService;

    @Inject
    OrderPanacheRepository orderRepository;

    @Incoming("order-compensation")
    @Transactional // Obrigatorio para modificar o registro no banco PostgreSQL
    public void processCompensation(JsonObject json) {
        CompensationMessage message = json.mapTo(CompensationMessage.class);

        // Busca o pedido na tb_checkout_orders do db_checkout
        orderRepository.findByIdOptional(message.pedido_id()).ifPresentOrElse(order -> {

            // IDEMPOTÊNCIA: Se o pedido já estiver CANCELLED, ignora para não somar +3 no Redis novamente
            if (OrderStatus.CANCELLED.equals(order.status)) {
                LOG.warnf("SAGA - Pedido %s já foi compensado anteriormente. Ignorando mensagem duplicada.", message.pedido_id());
                return;
            }

            LOG.infof("SAGA - Processando compensação para o pedido %s, produto %s, qtd: %d",
                    message.pedido_id(), message.produto_id(), message.quantidade());

            // 1. Estorna a quantidade de volta no Redis
            redisStockService.incrementStock(message.produto_id(), message.quantidade());

            // 2. Atualiza o status na tb_checkout_orders para CANCELLED
            order.status = OrderStatus.CANCELLED;
            orderRepository.persist(order);

            LOG.infof("SAGA - Estoque do produto %s devolvido (+%d) no Redis e pedido %s atualizado para CANCELLED na db_checkout.",
                    message.produto_id(), message.quantidade(), message.pedido_id());

        }, () -> LOG.errorf("SAGA - Pedido %s não foi encontrado na db_checkout para compensação.", message.pedido_id()));
    }
}