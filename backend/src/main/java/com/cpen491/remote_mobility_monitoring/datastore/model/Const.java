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
        public static final String PID_NAME = "pid";
        public static final String SID_NAME = "sid";
        public static final String CREATED_AT_NAME = "created_at";
        public static final String UPDATED_AT_NAME = "updated_at";
        public static final String SID_INDEX_NAME = SID_NAME + INDEX_NAME_SUFFIX;
    }

    public static class OrganizationTable extends BaseTable {
        public static final String ID_PREFIX = "org-";
        public static final String NAME_NAME = ID_PREFIX + "name";
        public static final String NAME_INDEX_NAME = NAME_NAME + INDEX_NAME_SUFFIX;
        public static final List<Pair<String, String>> INDEX_NAMES_AND_KEYS = Arrays.asList(
                new ImmutablePair<>(NAME_INDEX_NAME, NAME_NAME)
        );
    }

    public static class AdminTable extends BaseTable {
        public static final String ID_PREFIX = "adm-";
        public static final String EMAIL_NAME = ID_PREFIX + "email";
        public static final String FIRST_NAME_NAME = ID_PREFIX + "first_name";
        public static final String LAST_NAME_NAME = ID_PREFIX + "last_name";
        public static final String EMAIL_INDEX_NAME = EMAIL_NAME + INDEX_NAME_SUFFIX;
        public static final List<Pair<String, String>> INDEX_NAMES_AND_KEYS = Arrays.asList(
                new ImmutablePair<>(EMAIL_INDEX_NAME, EMAIL_NAME)
        );
    }

    public static class CaregiverTable extends BaseTable {
        public static final String ID_PREFIX = "car-";
        public static final String EMAIL_NAME = ID_PREFIX + "email";
        public static final String FIRST_NAME_NAME = ID_PREFIX + "first_name";
        public static final String LAST_NAME_NAME = ID_PREFIX + "last_name";
        public static final String TITLE_NAME = ID_PREFIX + "title";
        public static final String PHONE_NUMBER_NAME = ID_PREFIX + "phone_number";
        public static final String AUTH_CODE_NAME = ID_PREFIX + "auth_code";
        public static final String AUTH_CODE_TIMESTAMP_NAME = ID_PREFIX + "auth_code_timestamp";
        public static final String IS_PRIMARY_NAME = ID_PREFIX + "is_primary";
        public static final String EMAIL_INDEX_NAME = EMAIL_NAME + INDEX_NAME_SUFFIX;
        public static final List<Pair<String, String>> INDEX_NAMES_AND_KEYS = Arrays.asList(
                new ImmutablePair<>(EMAIL_INDEX_NAME, EMAIL_NAME)
        );
    }

    public static class PatientTable extends BaseTable {
        public static final String ID_PREFIX = "pat-";
        public static final String EMAIL_NAME = ID_PREFIX + "email";
        public static final String DEVICE_ID_NAME = ID_PREFIX + "device_id";
        public static final String FIRST_NAME_NAME = ID_PREFIX + "first_name";
        public static final String LAST_NAME_NAME = ID_PREFIX + "last_name";
        public static final String PHONE_NUMBER_NAME = ID_PREFIX + "phone_number";
        public static final String EMAIL_INDEX_NAME = EMAIL_NAME + INDEX_NAME_SUFFIX;
        public static final String DEVICE_ID_INDEX_NAME = DEVICE_ID_NAME + INDEX_NAME_SUFFIX;
        public static final List<Pair<String, String>> INDEX_NAMES_AND_KEYS = Arrays.asList(
                new ImmutablePair<>(EMAIL_INDEX_NAME, EMAIL_NAME),
                new ImmutablePair<>(DEVICE_ID_INDEX_NAME, DEVICE_ID_NAME)
        );
    }

    public static class MetricsTable {
        public static final String PATIENT_ID_NAME = "patient_id";
        public static final String DEVICE_ID_NAME = "device_id";
        public static final String MEASURE_NAME_NAME = "measure_name";
        public static final String MEASURE_VALUE_NAME = "measure_value";
        public static final String TIME_NAME = "time";
        public static final String STEP_LENGTH_NAME = "step_length";
        public static final String DOUBLE_SUPPORT_TIME_NAME = "double_support_time";
        public static final String WALKING_SPEED_NAME = "walking_speed";
        public static final String WALKING_ASYMMETRY_NAME = "walking_asymmetry";
        public static final String DISTANCE_WALKED_NAME = "distance_walked";
        public static final String TIMESTAMP_NAME = "timestamp";
    }
}
