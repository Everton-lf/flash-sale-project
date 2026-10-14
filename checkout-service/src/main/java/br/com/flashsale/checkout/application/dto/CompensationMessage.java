package br.com.flashsale.checkout.application.dto;

import java.util.UUID;

public record CompensationMessage(
        UUID pedido_id,
        String produto_id,
        Integer quantidade,
        String motivo
) {}