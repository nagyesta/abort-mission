package com.github.nagyesta.abortmission.strongback.base;

/**
 * Root of the exception hierarchy used by strongback modules.
 */
public class StrongbackException extends RuntimeException {

    public StrongbackException(final String message) {
        super(message);
    }

    public StrongbackException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public StrongbackException(final Throwable cause) {
        super(cause);
    }
}
