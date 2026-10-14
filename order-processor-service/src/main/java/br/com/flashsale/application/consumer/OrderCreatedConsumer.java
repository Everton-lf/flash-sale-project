package br.com.flashsale.application.consumer;

import br.com.flashsale.application.dto.CompensationMessage;
import br.com.flashsale.application.dto.OrderCreatedEvent;
import br.com.flashsale.domain.model.OrderProcess;
import br.com.flashsale.domain.repository.OrderProcessorDomainRepository;
import br.com.flashsale.infrastructure.messaging.CompensationProducer;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
public class OrderCreatedConsumer {

    private static final Logger LOG = Logger.getLogger(OrderCreatedConsumer.class);

    @Inject
    OrderProcessorDomainRepository repository;

    @Inject
    CompensationProducer compensationProducer;

    @Incoming("order-created")
    @Transactional
    public void processOrderCreated(JsonObject json) {
        // Converte o JSON do Vert.x para o seu DTO mantendo o restante da classe idêntico
        OrderCreatedEvent event = json.mapTo(OrderCreatedEvent.class);

        LOG.infof("Recebido pedido para processamento: %s - Produto: %s", event.pedido_id(), event.produto_id());

        try {
            // Regra de simulação de falha: Se o CPF do cliente terminar em "000", lança erro proposital
            if (event.cliente_cpf() != null && event.cliente_cpf().endsWith("000")) {
                throw new RuntimeException("Falha simulada no processamento de pagamento para o CPF " + event.cliente_cpf());
            }

            // 1. Sucesso: Salva no banco db_order_processor com status CONFIRMED
            OrderProcess orderProcess = OrderProcess.createFromEvent(
                    event.pedido_id(),
                    event.produto_id(),
                    event.quantidade(),
                    event.cliente_cpf(),
                    event.cliente_nome(),
                    event.data_hora_criacao()
            );

            repository.save(orderProcess);
            LOG.infof("Pedido %s processado e CONFIRMADO com sucesso!", event.pedido_id());

        } catch (Exception e) {
            LOG.errorf("Erro ao processar o pedido %s: %s. Iniciando compensação SAGA...", event.pedido_id(), e.getMessage());

            // 2. Falha: Atualiza/Salva o registro no banco com status CANCELLED
            OrderProcess cancelledOrder = OrderProcess.createFromEvent(
                    event.pedido_id(),
                    event.produto_id(),
                    event.quantidade(),
                    event.cliente_cpf(),
                    event.cliente_nome(),
                    event.data_hora_criacao()
            );
            cancelledOrder.markAsCancelled();
            repository.save(cancelledOrder);

            // 3. Emite compensação para estornar estoque no Redis
            CompensationMessage compensation = new CompensationMessage(
                    event.pedido_id(),
                    event.produto_id(),
                    event.quantidade(),
                    e.getMessage()
            );
            compensationProducer.publishCompensation(compensation);
            LOG.infof("Mensagem de compensação emitida para o pedido %s", event.pedido_id());
        }
    }
}