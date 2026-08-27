package com.acquaintech.demo.polyglot.exception;

public class QuantityOperationFailedException extends RuntimeException {

    public QuantityOperationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
