package com.cpen491.remote_mobility_monitoring.dependency.exception;

public class InvalidAuthCodeException extends RuntimeException {
    private static final String ERROR_MESSAGE = "auth_code is invalid";

    public InvalidAuthCodeException() {
        super(ERROR_MESSAGE);
    }
}
