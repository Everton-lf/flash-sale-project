package br.com.flashsale.checkout.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(
        @NotBlank(message = "O campo produto_id e obrigatorio")
        String produto_id,

        @NotNull(message = "A quantidade e obrigatoria")
        @Min(value = 1, message = "A quantidade deve ser maior que zero")
        Integer quantidade,

        @NotBlank(message = "O CPF do cliente e obrigatorio")
        String cliente_cpf,

        @NotBlank(message = "O nome do cliente e obrigatorio")
        String cliente_nome
) {}
