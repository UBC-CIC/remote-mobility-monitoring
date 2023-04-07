package com.cpen491.remote_mobility_monitoring.dependency.exception;

public class InvalidAuthorizationException extends RuntimeException {
    private static final String ERROR_MESSAGE = "invalid authorization";

    public InvalidAuthorizationException() {
        super(ERROR_MESSAGE);
    }
}
