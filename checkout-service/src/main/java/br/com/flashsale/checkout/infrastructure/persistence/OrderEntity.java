package br.com.flashsale.checkout.infrastructure.persistence;

import br.com.flashsale.checkout.domain.model.OrderStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_checkout_orders")
public class OrderEntity {

    @Id
    @Column(name = "id", nullable = false)
    public UUID id;

    @Column(name = "produto_id", nullable = false)
    public String produtoId;

    @Column(name = "quantidade", nullable = false)
    public Integer quantidade;

    @Column(name = "cliente_cpf", nullable = false)
    public String clienteCpf;

    @Column(name = "cliente_nome", nullable = false)
    public String clienteNome;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    public OrderStatus status;

    @Column(name = "data_hora_criacao", nullable = false)
    public LocalDateTime dataHoraCriacao;

    @Column(name = "data_hora_atualizacao", nullable = false)
    public LocalDateTime dataHoraAtualizacao;
}