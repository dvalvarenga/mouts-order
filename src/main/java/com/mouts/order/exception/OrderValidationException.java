package com.mouts.order.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrderValidationException extends RuntimeException {

    public OrderValidationException(String message) {
        log.warn(message);
    }

    public OrderValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
