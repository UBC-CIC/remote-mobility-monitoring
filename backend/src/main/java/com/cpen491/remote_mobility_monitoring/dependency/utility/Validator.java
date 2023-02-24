package com.cpen491.remote_mobility_monitoring.dependency.utility;

import com.cpen491.remote_mobility_monitoring.datastore.model.Admin;
import com.cpen491.remote_mobility_monitoring.datastore.model.Caregiver;
import com.cpen491.remote_mobility_monitoring.datastore.model.Metrics;
import com.cpen491.remote_mobility_monitoring.datastore.model.Metrics.MeasureName;
import com.cpen491.remote_mobility_monitoring.datastore.model.Organization;
import com.cpen491.remote_mobility_monitoring.datastore.model.Patient;
import com.cpen491.remote_mobility_monitoring.function.schema.admin.CreateAdminRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.admin.DeleteAdminRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.admin.GetAdminRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.AddPatientRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.CreateCaregiverRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.DeleteCaregiverRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.GetAllPatientsRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.GetCaregiverRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.RemovePatientRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.UpdateCaregiverRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.organization.CreateOrganizationRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.organization.GetOrganizationRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.AddMetricsRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.AddMetricsRequestBody.AddMetricsSerialization;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.CreatePatientRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.DeletePatientRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.GetAllCaregiversRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.GetPatientRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.QueryMetricsRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.UpdatePatientDeviceRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.UpdatePatientRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.VerifyPatientRequestBody;
import org.apache.commons.lang3.Validate;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.AdminTable;
import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.CaregiverTable;
import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.OrganizationTable;
import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.PatientTable;
import static com.cpen491.remote_mobility_monitoring.dependency.auth.CognitoWrapper.ADMIN_GROUP_NAME;
import static com.cpen491.remote_mobility_monitoring.dependency.auth.CognitoWrapper.CAREGIVER_GROUP_NAME;

