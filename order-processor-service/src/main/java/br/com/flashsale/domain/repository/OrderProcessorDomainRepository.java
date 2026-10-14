package br.com.flashsale.domain.repository;

import br.com.flashsale.domain.model.OrderProcess;
import java.util.Optional;
import java.util.UUID;

public interface OrderProcessorDomainRepository {
    void save(OrderProcess orderProcess);
    Optional<OrderProcess> findDomainById(UUID id);
}