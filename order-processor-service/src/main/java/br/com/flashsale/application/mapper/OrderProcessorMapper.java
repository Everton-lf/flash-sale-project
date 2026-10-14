package br.com.flashsale.application.mapper;

import br.com.flashsale.domain.model.OrderProcess;
import br.com.flashsale.infrastructure.persistence.OrderProcessEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "jakarta")
public interface OrderProcessorMapper {
    OrderProcessEntity toEntity(OrderProcess domain);
    OrderProcess toDomain(OrderProcessEntity entity);
}