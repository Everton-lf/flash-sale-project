package br.com.flashsale.checkout.infrastructure.messaging;

import br.com.flashsale.checkout.application.dto.OrderCreatedEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class OrderEventProducer {

    @Inject
    @Channel("order-created")
    Emitter<OrderCreatedEvent> emitter;

    public void publishOrderCreated(OrderCreatedEvent event) {
        emitter.send(event);
    }
}