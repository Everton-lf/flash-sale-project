package br.com.flashsale.checkout.domain.exceptions;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String productId) {
        super("Estoque insuficiente para o produto " + productId);
    }
}
