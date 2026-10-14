package br.com.flashsale.infrastructure.messaging;

import br.com.flashsale.application.dto.CompensationMessage;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class CompensationProducer {

    @Inject
    @Channel("order-compensation")
    Emitter<CompensationMessage> emitter;

    public void publishCompensation(CompensationMessage message) {
        emitter.send(message);
    }
}