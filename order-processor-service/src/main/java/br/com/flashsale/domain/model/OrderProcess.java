package br.com.flashsale.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class OrderProcess {
    private UUID id;
    private String produtoId;
    private Integer quantidade;
    private String clienteCpf;
    private String clienteNome;
    private OrderStatus status;
    private LocalDateTime dataHoraCriacao;
    private LocalDateTime dataHoraAtualizacao;

    public OrderProcess() {}

    public OrderProcess(UUID id, String produtoId, Integer quantidade, String clienteCpf, String clienteNome, OrderStatus status, LocalDateTime dataHoraCriacao, LocalDateTime dataHoraAtualizacao) {
        this.id = id;
        this.produtoId = produtoId;
        this.quantidade = quantidade;
        this.clienteCpf = clienteCpf;
        this.clienteNome = clienteNome;
        this.status = status;
        this.dataHoraCriacao = dataHoraCriacao;
        this.dataHoraAtualizacao = dataHoraAtualizacao;
    }

    public static OrderProcess createFromEvent(UUID id, String produtoId, Integer quantidade, String clienteCpf, String clienteNome, LocalDateTime dataHoraCriacao) {
        LocalDateTime now = LocalDateTime.now();
        return new OrderProcess(id, produtoId, quantidade, clienteCpf, clienteNome, OrderStatus.CONFIRMED, dataHoraCriacao, now);
    }

    public void markAsCancelled() {
        this.status = OrderStatus.CANCELLED;
        this.dataHoraAtualizacao = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getProdutoId() { return produtoId; }
    public void setProdutoId(String produtoId) { this.produtoId = produtoId; }
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
    public String getClienteCpf() { return clienteCpf; }
    public void setClienteCpf(String clienteCpf) { this.clienteCpf = clienteCpf; }
    public String getClienteNome() { return clienteNome; }
    public void setClienteNome(String clienteNome) { this.clienteNome = clienteNome; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public LocalDateTime getDataHoraCriacao() { return dataHoraCriacao; }
    public void setDataHoraCriacao(LocalDateTime dataHoraCriacao) { this.dataHoraCriacao = dataHoraCriacao; }
    public LocalDateTime getDataHoraAtualizacao() { return dataHoraAtualizacao; }
    public void setDataHoraAtualizacao(LocalDateTime dataHoraAtualizacao) { this.dataHoraAtualizacao = dataHoraAtualizacao; }
}