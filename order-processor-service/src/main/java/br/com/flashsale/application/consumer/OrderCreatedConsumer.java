package br.com.flashsale.application.consumer;

import br.com.flashsale.application.dto.OrderCreatedEvent;
import br.com.flashsale.domain.model.OrderProcess;
import br.com.flashsale.domain.repository.OrderProcessorDomainRepository;
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

    @Incoming("order-created")
    @Transactional
    public void processOrderCreated(JsonObject json) {
        OrderCreatedEvent event = json.mapTo(OrderCreatedEvent.class);

        if (event.pedido_id() != null && repository.findDomainById(event.pedido_id()).isPresent()) {
            LOG.infof("SAGA PROCESSOR - Pedido %s já foi processado, ignorando mensagem duplicada.", event.pedido_id());
            return;
        }

        LOG.infof("SAGA PROCESSOR - Recebido pedido %s. CPF: %s", event.pedido_id(), event.cliente_cpf());

        OrderProcess orderProcess = OrderProcess.createFromEvent(
                event.pedido_id(),
                event.produto_id(),
                event.quantidade(),
                event.cliente_cpf(),
                event.cliente_nome(),
                event.data_hora_criacao()
        );

        repository.save(orderProcess);
        LOG.infof("SAGA PROCESSOR - Pedido %s CONFIRMADO com sucesso!", event.pedido_id());
    }
}