package com.cpen491.remote_mobility_monitoring.datastore.exception;

public class DuplicateRecordException extends RuntimeException {
    private static final String ERROR_MESSAGE_FORMAT = "Record with email [%s] already exists";

    public DuplicateRecordException(String email) {
        super(String.format(ERROR_MESSAGE_FORMAT, email));
    }
}
