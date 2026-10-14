package br.com.flashsale.checkout.domain.model;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Order {
    private UUID id;
    private String produtoId;
    private Integer quantidade;
    private String clienteCpf;
    private String clienteNome;
    private OrderStatus status;
    private LocalDateTime dataHoraCriacao;
    private LocalDateTime dataHoraAtualizacao;

    public static Order createNew(String produtoId, Integer quantidade, String clienteCpf, String clienteNome) {
        Order order = new Order();
        LocalDateTime now = LocalDateTime.now();

        order.setId(UUID.randomUUID());
        order.setProdutoId(produtoId);
        order.setQuantidade(quantidade);
        order.setClienteCpf(clienteCpf);
        order.setClienteNome(clienteNome);
        order.setStatus(OrderStatus.PROCESSING);
        order.setDataHoraCriacao(now);
        order.setDataHoraAtualizacao(now);

        return order;
    }
}
