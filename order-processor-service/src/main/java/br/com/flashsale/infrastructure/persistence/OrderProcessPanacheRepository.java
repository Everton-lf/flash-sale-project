package br.com.flashsale.infrastructure.persistence;

import br.com.flashsale.application.mapper.OrderProcessorMapper;
import br.com.flashsale.domain.model.OrderProcess;
import br.com.flashsale.domain.repository.OrderProcessorDomainRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class OrderProcessPanacheRepository implements PanacheRepositoryBase<OrderProcessEntity, UUID>, OrderProcessorDomainRepository {

    @Inject
    OrderProcessorMapper mapper;

    @Override
    public void save(OrderProcess orderProcess) {
        OrderProcessEntity entity = mapper.toEntity(orderProcess);
        getEntityManager().merge(entity);
    }

    @Override
    public Optional<OrderProcess> findDomainById(UUID id) {
        return findByIdOptional(id).map(mapper::toDomain);
    }
}