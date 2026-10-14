package br.com.flashsale.checkout.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OrderResponse(
        UUID pedido_id,
        String produto_id,
        Integer quantidade,
        String cliente_cpf,
        String cliente_nome,
        String status,
        LocalDateTime data_hora_requisicao,
        LocalDateTime data_hora_atualizacao
) {}
