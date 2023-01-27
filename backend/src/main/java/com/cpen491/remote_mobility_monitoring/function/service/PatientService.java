package com.cpen491.remote_mobility_monitoring.function.service;

import com.cpen491.remote_mobility_monitoring.datastore.dao.CaregiverDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.PatientDao;
import com.cpen491.remote_mobility_monitoring.datastore.exception.InvalidAuthCodeException;
import com.cpen491.remote_mobility_monitoring.datastore.exception.RecordDoesNotExistException;
import com.cpen491.remote_mobility_monitoring.datastore.model.Caregiver;
import com.cpen491.remote_mobility_monitoring.datastore.model.Patient;
import com.cpen491.remote_mobility_monitoring.dependency.utility.Validator;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.CreatePatientRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.CreatePatientResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.DeletePatientRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.DeletePatientResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.SharePatientRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.SharePatientResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.UpdatePatientDeviceRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.UpdatePatientDeviceResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.VerifyPatientRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.VerifyPatientResponseBody;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
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

    public UpdatePatientDeviceResponseBody updatePatientDevice(UpdatePatientDeviceRequestBody body) {
        Validator.validateUpdatePatientDeviceRequestBody(body);

        Patient patient = findPatientById(body.getPatientId());

        generateAuthCodeForPatient(patient);
        patientDao.update(patient);

        return UpdatePatientDeviceResponseBody.builder()
                .authCode(patient.getAuthCode())
                .build();
    }

    public VerifyPatientResponseBody verifyPatient(VerifyPatientRequestBody body) {
        Validator.validateVerifyPatientRequestBody(body);

        Patient patient = findPatientById(body.getPatientId());
        Caregiver caregiver = findCaregiverById(body.getCaregiverId());

        verifyAuthCode(patient.getAuthCode(), patient.getAuthCodeTimestamp(), body.getAuthCode());

        patient.setDeviceId(body.getDeviceId());
        patient.setVerified(true);
//        associatePatientWithCaregiver(patient, caregiver);
        patientDao.update(patient);
        caregiverDao.update(caregiver);

        return VerifyPatientResponseBody.builder()
                .message("OK")
                .build();
    }

    public SharePatientResponseBody sharePatient(SharePatientRequestBody body) {
        Validator.validateSharePatientRequestBody(body);

        Patient patient = findPatientById(body.getPatientId());
        Caregiver caregiver = findCaregiverById(body.getCaregiverId());

//        associatePatientWithCaregiver(patient, caregiver);
        patientDao.update(patient);
        caregiverDao.update(caregiver);

        return SharePatientResponseBody.builder()
                .message("OK")
                .build();
    }

    public DeletePatientResponseBody deletePatient(DeletePatientRequestBody body) {
        Validator.validateDeletePatientRequestBody(body);

        patientDao.delete(body.getPatientId());

        return DeletePatientResponseBody.builder()
                .message("OK")
                .build();
    }

    private Patient findPatientById(String id) {
        Patient patient = patientDao.findById(id);
        if (patient == null) {
            throw new RecordDoesNotExistException(Patient.class.getSimpleName(), id);
        }
        return patient;
    }

    private Caregiver findCaregiverById(String id) {
        Caregiver caregiver = caregiverDao.findById(id);
        if (caregiver == null) {
            throw new RecordDoesNotExistException(Caregiver.class.getSimpleName(), id);
        }
        return caregiver;
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