public class Validator {
    public static final String PID_BLANK_ERROR_MESSAGE = "pid must be present";
    public static final String SID_BLANK_ERROR_MESSAGE = "sid must be present";
    public static final String PID_NOT_EQUAL_SID_ERROR_MESSAGE = "pid must be the same as sid";
    public static final String NAME_BLANK_ERROR_MESSAGE = "name must be present";
    public static final String EMAIL_BLANK_ERROR_MESSAGE = "email must be present";
    public static final String FIRST_NAME_BLANK_ERROR_MESSAGE = "first_name must be present";
    public static final String LAST_NAME_BLANK_ERROR_MESSAGE = "last_name must be present";
    public static final String TITLE_BLANK_ERROR_MESSAGE = "title must be present";
    public static final String PHONE_NUMBER_BLANK_ERROR_MESSAGE = "phone_number must be present";
    public static final String ORGANIZATION_ID_BLANK_ERROR_MESSAGE = "organization_id must be present";
    public static final String ORGANIZATION_ID_INVALID_ERROR_MESSAGE = "organization_id invalid";
    public static final String DEVICE_ID_BLANK_ERROR_MESSAGE = "device_id must be present";
    public static final String CAREGIVER_ID_BLANK_ERROR_MESSAGE = "caregiver_id must be present";
    public static final String CAREGIVER_ID_INVALID_ERROR_MESSAGE = "caregiver_id invalid";
    public static final String PATIENT_ID_BLANK_ERROR_MESSAGE = "patient_id must be present";
    public static final String PATIENT_ID_INVALID_ERROR_MESSAGE = "patient_id invalid";
    public static final String ADMIN_ID_BLANK_ERROR_MESSAGE = "admin_id must be present";
    public static final String ADMIN_ID_INVALID_ERROR_MESSAGE = "admin_id invalid";
    public static final String AUTH_CODE_BLANK_ERROR_MESSAGE = "auth_code must be present";
    public static final String AUTH_CODE_TIMESTAMP_BLANK_ERROR_MESSAGE = "auth_code_timestamp must be present";
    public static final String MEASURE_NAME_NULL_ERROR_MESSAGE = "measure_name must not be null";
    public static final String MEASURE_VALUE_BLANK_ERROR_MESSAGE = "measure_value must be present";
    public static final String MEASURE_VALUE_INVALID_ERROR_MESSAGE = "measure_value is not a double";
    public static final String STEP_LENGTH_BLANK_ERROR_MESSAGE = "step_length must be present";
    public static final String STEP_LENGTH_INVALID_ERROR_MESSAGE = "step_length is not a double";
    public static final String DOUBLE_SUPPORT_TIME_BLANK_ERROR_MESSAGE = "double_support_time must be present";
    public static final String DOUBLE_SUPPORT_TIME_INVALID_ERROR_MESSAGE = "double_support_time is not a double";
    public static final String WALKING_SPEED_BLANK_ERROR_MESSAGE = "walking_speed must be present";
    public static final String WALKING_SPEED_INVALID_ERROR_MESSAGE = "walking_speed is not a double";
    public static final String WALKING_ASYMMETRY_BLANK_ERROR_MESSAGE = "walking_asymmetry must be present";
    public static final String WALKING_ASYMMETRY_INVALID_ERROR_MESSAGE = "walking_asymmetry is not a double";
    public static final String DISTANCE_WALKED_BLANK_ERROR_MESSAGE = "distance_walked must be present";
    public static final String DISTANCE_WALKED_INVALID_ERROR_MESSAGE = "distance_walked is not a double";
    public static final String TIMESTAMP_BLANK_ERROR_MESSAGE = "timestamp must be present";
    public static final String TIMESTAMP_INVALID_ERROR_MESSAGE = "timestamp is not an in iso8601 format";
    public static final String VERIFIED_NULL_ERROR_MESSAGE = "verified must not be null";
    public static final String GROUP_NAME_BLANK_ERROR_MESSAGE = "group name must be present";
    public static final List<String> VALID_GROUP_NAMES = Arrays.asList(ADMIN_GROUP_NAME, CAREGIVER_GROUP_NAME);
    public static final String GROUP_NAME_INVALID_ERROR_MESSAGE = "group name is not Admin or Caregiver";
    public static final String IDS_NULL_ERROR_MESSAGE = "IDs must not be null";
    public static final String ORGANIZATION_RECORD_NULL_ERROR_MESSAGE = "Organization record must not be null";
    public static final String ADMIN_RECORD_NULL_ERROR_MESSAGE = "Admin record must not be null";
    public static final String CAREGIVER_RECORD_NULL_ERROR_MESSAGE = "Caregiver record must not be null";
    public static final String PATIENT_RECORD_NULL_ERROR_MESSAGE = "Patient record must not be null";
    public static final String METRICS_LIST_NULL_ERROR_MESSAGE = "Metrics list must not be null";
    public static final String METRICS_NULL_ERROR_MESSAGE = "Metrics must not be null";
    public static final String CREATE_ORGANIZATION_NULL_ERROR_MESSAGE = "Create organization request body must not be null";
    public static final String GET_ORGANIZATION_NULL_ERROR_MESSAGE = "Get organization request body must not be null";
    public static final String CREATE_ADMIN_NULL_ERROR_MESSAGE = "Create admin request body must not be null";
    public static final String GET_ADMIN_NULL_ERROR_MESSAGE = "Get admin request body must not be null";
    public static final String DELETE_ADMIN_NULL_ERROR_MESSAGE = "Delete admin request body must not be null";
    public static final String CREATE_CAREGIVER_NULL_ERROR_MESSAGE = "Create caregiver request body must not be null";
    public static final String ADD_PATIENT_NULL_ERROR_MESSAGE = "Add patient request body must not be null";
    public static final String REMOVE_PATIENT_NULL_ERROR_MESSAGE = "Remove patient request body must not be null";
    public static final String GET_CAREGIVER_NULL_ERROR_MESSAGE = "Get caregiver request body must not be null";
    public static final String GET_ALL_PATIENTS_NULL_ERROR_MESSAGE = "Get all patients request body must not be null";
    public static final String UPDATE_CAREGIVER_NULL_ERROR_MESSAGE = "Update caregiver request body must not be null";
    public static final String DELETE_CAREGIVER_NULL_ERROR_MESSAGE = "Delete caregiver request body must not be null";
    public static final String CREATE_PATIENT_NULL_ERROR_MESSAGE = "Create patient request body must not be null";
    public static final String UPDATE_PATIENT_DEVICE_NULL_ERROR_MESSAGE = "Update patient device request body must not be null";
    public static final String VERIFY_PATIENT_NULL_ERROR_MESSAGE = "Verify patient request body must not be null";
    public static final String GET_PATIENT_NULL_ERROR_MESSAGE = "Get patient request body must not be null";
    public static final String GET_ALL_CAREGIVERS_NULL_ERROR_MESSAGE = "Get all caregivers request body must not be null";
    public static final String ADD_METRICS_NULL_ERROR_MESSAGE = "Add metrics request body must not be null";
    public static final String QUERY_METRICS_NULL_ERROR_MESSAGE = "Query metrics request body must not be null";
    public static final String UPDATE_PATIENT_NULL_ERROR_MESSAGE = "Update patient request body must not be null";
    public static final String DELETE_PATIENT_NULL_ERROR_MESSAGE = "Delete patient request body must not be null";

