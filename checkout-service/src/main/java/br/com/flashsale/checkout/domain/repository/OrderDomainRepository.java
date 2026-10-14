package br.com.flashsale.checkout.domain.repository;

import br.com.flashsale.checkout.domain.model.Order;

import java.util.Optional;
import java.util.UUID;

public interface OrderDomainRepository {
    void save(Order order);
    Optional<Order> findDomainById(UUID id);
}
