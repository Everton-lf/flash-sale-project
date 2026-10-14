package br.com.flashsale.checkout.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID pedido_id,
        String produto_id,
        Integer quantidade,
        String cliente_cpf,
        String cliente_nome,
        LocalDateTime data_hora_criacao
) {}