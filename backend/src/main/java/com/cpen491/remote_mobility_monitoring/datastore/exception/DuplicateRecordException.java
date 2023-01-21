package com.cpen491.remote_mobility_monitoring.datastore.exception;

public class DuplicateRecordException extends RuntimeException {
    private static final String ERROR_MESSAGE_FORMAT = "%s record %s already exists";

    public DuplicateRecordException(String className, String key) {
        super(String.format(ERROR_MESSAGE_FORMAT, className, key));
    }
}
