package com.mouts.order.exception;

import org.springframework.http.HttpStatus;

public class DuplicateOrderException extends RuntimeException {

    public DuplicateOrderException(String orderCode) {
        super("Pedido em duplicidade. Código do pedido: " + orderCode + ". Pedido não será enviado para reprocessamento.");
    }
}
