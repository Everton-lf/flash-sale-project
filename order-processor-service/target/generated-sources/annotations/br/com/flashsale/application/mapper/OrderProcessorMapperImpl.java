package br.com.flashsale.application.mapper;

import br.com.flashsale.domain.model.OrderProcess;
import br.com.flashsale.infrastructure.persistence.OrderProcessEntity;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-14T10:16:16-0300",
    comments = "version: 1.6.0, compiler: javac, environment: Java 21.0.9 (Oracle Corporation)"
)
@Singleton
@Named
public class OrderProcessorMapperImpl implements OrderProcessorMapper {

    @Override
    public OrderProcessEntity toEntity(OrderProcess domain) {
        if ( domain == null ) {
            return null;
        }

        OrderProcessEntity orderProcessEntity = new OrderProcessEntity();

        orderProcessEntity.id = domain.getId();
        orderProcessEntity.produtoId = domain.getProdutoId();
        orderProcessEntity.quantidade = domain.getQuantidade();
        orderProcessEntity.clienteCpf = domain.getClienteCpf();
        orderProcessEntity.clienteNome = domain.getClienteNome();
        orderProcessEntity.status = domain.getStatus();
        orderProcessEntity.dataHoraCriacao = domain.getDataHoraCriacao();
        orderProcessEntity.dataHoraAtualizacao = domain.getDataHoraAtualizacao();

        return orderProcessEntity;
    }

    @Override
    public OrderProcess toDomain(OrderProcessEntity entity) {
        if ( entity == null ) {
            return null;
        }

        OrderProcess orderProcess = new OrderProcess();

        orderProcess.setId( entity.id );
        orderProcess.setProdutoId( entity.produtoId );
        orderProcess.setQuantidade( entity.quantidade );
        orderProcess.setClienteCpf( entity.clienteCpf );
        orderProcess.setClienteNome( entity.clienteNome );
        orderProcess.setStatus( entity.status );
        orderProcess.setDataHoraCriacao( entity.dataHoraCriacao );
        orderProcess.setDataHoraAtualizacao( entity.dataHoraAtualizacao );

        return orderProcess;
    }
}
