package com.mouts.order.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DuplicateOrderException extends RuntimeException {

    public DuplicateOrderException(String orderCode) {
        log.warn("Pedido em duplicidade. Código do pedido: " + orderCode + ". Pedido não será enviado para reprocessamento.");
    }
}
