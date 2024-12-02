package com.mouts.order.exception;

public class DuplicateOrderException extends RuntimeException {

    public DuplicateOrderException(String message) {
        super(message);
    }

    public DuplicateOrderException(String message, Throwable cause) {
        super(message, cause);
    }
}
