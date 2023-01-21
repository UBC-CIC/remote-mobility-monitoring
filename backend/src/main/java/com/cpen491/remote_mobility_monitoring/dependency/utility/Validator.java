package com.cpen491.remote_mobility_monitoring.dependency.utility;

import com.cpen491.remote_mobility_monitoring.datastore.model.Admin;
import com.cpen491.remote_mobility_monitoring.datastore.model.Organization;
import org.apache.commons.lang3.Validate;

public class Validator {
    public static final String ID_BLANK_ERROR_MESSAGE = "id must be present";
    public static final String NAME_BLANK_ERROR_MESSAGE = "name must be present";
    public static final String EMAIL_BLANK_ERROR_MESSAGE = "email must be present";
    public static final String FIRST_NAME_BLANK_ERROR_MESSAGE = "first_name must be present";
    public static final String LAST_NAME_BLANK_ERROR_MESSAGE = "last_name must be present";
    public static final String ORGANIZATION_ID_BLANK_ERROR_MESSAGE = "organization_id must be present";
    public static final String ORGANIZATION_RECORD_NULL_ERROR_MESSAGE = "Organization record must not be null";
    public static final String ADMIN_RECORD_NULL_ERROR_MESSAGE = "Admin record must not be null";

    public static void validateId(String id) {
        Validate.notBlank(id, ID_BLANK_ERROR_MESSAGE);
    }

    public static void validateName(String name) {
        Validate.notBlank(name, NAME_BLANK_ERROR_MESSAGE);
    }

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

    public static void validateOrganization(Organization organization) {
        Validate.notNull(organization, ORGANIZATION_RECORD_NULL_ERROR_MESSAGE);
        validateName(organization.getName());
    }

    public static void validateAdmin(Admin admin) {
        Validate.notNull(admin, ADMIN_RECORD_NULL_ERROR_MESSAGE);
        validateEmail(admin.getEmail());
        validateFirstName(admin.getFirstName());
        validateLastName(admin.getLastName());
        validateOrganizationId(admin.getOrganizationId());
    }
}
