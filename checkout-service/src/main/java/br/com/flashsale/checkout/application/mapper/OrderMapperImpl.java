package br.com.flashsale.checkout.application.mapper;

import br.com.flashsale.checkout.application.dto.OrderCreatedEvent;
import br.com.flashsale.checkout.application.dto.OrderResponse;
import br.com.flashsale.checkout.domain.model.Order;
import br.com.flashsale.checkout.infrastructure.persistence.OrderEntity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderMapperImpl implements OrderMapper {

    @Override
    public OrderEntity toEntity(Order order) {
        if (order == null) {
            return null;
        }

        OrderEntity entity = new OrderEntity();
        entity.id = order.getId();
        entity.produtoId = order.getProdutoId();
        entity.quantidade = order.getQuantidade();
        entity.clienteCpf = order.getClienteCpf();
        entity.clienteNome = order.getClienteNome();
        entity.status = order.getStatus();
        entity.dataHoraCriacao = order.getDataHoraCriacao();
        entity.dataHoraAtualizacao = order.getDataHoraAtualizacao();
        return entity;
    }

    @Override
    public Order toDomain(OrderEntity entity) {
        if (entity == null) {
            return null;
        }

        Order order = new Order();
        order.setId(entity.id);
        order.setProdutoId(entity.produtoId);
        order.setQuantidade(entity.quantidade);
        order.setClienteCpf(entity.clienteCpf);
        order.setClienteNome(entity.clienteNome);
        order.setStatus(entity.status);
        order.setDataHoraCriacao(entity.dataHoraCriacao);
        order.setDataHoraAtualizacao(entity.dataHoraAtualizacao);
        return order;
    }

    @Override
    public OrderResponse toResponse(Order order) {
        if (order == null) {
            return null;
        }

        return new OrderResponse(
                order.getId(),
                order.getProdutoId(),
                order.getQuantidade(),
                order.getClienteCpf(),
                order.getClienteNome(),
                order.getStatus() != null ? order.getStatus().name() : null,
                order.getDataHoraCriacao(),
                order.getDataHoraAtualizacao()
        );
    }

    @Override
    public OrderCreatedEvent toEvent(Order order) {
        if (order == null) {
            return null;
        }

        return new OrderCreatedEvent(
                order.getId(),
                order.getProdutoId(),
                order.getQuantidade(),
                order.getClienteCpf(),
                order.getClienteNome(),
                order.getDataHoraCriacao()
        );
    }
}
