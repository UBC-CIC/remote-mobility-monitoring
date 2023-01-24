package com.cpen491.remote_mobility_monitoring.dependency.utility;

import com.cpen491.remote_mobility_monitoring.datastore.model.Admin;
import com.cpen491.remote_mobility_monitoring.datastore.model.Caregiver;
import com.cpen491.remote_mobility_monitoring.datastore.model.Organization;
import com.cpen491.remote_mobility_monitoring.datastore.model.Patient;
import org.apache.commons.lang3.Validate;

import java.util.Set;

public class Validator {
    public static final String ID_BLANK_ERROR_MESSAGE = "id must be present";
    public static final String NAME_BLANK_ERROR_MESSAGE = "name must be present";
    public static final String EMAIL_BLANK_ERROR_MESSAGE = "email must be present";
    public static final String FIRST_NAME_BLANK_ERROR_MESSAGE = "first_name must be present";
    public static final String LAST_NAME_BLANK_ERROR_MESSAGE = "last_name must be present";
    public static final String TITLE_BLANK_ERROR_MESSAGE = "title must be present";
    public static final String PHONE_NUMBER_BLANK_ERROR_MESSAGE = "phone_number must be present";
    public static final String IMAGE_URL_BLANK_ERROR_MESSAGE = "image_url must be present";
    public static final String ORGANIZATION_ID_BLANK_ERROR_MESSAGE = "organization_id must be present";
    public static final String DEVICE_ID_BLANK_ERROR_MESSAGE = "device_id must be present";
    public static final String DATE_OF_BIRTH_BLANK_ERROR_MESSAGE = "date_of_birth must be present";
    public static final String IDS_NULL_ERROR_MESSAGE = "IDs must not be null";
    public static final String ORGANIZATION_RECORD_NULL_ERROR_MESSAGE = "Organization record must not be null";
    public static final String ADMIN_RECORD_NULL_ERROR_MESSAGE = "Admin record must not be null";
    public static final String CAREGIVER_RECORD_NULL_ERROR_MESSAGE = "Caregiver record must not be null";
    public static final String PATIENT_RECORD_NULL_ERROR_MESSAGE = "Patient record must not be null";

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

    public static void validateTitle(String title) {
        Validate.notBlank(title, TITLE_BLANK_ERROR_MESSAGE);
    }

    public static void validatePhoneNumber(String phoneNumber) {
        Validate.notBlank(phoneNumber, PHONE_NUMBER_BLANK_ERROR_MESSAGE);
    }

    public static void validateImageUrl(String imageUrl) {
        Validate.notBlank(imageUrl, IMAGE_URL_BLANK_ERROR_MESSAGE);
    }

    public static void validateOrganizationId(String organizationId) {
        Validate.notBlank(organizationId, ORGANIZATION_ID_BLANK_ERROR_MESSAGE);
    }

    public static void validateDeviceId(String deviceId) {
        Validate.notBlank(deviceId, DEVICE_ID_BLANK_ERROR_MESSAGE);
    }

    public static void validateDateOfBirth(String dateOfBirth) {
        Validate.notBlank(dateOfBirth, DATE_OF_BIRTH_BLANK_ERROR_MESSAGE);
    }

    public static void validateIds(Set<String> ids) {
        Validate.notNull(ids, IDS_NULL_ERROR_MESSAGE);
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

    public static void validateCaregiver(Caregiver caregiver) {
        Validate.notNull(caregiver, CAREGIVER_RECORD_NULL_ERROR_MESSAGE);
        validateEmail(caregiver.getEmail());
        validateFirstName(caregiver.getFirstName());
        validateLastName(caregiver.getLastName());
        validateTitle(caregiver.getTitle());
        validatePhoneNumber(caregiver.getPhoneNumber());
        validateImageUrl(caregiver.getImageUrl());
        validateOrganizationId(caregiver.getOrganizationId());
    }

    public static void validatePatient(Patient patient) {
        Validate.notNull(patient, PATIENT_RECORD_NULL_ERROR_MESSAGE);
        validateFirstName(patient.getFirstName());
        validateLastName(patient.getLastName());
        validateDateOfBirth(patient.getDateOfBirth());
        validatePhoneNumber(patient.getPhoneNumber());
    }
}
