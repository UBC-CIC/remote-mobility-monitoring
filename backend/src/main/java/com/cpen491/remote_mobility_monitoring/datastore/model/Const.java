package com.cpen491.remote_mobility_monitoring.datastore.model;

/**
 * Specifies the table, index, and column names of all models
 */
public final class Const {
    public static class AdminTable {
        public static final String TABLE_NAME = "admin";
        public static final String ID_NAME = "id";
        public static final String EMAIL_NAME = "email";
        public static final String FIRST_NAME_NAME = "first_name";
        public static final String LAST_NAME_NAME = "last_name";
        public static final String ORGANIZATION_ID_NAME = "organization_id";
        public static final String CREATED_AT_NAME = "created_at";
        public static final String UPDATED_AT_NAME = "updated_at";
        public static final String VERSION_NAME = "version";
        public static final String EMAIL_GSI_NAME = EMAIL_NAME + "-gsi";
    }
}
