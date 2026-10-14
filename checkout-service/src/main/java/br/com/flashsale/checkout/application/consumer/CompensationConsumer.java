package br.com.flashsale.checkout.application.consumer;

import br.com.flashsale.checkout.application.dto.CompensationMessage;
import br.com.flashsale.checkout.domain.model.OrderStatus;
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
    OrderPanacheRepository orderPanacheRepository;

    @Incoming("order-compensation")
    @Transactional
    public void processCompensation(JsonObject json) {
        CompensationMessage message = json.mapTo(CompensationMessage.class);

        orderPanacheRepository.findByIdOptional(message.pedido_id()).ifPresentOrElse(order -> {

            // IDEMPOTÊNCIA: Se já foi cancelado, evita somar estoque no Redis novamente
            if (OrderStatus.CANCELLED.equals(order.getStatus())) {
                LOG.warnf("SAGA - Pedido %s já foi compensado anteriormente. Ignorando duplicação.", message.pedido_id());
                return;
            }

            LOG.infof("SAGA - Recebida solicitação de compensação para pedido %s, produto %s, qtd: %d",
                    message.pedido_id(), message.produto_id(), message.quantidade());

            // 1. Devolve o estoque no Redis
            redisStockService.incrementStock(message.produto_id(), message.quantidade());

            // 2. Atualiza o banco do db_checkout para CANCELLED
            order.setStatus(OrderStatus.CANCELLED);
            orderPanacheRepository.persist(order);

            LOG.infof("SAGA - Estoque do produto %s devolvido com sucesso no Redis e pedido %s marcado como CANCELLED.",
                    message.produto_id(), message.pedido_id());

        }, () -> LOG.errorf("SAGA - Pedido %s não encontrado no db_checkout.", message.pedido_id()));
    }
}