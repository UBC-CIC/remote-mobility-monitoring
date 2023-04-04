package com.cpen491.remote_mobility_monitoring.function.service;

import com.cpen491.remote_mobility_monitoring.datastore.dao.MetricsDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.PatientDao;
import com.cpen491.remote_mobility_monitoring.datastore.exception.DuplicateRecordException;
import com.cpen491.remote_mobility_monitoring.datastore.exception.InvalidMetricsException;
import com.cpen491.remote_mobility_monitoring.datastore.exception.RecordDoesNotExistException;
import com.cpen491.remote_mobility_monitoring.datastore.model.Caregiver;
import com.cpen491.remote_mobility_monitoring.datastore.model.Metrics;
import com.cpen491.remote_mobility_monitoring.datastore.model.Patient;
import com.cpen491.remote_mobility_monitoring.dependency.auth.CognitoWrapper;
import com.cpen491.remote_mobility_monitoring.dependency.auth.CognitoWrapper.CognitoUser;
import com.cpen491.remote_mobility_monitoring.dependency.exception.CognitoException;
import com.cpen491.remote_mobility_monitoring.dependency.utility.Validator;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.AddMetricsRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.AddMetricsRequestBody.AddMetricsSerialization;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.AddMetricsResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.CreatePatientRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.CreatePatientResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.DeletePatientRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.DeletePatientResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.GetAllCaregiversRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.GetAllCaregiversResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.GetAllCaregiversResponseBody.CaregiverSerialization;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.GetPatientRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.GetPatientResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.QueryMetricsRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.QueryMetricsResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.QueryMetricsResponseBody.QueryMetricsSerialization;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.UpdatePatientRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.UpdatePatientResponseBody;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.PatientTable;
import static com.cpen491.remote_mobility_monitoring.dependency.auth.CognitoWrapper.PATIENT_GROUP_NAME;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Slf4j
@RequiredArgsConstructor
public class PatientService {
    @NonNull
    private PatientDao patientDao;
    @NonNull
    private MetricsDao metricsDao;
    @NonNull
    private CognitoWrapper cognitoWrapper;

    /**
     * Creates a Patient in database and Cognito.
     *
     * @param body The request body
     * @return {@link CreatePatientResponseBody}
     * @throws CognitoException If Cognito fails to create user or add user to group
     * @throws DuplicateRecordException If record with the given email already exists
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if any of firstName, lastName, or phoneNumber are empty
     */
    public CreatePatientResponseBody createPatient(CreatePatientRequestBody body) {
        log.info("Creating Patient {}", body);
        Validator.validateCreatePatientRequestBody(body);

        CognitoUser user = cognitoWrapper.createUserIfNotExistAndAddToGroup(body.getEmail(), PATIENT_GROUP_NAME);
        cognitoWrapper.setPassword(body.getEmail(), body.getPassword());
        String patientId = PatientTable.ID_PREFIX + user.getId();

        Patient newPatient = Patient.builder()
                .pid(patientId)
                .sid(patientId)
                .email(body.getEmail())
                .firstName(body.getFirstName())
                .lastName(body.getLastName())
                .phoneNumber(body.getPhoneNumber())
                .sex(body.getSex())
                .height(body.getHeight())
                .weight(body.getWeight())
                .birthday(
                        isEmpty(body.getBirthday()) ? null : LocalDate.parse(body.getBirthday(), DateTimeFormatter.ISO_DATE)
                )
                .build();
        patientDao.create(newPatient);
        return CreatePatientResponseBody.builder()
                .patientId(newPatient.getPid())
                .build();
    }

    /**
     * Gets the Patient specified by patientId.
     *
     * @param body The request body
     * @return {@link GetPatientResponseBody}
     * @throws RecordDoesNotExistException If Patient record with the given patientId does not exist
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if patientId is empty
     */
    public GetPatientResponseBody getPatient(GetPatientRequestBody body) {
        log.info("Getting Patient {}", body);
        Validator.validateGetPatientRequestBody(body);

        Patient patient = patientDao.findById(body.getPatientId());

        return GetPatientResponseBody.builder()
                .email(patient.getEmail())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .phoneNumber(patient.getPhoneNumber())
                .createdAt(patient.getCreatedAt())
                .birthday(patient.getBirthday() == null ? null : patient.getBirthday().format(DateTimeFormatter.ISO_DATE))
                .height(patient.getHeight())
                .weight(patient.getWeight())
                .sex(patient.getSex())
                .build();
    }

