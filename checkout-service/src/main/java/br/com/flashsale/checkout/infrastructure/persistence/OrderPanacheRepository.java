package br.com.flashsale.checkout.infrastructure.persistence;


import br.com.flashsale.checkout.application.mapper.OrderMapper;
import br.com.flashsale.checkout.domain.model.Order;
import br.com.flashsale.checkout.domain.repository.OrderDomainRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class OrderPanacheRepository implements PanacheRepositoryBase<OrderEntity, UUID>, OrderDomainRepository {

    @Inject
    OrderMapper mapper;

    @Override
    public void save(Order order) {
        OrderEntity entity = mapper.toEntity(order);
        persist(entity);
    }

    @Override
    public Optional<Order> findDomainById(UUID id) {
        return findByIdOptional(id).map(mapper::toDomain);
    }
}