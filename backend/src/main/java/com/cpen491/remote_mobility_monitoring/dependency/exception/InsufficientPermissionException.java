package com.cpen491.remote_mobility_monitoring.dependency.exception;

public class InsufficientPermissionException extends RuntimeException {
    private static final String ERROR_MESSAGE = "insufficient permission to perform action";

    public InsufficientPermissionException() {
        super(ERROR_MESSAGE);
    }
}
