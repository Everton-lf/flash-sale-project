package br.com.flashsale.application.dto;

import java.util.UUID;

public record CompensationMessage(
        UUID pedido_id,
        String produto_id,
        Integer quantidade,
        String motivo
) {}