package com.cpen491.remote_mobility_monitoring.datastore.model;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;

/**
 * Specifies the table, index, and column names of all models
 */
public final class Const {
    private static final String INDEX_NAME_SUFFIX = "-gsi";

    public static class BaseTable {
        public static final String ID_NAME = "id";
        public static final String CREATED_AT_NAME = "created_at";
        public static final String UPDATED_AT_NAME = "updated_at";
    }

    public static class OrganizationTable extends BaseTable {
        public static final String TABLE_NAME = "organization";
        public static final String NAME_NAME = "name";
        public static final String NAME_INDEX_NAME = NAME_NAME + INDEX_NAME_SUFFIX;
        public static final List<Pair<String, String>> INDEX_NAMES_AND_KEYS = Arrays.asList(
                new ImmutablePair<>(NAME_INDEX_NAME, NAME_NAME)
        );
    }

    public static class AdminTable extends BaseTable {
        public static final String TABLE_NAME = "admin";
        public static final String EMAIL_NAME = "email";
        public static final String FIRST_NAME_NAME = "first_name";
        public static final String LAST_NAME_NAME = "last_name";
        public static final String ORGANIZATION_ID_NAME = "organization_id";
        public static final String EMAIL_INDEX_NAME = EMAIL_NAME + INDEX_NAME_SUFFIX;
        public static final String ORGANIZATION_ID_INDEX_NAME = ORGANIZATION_ID_NAME + INDEX_NAME_SUFFIX;
        public static final List<Pair<String, String>> INDEX_NAMES_AND_KEYS = Arrays.asList(
                new ImmutablePair<>(EMAIL_INDEX_NAME, EMAIL_NAME),
                new ImmutablePair<>(ORGANIZATION_ID_INDEX_NAME, ORGANIZATION_ID_NAME)
        );
    }

    public static class CaregiverTable extends BaseTable {
        public static final String TABLE_NAME = "caregiver";
        public static final String EMAIL_NAME = "email";
        public static final String FIRST_NAME_NAME = "first_name";
        public static final String LAST_NAME_NAME = "last_name";
        public static final String TITLE_NAME = "title";
        public static final String PHONE_NUMBER_NAME = "phone_number";
        public static final String IMAGE_URL_NAME = "image_url";
        public static final String ORGANIZATION_ID_NAME = "organization_id";
        public static final String EMAIL_INDEX_NAME = EMAIL_NAME + INDEX_NAME_SUFFIX;
        public static final String ORGANIZATION_ID_INDEX_NAME = ORGANIZATION_ID_NAME + INDEX_NAME_SUFFIX;
        public static final List<Pair<String, String>> INDEX_NAMES_AND_KEYS = Arrays.asList(
                new ImmutablePair<>(EMAIL_INDEX_NAME, EMAIL_NAME),
                new ImmutablePair<>(ORGANIZATION_ID_INDEX_NAME, ORGANIZATION_ID_NAME)
        );
    }
}
