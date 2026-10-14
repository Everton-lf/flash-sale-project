package br.com.flashsale.checkout.application.mapper;

import br.com.flashsale.checkout.application.dto.OrderCreatedEvent;
import br.com.flashsale.checkout.application.dto.OrderResponse;
import br.com.flashsale.checkout.domain.model.Order;
import br.com.flashsale.checkout.infrastructure.persistence.OrderEntity;

public interface OrderMapper {

    OrderEntity toEntity(Order order);
    Order toDomain(OrderEntity entity);
    OrderResponse toResponse(Order order);
    OrderCreatedEvent toEvent(Order order);
}