    public static void validatePidEqualsSid(String pid, String sid) {
        Validate.notBlank(pid, PID_BLANK_ERROR_MESSAGE);
        Validate.notBlank(sid, SID_BLANK_ERROR_MESSAGE);
        if (!pid.equals(sid)) {
            throw new IllegalArgumentException(PID_NOT_EQUAL_SID_ERROR_MESSAGE);
        }
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

    public static void validateOrganizationId(String organizationId) {
        Validate.notBlank(organizationId, ORGANIZATION_ID_BLANK_ERROR_MESSAGE);
        if (!organizationId.startsWith(OrganizationTable.ID_PREFIX)) {
            throw new IllegalArgumentException(ORGANIZATION_ID_INVALID_ERROR_MESSAGE);
        }
    }

    public static void validateDeviceId(String deviceId) {
        Validate.notBlank(deviceId, DEVICE_ID_BLANK_ERROR_MESSAGE);
    }

    public static void validateCaregiverId(String caregiverId) {
        Validate.notBlank(caregiverId, CAREGIVER_ID_BLANK_ERROR_MESSAGE);
        if (!caregiverId.startsWith(CaregiverTable.ID_PREFIX)) {
            throw new IllegalArgumentException(CAREGIVER_ID_INVALID_ERROR_MESSAGE);
        }
    }

    public static void validatePatientId(String patientId) {
        Validate.notBlank(patientId, PATIENT_ID_BLANK_ERROR_MESSAGE);
        if (!patientId.startsWith(PatientTable.ID_PREFIX)) {
            throw new IllegalArgumentException(PATIENT_ID_INVALID_ERROR_MESSAGE);
        }
    }

    public static void validateAdminId(String adminId) {
        Validate.notBlank(adminId, ADMIN_ID_BLANK_ERROR_MESSAGE);
        if (!adminId.startsWith(AdminTable.ID_PREFIX)) {
            throw new IllegalArgumentException(ADMIN_ID_INVALID_ERROR_MESSAGE);
        }
    }

    public static void validateAuthCode(String authCode) {
        Validate.notBlank(authCode, AUTH_CODE_BLANK_ERROR_MESSAGE);
    }

    public static void validateAuthCodeTimestamp(String authCodeTimestamp) {
        Validate.notBlank(authCodeTimestamp, AUTH_CODE_TIMESTAMP_BLANK_ERROR_MESSAGE);
    }

    public static void validateMeasureName(MeasureName measureName) {
        Validate.notNull(measureName, MEASURE_NAME_NULL_ERROR_MESSAGE);
    }

    public static void validateMeasureValue(String measureValue) {
        validateMetricsValue(measureValue, MEASURE_VALUE_BLANK_ERROR_MESSAGE, MEASURE_VALUE_INVALID_ERROR_MESSAGE);
    }

    public static void validateStepLength(String stepLength) {
        validateMetricsValue(stepLength, STEP_LENGTH_BLANK_ERROR_MESSAGE, STEP_LENGTH_INVALID_ERROR_MESSAGE);
    }

    public static void validateDoubleSupportTime(String doubleSupportTime) {
        validateMetricsValue(doubleSupportTime, DOUBLE_SUPPORT_TIME_BLANK_ERROR_MESSAGE, DOUBLE_SUPPORT_TIME_INVALID_ERROR_MESSAGE);
    }

    public static void validateWalkingSpeed(String walkingSpeed) {
        validateMetricsValue(walkingSpeed, WALKING_SPEED_BLANK_ERROR_MESSAGE, WALKING_SPEED_INVALID_ERROR_MESSAGE);
    }

    public static void validateWalkingAsymmetry(String walkingAsymmetry) {
        validateMetricsValue(walkingAsymmetry, WALKING_ASYMMETRY_BLANK_ERROR_MESSAGE, WALKING_ASYMMETRY_INVALID_ERROR_MESSAGE);
    }

    public static void validateDistanceWalked(String distanceWalked) {
        validateMetricsValue(distanceWalked, DISTANCE_WALKED_BLANK_ERROR_MESSAGE, DISTANCE_WALKED_INVALID_ERROR_MESSAGE);
    }

    private static void validateMetricsValue(String metricsValue, String blankErrorMessage, String invalidErrorMessage) {
        Validate.notBlank(metricsValue, blankErrorMessage);
        try {
            Double.parseDouble(metricsValue);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(invalidErrorMessage);
        }
    }

    public static void validateTimestamp(String timestamp) {
        Validate.notBlank(timestamp, TIMESTAMP_BLANK_ERROR_MESSAGE);
        try {
            LocalDateTime.parse(timestamp);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(TIMESTAMP_INVALID_ERROR_MESSAGE);
        }
    }

    public static void validateVerified(Boolean verified) {
        Validate.notNull(verified, VERIFIED_NULL_ERROR_MESSAGE);
    }

    public static void validateGroupName(String groupName) {
        Validate.notBlank(groupName, GROUP_NAME_BLANK_ERROR_MESSAGE);
        if (!VALID_GROUP_NAMES.contains(groupName)) {
            throw new IllegalArgumentException(GROUP_NAME_INVALID_ERROR_MESSAGE);
        }
    }

    public static void validateIds(List<String> ids) {
        Validate.notNull(ids, IDS_NULL_ERROR_MESSAGE);
    }

    public static void validateOrganization(Organization organization) {
        Validate.notNull(organization, ORGANIZATION_RECORD_NULL_ERROR_MESSAGE);
        validateName(organization.getName());
    }

    public static void validateAdmin(Admin admin) {
        Validate.notNull(admin, ADMIN_RECORD_NULL_ERROR_MESSAGE);
        validatePidEqualsSid(admin.getPid(), admin.getSid());
        validateAdminId(admin.getPid());
        validateEmail(admin.getEmail());
        validateFirstName(admin.getFirstName());
        validateLastName(admin.getLastName());
    }

    public static void validateCaregiver(Caregiver caregiver) {
        Validate.notNull(caregiver, CAREGIVER_RECORD_NULL_ERROR_MESSAGE);
        validatePidEqualsSid(caregiver.getPid(), caregiver.getSid());
        validateCaregiverId(caregiver.getPid());
        validateEmail(caregiver.getEmail());
        validateFirstName(caregiver.getFirstName());
        validateLastName(caregiver.getLastName());
        validateTitle(caregiver.getTitle());
        validatePhoneNumber(caregiver.getPhoneNumber());
    }

    public static void validatePatient(Patient patient) {
        Validate.notNull(patient, PATIENT_RECORD_NULL_ERROR_MESSAGE);
        validatePidEqualsSid(patient.getPid(), patient.getSid());
        validatePatientId(patient.getPid());
        validateEmail(patient.getEmail());
        validateFirstName(patient.getFirstName());
        validateLastName(patient.getLastName());
        validatePhoneNumber(patient.getPhoneNumber());
    }

    public static void validateMetricsList(List<Metrics> metrics) {
        Validate.notNull(metrics, METRICS_LIST_NULL_ERROR_MESSAGE);
    }

    public static void validateMetrics(Metrics metrics) {
        Validate.notNull(metrics, METRICS_NULL_ERROR_MESSAGE);
        validatePatientId(metrics.getPatientId());
        validateDeviceId(metrics.getDeviceId());
        validateMeasureName(metrics.getMeasureName());
        validateMeasureValue(metrics.getMeasureValue());
        validateTimestamp(metrics.getTimestamp());
    }

    public static void validateAddMetricsSerialization(AddMetricsSerialization metrics) {
        Validate.notNull(metrics, METRICS_NULL_ERROR_MESSAGE);
        validateStepLength(metrics.getStepLength());
        validateDoubleSupportTime(metrics.getDoubleSupportTime());
        validateWalkingSpeed(metrics.getWalkingSpeed());
        validateWalkingAsymmetry(metrics.getWalkingAsymmetry());
        validateDistanceWalked(metrics.getDistanceWalked());
        validateTimestamp(metrics.getTimestamp());
    }

    public static void validateCreateOrganizationRequestBody(CreateOrganizationRequestBody body) {
        Validate.notNull(body, CREATE_ORGANIZATION_NULL_ERROR_MESSAGE);
        validateName(body.getOrganizationName());
    }

    public static void validateGetOrganizationRequestBody(GetOrganizationRequestBody body) {
        Validate.notNull(body, GET_ORGANIZATION_NULL_ERROR_MESSAGE);
        validateOrganizationId(body.getOrganizationId());
    }

    public static void validateCreateAdminRequestBody(CreateAdminRequestBody body) {
        Validate.notNull(body, CREATE_ADMIN_NULL_ERROR_MESSAGE);
        validateEmail(body.getEmail());
        validateFirstName(body.getFirstName());
        validateLastName(body.getLastName());
        validateOrganizationId(body.getOrganizationId());
    }

    public static void validateGetAdminRequestBody(GetAdminRequestBody body) {
        Validate.notNull(body, GET_ADMIN_NULL_ERROR_MESSAGE);
        validateAdminId(body.getAdminId());
    }

    public static void validateDeleteAdminRequestBody(DeleteAdminRequestBody body) {
        Validate.notNull(body, DELETE_ADMIN_NULL_ERROR_MESSAGE);
        validateAdminId(body.getAdminId());
    }

    public static void validateCreateCaregiverRequestBody(CreateCaregiverRequestBody body) {
        Validate.notNull(body, CREATE_CAREGIVER_NULL_ERROR_MESSAGE);
        validateEmail(body.getEmail());
        validateFirstName(body.getFirstName());
        validateLastName(body.getLastName());
        validateTitle(body.getTitle());
        validatePhoneNumber(body.getPhoneNumber());
        validateOrganizationId(body.getOrganizationId());
    }

    public static void validateAddPatientRequestBody(AddPatientRequestBody body) {
        Validate.notNull(body, ADD_PATIENT_NULL_ERROR_MESSAGE);
        validateCaregiverId(body.getCaregiverId());
        validatePatientId(body.getPatientId());
    }

    public static void validateRemovePatientRequestBody(RemovePatientRequestBody body) {
        Validate.notNull(body, REMOVE_PATIENT_NULL_ERROR_MESSAGE);
        validateCaregiverId(body.getCaregiverId());
        validatePatientId(body.getPatientId());
    }

    public static void validateGetCaregiverRequestBody(GetCaregiverRequestBody body) {
        Validate.notNull(body, GET_CAREGIVER_NULL_ERROR_MESSAGE);
        validateCaregiverId(body.getCaregiverId());
    }

    public static void validateGetAllPatientsRequestBody(GetAllPatientsRequestBody body) {
        Validate.notNull(body, GET_ALL_PATIENTS_NULL_ERROR_MESSAGE);
        validateCaregiverId(body.getCaregiverId());
    }

    public static void validateUpdateCaregiverRequestBody(UpdateCaregiverRequestBody body) {
        Validate.notNull(body, UPDATE_CAREGIVER_NULL_ERROR_MESSAGE);
        validateCaregiverId(body.getCaregiverId());
        validateFirstName(body.getFirstName());
        validateLastName(body.getLastName());
        validateTitle(body.getTitle());
        validatePhoneNumber(body.getPhoneNumber());
    }

    public static void validateDeleteCaregiverRequestBody(DeleteCaregiverRequestBody body) {
        Validate.notNull(body, DELETE_CAREGIVER_NULL_ERROR_MESSAGE);
        validateCaregiverId(body.getCaregiverId());
    }

    public static void validateCreatePatientRequestBody(CreatePatientRequestBody body) {
        Validate.notNull(body, CREATE_PATIENT_NULL_ERROR_MESSAGE);
        validateFirstName(body.getFirstName());
        validateLastName(body.getLastName());
        validatePhoneNumber(body.getPhoneNumber());
    }

    public static void validateUpdatePatientDeviceRequestBody(UpdatePatientDeviceRequestBody body) {
        Validate.notNull(body, UPDATE_PATIENT_DEVICE_NULL_ERROR_MESSAGE);
        validatePatientId(body.getPatientId());
    }

    public static void validateVerifyPatientRequestBody(VerifyPatientRequestBody body) {
        Validate.notNull(body, VERIFY_PATIENT_NULL_ERROR_MESSAGE);
        validateCaregiverId(body.getCaregiverId());
        validatePatientId(body.getPatientId());
        validateAuthCode(body.getAuthCode());
        validateDeviceId(body.getDeviceId());
    }

    public static void validateGetPatientRequestBody(GetPatientRequestBody body) {
        Validate.notNull(body, GET_PATIENT_NULL_ERROR_MESSAGE);
        validatePatientId(body.getPatientId());
    }

    public static void validateGetAllCaregiversRequestBody(GetAllCaregiversRequestBody body) {
        Validate.notNull(body, GET_ALL_CAREGIVERS_NULL_ERROR_MESSAGE);
        validatePatientId(body.getPatientId());
    }

    public static void validateAddMetricsRequestBody(AddMetricsRequestBody body) {
        Validate.notNull(body, ADD_METRICS_NULL_ERROR_MESSAGE);
        validateDeviceId(body.getDeviceId());
        Validate.notNull(body.getMetrics(), METRICS_NULL_ERROR_MESSAGE);
    }

    public static void validateQueryMetricsRequestBody(QueryMetricsRequestBody body) {
        Validate.notNull(body, QUERY_METRICS_NULL_ERROR_MESSAGE);
        validateIds(body.getPatientIds());
        for (String patientId : body.getPatientIds()) {
            validatePatientId(patientId);
        }
        validateTimestamp(body.getStart());
        validateTimestamp(body.getEnd());
    }

    public static void validateUpdatePatientRequestBody(UpdatePatientRequestBody body) {
        Validate.notNull(body, UPDATE_PATIENT_NULL_ERROR_MESSAGE);
        validatePatientId(body.getPatientId());
        validateFirstName(body.getFirstName());
        validateLastName(body.getLastName());
        validatePhoneNumber(body.getPhoneNumber());
    }

    public static void validateDeletePatientRequestBody(DeletePatientRequestBody body) {
        Validate.notNull(body, DELETE_PATIENT_NULL_ERROR_MESSAGE);
        validatePatientId(body.getPatientId());
    }
}
