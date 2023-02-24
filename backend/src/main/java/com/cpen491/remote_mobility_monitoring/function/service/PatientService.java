package com.cpen491.remote_mobility_monitoring.function.service;

import com.cpen491.remote_mobility_monitoring.datastore.dao.CaregiverDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.MetricsDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.PatientDao;
import com.cpen491.remote_mobility_monitoring.datastore.exception.DuplicateRecordException;
import com.cpen491.remote_mobility_monitoring.datastore.exception.InvalidMetricsException;
import com.cpen491.remote_mobility_monitoring.datastore.exception.RecordDoesNotExistException;
import com.cpen491.remote_mobility_monitoring.datastore.model.Caregiver;
import com.cpen491.remote_mobility_monitoring.datastore.model.Metrics;
import com.cpen491.remote_mobility_monitoring.datastore.model.Patient;
import com.cpen491.remote_mobility_monitoring.dependency.exception.InvalidAuthCodeException;
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
import com.cpen491.remote_mobility_monitoring.function.schema.patient.UpdatePatientDeviceRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.UpdatePatientDeviceResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.UpdatePatientRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.UpdatePatientResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.VerifyPatientRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.VerifyPatientResponseBody;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.cpen491.remote_mobility_monitoring.dependency.utility.TimeUtils.getCurrentUtcTime;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.TimeUtils.getCurrentUtcTimeString;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.TimeUtils.parseTime;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.TimeUtils.secondsBetweenTimes;

@Slf4j
@RequiredArgsConstructor
public class PatientService {
    // TODO: make this configurable
    private static final long TTL = 300;

    @NonNull
    private PatientDao patientDao;
    @NonNull
    private CaregiverDao caregiverDao;
    @NonNull
    private MetricsDao metricsDao;

    /**
     * Creates a Patient. Generates an authCode which will expire after a short time and returns it.
     * Patient must verify using this authCode to get verified.
     *
     * @param body The request body
     * @return {@link CreatePatientResponseBody}
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if any of firstName, lastName, or phoneNumber are empty
     */
    public CreatePatientResponseBody createPatient(CreatePatientRequestBody body) {
        log.info("Creating Patient {}", body);
        Validator.validateCreatePatientRequestBody(body);

        Patient newPatient = Patient.builder()
                .firstName(body.getFirstName())
                .lastName(body.getLastName())
                .phoneNumber(body.getPhoneNumber())
                .build();

        patientDao.create(newPatient);

        return CreatePatientResponseBody.builder()
                .patientId(newPatient.getPid())
                .build();
    }

    /**
     * Initiates an update device request. Generates an authCode which will expire after a short time and returns it.
     * Patient must verify using this authCode to update their device ID.
     *
     * @param body The request body
     * @return {@link UpdatePatientDeviceResponseBody}
     * @throws RecordDoesNotExistException If Patient record with the given id does not exist
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if id is empty or invalid
     */
    public UpdatePatientDeviceResponseBody updatePatientDevice(UpdatePatientDeviceRequestBody body) {
//        log.info("Updating Patient device {}", body);
//        Validator.validateUpdatePatientDeviceRequestBody(body);
//
//        Patient patient = patientDao.findById(body.getPatientId());
//
//        generateAuthCodeForPatient(patient);
//        patientDao.update(patient);
//
//        return UpdatePatientDeviceResponseBody.builder()
//                .authCode(patient.getAuthCode())
//                .build();
        return null;
    }

    /**
     * Verifies a patient to mark them as verified or update their device ID.
     *
     * @param body The request body
     * @return {@link VerifyPatientResponseBody}
     * @throws RecordDoesNotExistException If Patient or Caregiver record with the given ids do not exist
     * @throws InvalidAuthCodeException If the existing authCode does not match the provided authCode or is expired
     * @throws DuplicateRecordException If Patient record with the given deviceId already exists
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if caregiverId, patientId, authCode,
     *                              or deviceId are empty or invalid
     */
    public VerifyPatientResponseBody verifyPatient(VerifyPatientRequestBody body) {
//        log.info("Verifying Patient {}", body);
//        Validator.validateVerifyPatientRequestBody(body);
//
//        Patient patient = patientDao.findById(body.getPatientId());
//        caregiverDao.findById(body.getCaregiverId());
//
//        verifyAuthCode(patient.getAuthCode(), patient.getAuthCodeTimestamp(), body.getAuthCode());
//
//        patient.setDeviceId(body.getDeviceId());
//        patient.setVerified(true);
//        patientDao.update(patient);
//        caregiverDao.addPatient(body.getPatientId(), body.getCaregiverId());
//
//        return VerifyPatientResponseBody.builder()
//                .message("OK")
//                .build();
        return null;
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
                .deviceId(patient.getDeviceId())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .phoneNumber(patient.getPhoneNumber())
                .createdAt(patient.getCreatedAt())
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
     * @throws RecordDoesNotExistException If Patient record with the given deviceId does not exist
     * @throws InvalidMetricsException If the metrics already exists or if timestamp is out of Timestream range
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if deviceId or metrics are empty or invalid
     */
    public AddMetricsResponseBody addMetrics(AddMetricsRequestBody body) {
        log.info("Adding Metrics {}", body);
        Validator.validateAddMetricsRequestBody(body);

        String deviceId = body.getDeviceId();
        Patient patient = patientDao.findByDeviceId(deviceId);
        if (patient == null) {
            throw new RecordDoesNotExistException(Patient.class.getSimpleName(), deviceId);
        }
        String patientId = patient.getPid();

        List<Metrics> metricsList = new ArrayList<>();
        for (AddMetricsSerialization serialization : body.getMetrics()) {
            Validator.validateAddMetricsSerialization(serialization);

            metricsList.addAll(AddMetricsSerialization.convertToMetrics(patientId, deviceId, serialization));
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
     * @throws DuplicateRecordException If record with the given deviceId already exists
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

//    private static void generateAuthCodeForPatient(Patient patient) {
//        patient.setAuthCode(UUID.randomUUID().toString().replace("-", ""));
//        String currentTime = getCurrentUtcTimeString();
//        patient.setAuthCodeTimestamp(currentTime);
//    }

//    private static void verifyAuthCode(String expected, String timestamp, String actual) {
//        if (!expected.equals(actual)) {
//            throw new InvalidAuthCodeException();
//        }
//        LocalDateTime authCodeTime = parseTime(timestamp);
//        long secondsBetween = secondsBetweenTimes(authCodeTime, getCurrentUtcTime());
//        if (secondsBetween > TTL) {
//            throw new InvalidAuthCodeException();
//        }
//    }
}
