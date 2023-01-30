package com.cpen491.remote_mobility_monitoring.function.service;

import com.cpen491.remote_mobility_monitoring.datastore.dao.CaregiverDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.PatientDao;
import com.cpen491.remote_mobility_monitoring.datastore.exception.DuplicateRecordException;
import com.cpen491.remote_mobility_monitoring.datastore.exception.InvalidAuthCodeException;
import com.cpen491.remote_mobility_monitoring.datastore.exception.RecordDoesNotExistException;
import com.cpen491.remote_mobility_monitoring.datastore.model.Patient;
import com.cpen491.remote_mobility_monitoring.dependency.utility.Validator;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.CreatePatientRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.CreatePatientResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.DeletePatientRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.DeletePatientResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.GetPatientRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.GetPatientResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.UpdatePatientDeviceRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.UpdatePatientDeviceResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.UpdatePatientRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.UpdatePatientResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.VerifyPatientRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.VerifyPatientResponseBody;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.cpen491.remote_mobility_monitoring.dependency.utility.TimeUtils.getCurrentUtcTime;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.TimeUtils.getCurrentUtcTimeString;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.TimeUtils.parseTime;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.TimeUtils.secondsBetweenTimes;

// TODO: logging
@RequiredArgsConstructor
public class PatientService {
    // TODO: make this configurable
    private static final long TTL = 300;

    @NonNull
    private PatientDao patientDao;
    @NonNull
    private CaregiverDao caregiverDao;

    /**
     * Creates a Patient. Generates an authCode which will expire after a short time and returns it.
     * Patient must verify using this authCode to get verified.
     *
     * @param body The request body
     * @return {@link CreatePatientResponseBody}
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if any of firstName, lastName,
     *                              dateOfBirth, or phoneNumber are empty
     */
    public CreatePatientResponseBody createPatient(CreatePatientRequestBody body) {
        Validator.validateCreatePatientRequestBody(body);

        Patient newPatient = Patient.builder()
                .firstName(body.getFirstName())
                .lastName(body.getLastName())
                .phoneNumber(body.getPhoneNumber())
                .dateOfBirth(body.getDateOfBirth())
                .build();

        generateAuthCodeForPatient(newPatient);
        newPatient.setVerified(false);
        patientDao.create(newPatient);

        return CreatePatientResponseBody.builder()
                .patientId(newPatient.getPid())
                .authCode(newPatient.getAuthCode())
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
        Validator.validateUpdatePatientDeviceRequestBody(body);

        Patient patient = patientDao.findById(body.getPatientId());

        generateAuthCodeForPatient(patient);
        patientDao.update(patient);

        return UpdatePatientDeviceResponseBody.builder()
                .authCode(patient.getAuthCode())
                .build();
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
        Validator.validateVerifyPatientRequestBody(body);

        Patient patient = patientDao.findById(body.getPatientId());
        caregiverDao.findById(body.getCaregiverId());

        verifyAuthCode(patient.getAuthCode(), patient.getAuthCodeTimestamp(), body.getAuthCode());

        patient.setDeviceId(body.getDeviceId());
        patient.setVerified(true);
        patientDao.update(patient);
        try {
            caregiverDao.addPatient(body.getPatientId(), body.getCaregiverId());
        } catch (DuplicateRecordException e) {
            // log.info("Patient [{}] already associated with Caregiver [{}]", patientId, caregiverId);
        }

        return VerifyPatientResponseBody.builder()
                .message("OK")
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
        Validator.validateGetPatientRequestBody(body);

        Patient patient = patientDao.findById(body.getPatientId());

        return GetPatientResponseBody.builder()
                .deviceId(patient.getDeviceId())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .dateOfBirth(patient.getDateOfBirth())
                .phoneNumber(patient.getPhoneNumber())
                .createdAt(patient.getCreatedAt())
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
        Validator.validateDeletePatientRequestBody(body);

        patientDao.delete(body.getPatientId());

        return DeletePatientResponseBody.builder()
                .message("OK")
                .build();
    }

    private static void generateAuthCodeForPatient(Patient patient) {
        patient.setAuthCode(UUID.randomUUID().toString().replace("-", ""));
        String currentTime = getCurrentUtcTimeString();
        patient.setAuthCodeTimestamp(currentTime);
    }

    private static void verifyAuthCode(String expected, String timestamp, String actual) {
        if (!expected.equals(actual)) {
            throw new InvalidAuthCodeException();
        }
        LocalDateTime authCodeTime = parseTime(timestamp);
        long secondsBetween = secondsBetweenTimes(authCodeTime, getCurrentUtcTime());
        if (secondsBetween > TTL) {
            throw new InvalidAuthCodeException();
        }
    }
}
