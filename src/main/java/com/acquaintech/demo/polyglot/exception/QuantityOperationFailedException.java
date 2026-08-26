package com.acquaintech.demo.polyglot.exception;

/**
 * Thrown when the MySQL-side quantity write/delete keeps failing after the
 * configured retry attempts are exhausted (see @Recover methods in QuantityService).
 */
public class QuantityOperationFailedException extends RuntimeException {

    public QuantityOperationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
