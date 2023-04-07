package com.cpen491.remote_mobility_monitoring.datastore.exception;

public class RecordDoesNotExistException extends RuntimeException {
    private static final String ERROR_MESSAGE_FORMAT = "%s record with ID [%s] does not exist";

    public RecordDoesNotExistException(String className, String id) {
        super(String.format(ERROR_MESSAGE_FORMAT, className, id));
    }
}