    /**
     * Get all Caregivers for a Patient.
     *
     * @param body The request body
     * @return {@link GetAllCaregiversResponseBody}
     * @throws RecordDoesNotExistException If Patient record with the given patientId does not exist
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if patientId is empty
     */
    public GetAllCaregiversResponseBody getAllCaregivers(GetAllCaregiversRequestBody body) {
        log.info("Getting all Caregivers {}", body);
        Validator.validateGetAllCaregiversRequestBody(body);

        patientDao.findById(body.getPatientId());
        List<Caregiver> caregivers = patientDao.findAllCaregivers(body.getPatientId());

        return GetAllCaregiversResponseBody.builder()
                .caregivers(caregivers.stream().map(CaregiverSerialization::fromCaregiver).collect(Collectors.toList()))
                .build();
    }

    /**
     * Add Metrics to Patient.
     *
     * @param body The request body
     * @return {@link AddMetricsResponseBody}
     * @throws RecordDoesNotExistException If Patient record with the given patientId does not exist
     * @throws InvalidMetricsException If the metrics already exists or if timestamp is out of Timestream range
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if patientId or metrics are empty or invalid
     */
    public AddMetricsResponseBody addMetrics(AddMetricsRequestBody body) {
        log.info("Adding Metrics {}", body);
        Validator.validateAddMetricsRequestBody(body);

        Patient patient = patientDao.findById(body.getPatientId());

        List<Metrics> metricsList = new ArrayList<>();
        for (AddMetricsSerialization serialization : body.getMetrics()) {
            Validator.validateAddMetricsSerialization(serialization);

            metricsList.addAll(AddMetricsSerialization.convertToMetrics(patient, serialization));
        }

        metricsDao.add(metricsList);

        return AddMetricsResponseBody.builder()
                .message("OK")
                .build();
    }

    /**
     * Queries for Metrics for one or more Patients at specified time range.
     *
     * @param body The request body
     * @return {@link QueryMetricsResponseBody}
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if any of patientIds, start, or end are empty or invalid
     */
    public QueryMetricsResponseBody queryMetrics(QueryMetricsRequestBody body) {
        log.info("Querying Metrics {}", body);
        Validator.validateQueryMetricsRequestBody(body);

        List<Metrics> metrics = metricsDao.query(body.getPatientIds(), body.getStart(), body.getEnd());

        return QueryMetricsResponseBody.builder()
                .metrics(QueryMetricsSerialization.convertFromMetrics(metrics))
                .build();
    }

    /**
     * Updates a Patient.
     *
     * @param body The request body
     * @return {@link UpdatePatientResponseBody}
     * @throws DuplicateRecordException If record with the given email already exists
     * @throws RecordDoesNotExistException If record with the given id does not exist
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if any of patientId, firstName, lastName,
     *                              or phoneNumber are empty
     */
    public UpdatePatientResponseBody updatePatient(UpdatePatientRequestBody body) {
        log.info("Updating Patient {}", body);
        Validator.validateUpdatePatientRequestBody(body);

        Patient patient = patientDao.findById(body.getPatientId());
        patient.setFirstName(body.getFirstName());
        patient.setLastName(body.getLastName());
        patient.setPhoneNumber(body.getPhoneNumber());
        patient.setBirthday(
                isEmpty(body.getBirthday()) ? null : LocalDate.parse(body.getBirthday(), DateTimeFormatter.ISO_DATE)
        );
        patient.setHeight(body.getHeight());
        patient.setWeight(body.getWeight());
        patient.setSex(body.getSex());
        patientDao.update(patient);

        return UpdatePatientResponseBody.builder()
                .message("OK")
                .build();
    }

    /**
     * Deletes a Patient.
     *
     * @param body The request body
     * @return {@link DeletePatientResponseBody}
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if patientId is empty
     */
    public DeletePatientResponseBody deletePatient(DeletePatientRequestBody body) {
        log.info("Deleting Patient {}", body);
        Validator.validateDeletePatientRequestBody(body);

        try {
            Patient patient = patientDao.findById(body.getPatientId());
            cognitoWrapper.removeUserFromGroupAndDeleteUser(patient.getEmail(), PATIENT_GROUP_NAME);
        } catch (Exception e) {
            log.warn("Error {} thrown when trying to find and delete Patient {} in Cognito", e.getClass(), body);
        }

        patientDao.delete(body.getPatientId());

        return DeletePatientResponseBody.builder()
                .message("OK")
                .build();
    }

    /**
     * Primes the PatientService to reduce cold start time.
     */
    public void prime() {
        log.info("Priming PatientService");
        try {
            patientDao.findById("pat-prime");
        } catch (Exception e) {
            // Expected
        }
        try {
            metricsDao.query(Collections.singletonList("pat-prime"), "2023-01-01T00:00:00", "2023-01-02T00:00:00");
        } catch (Exception e) {
            // Expected
        }
        log.info("Done priming PatientService");
    }
}
