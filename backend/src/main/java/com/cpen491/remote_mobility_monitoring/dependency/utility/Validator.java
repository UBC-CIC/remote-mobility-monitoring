package com.cpen491.remote_mobility_monitoring.dependency.utility;

import com.cpen491.remote_mobility_monitoring.datastore.model.Admin;
import org.apache.commons.lang3.Validate;

public class Validator {
    public static final String EMAIL_BLANK_ERROR_MESSAGE = "email must be present";
    public static final String FIRST_NAME_BLANK_ERROR_MESSAGE = "first_name must be present";
    public static final String LAST_NAME_BLANK_ERROR_MESSAGE = "last_name must be present";
    public static final String ORGANIZATION_ID_BLANK_ERROR_MESSAGE = "organization_id must be present";
    public static final String ADMIN_RECORD_NULL_ERROR_MESSAGE = "Admin record must not be null";

    public static void validateEmail(String email) {
        Validate.notBlank(email, EMAIL_BLANK_ERROR_MESSAGE);
    }

    public static void validateFirstName(String firstName) {
        Validate.notBlank(firstName, FIRST_NAME_BLANK_ERROR_MESSAGE);
    }

    public static void validateLastName(String lastName) {
        Validate.notBlank(lastName, LAST_NAME_BLANK_ERROR_MESSAGE);
    }

    public static void validateOrganizationId(String organizationId) {
        Validate.notBlank(organizationId, ORGANIZATION_ID_BLANK_ERROR_MESSAGE);
    }

    public static void validateAdmin(Admin admin) {
        Validate.notNull(admin, ADMIN_RECORD_NULL_ERROR_MESSAGE);
        validateEmail(admin.getEmail());
        validateFirstName(admin.getFirstName());
        validateLastName(admin.getLastName());
        validateOrganizationId(admin.getOrganizationId());
    }
}
