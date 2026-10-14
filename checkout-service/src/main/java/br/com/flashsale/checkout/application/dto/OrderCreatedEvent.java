package br.com.flashsale.checkout.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.UUID;

public record OrderCreatedEvent(
        @JsonProperty("pedido_id") UUID pedido_id,
        @JsonProperty("produto_id") String produto_id,
        @JsonProperty("quantidade") Integer quantidade,
        @JsonProperty("cliente_cpf") String cliente_cpf,
        @JsonProperty("cliente_nome") String cliente_nome,
        @JsonProperty("data_hora_criacao") LocalDateTime data_hora_criacao
) {}