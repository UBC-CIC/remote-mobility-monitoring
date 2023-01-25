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
import com.cpen491.remote_mobility_monitoring.function.schema.patient.VerifyPatientRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.VerifyPatientResponseBody;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

import static com.cpen491.remote_mobility_monitoring.dependency.utility.TimeUtils.getCurrentUtcTime;
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
        patientDao.create(newPatient);

        return CreatePatientResponseBody.builder()
                .patientId(newPatient.getId())
                .authCode(newPatient.getAuthCode())
                .build();
    }

    public VerifyPatientResponseBody verifyPatient(VerifyPatientRequestBody body) {
        Validator.validateVerifyPatientRequestBody(body);

        Patient patient = patientDao.findById(body.getPatientId());
        if (patient == null) {
            throw new RecordDoesNotExistException(Patient.class.getSimpleName(), body.getPatientId());
        }
        Caregiver caregiver = caregiverDao.findById(body.getCaregiverId());
        if (caregiver == null) {
            throw new RecordDoesNotExistException(Caregiver.class.getSimpleName(), body.getCaregiverId());
        }

        verifyAuthCode(patient.getAuthCode(), patient.getAuthCodeTimestamp(), body.getAuthCode());

        patient.setDeviceId(body.getDeviceId());
        patient.setVerified(true);
        patient.setCaregiverIds(Set.of(body.getCaregiverId()));
        patientDao.update(patient);

        if (caregiver.getPatientIds() == null) {
            caregiver.setPatientIds(Set.of(body.getPatientId()));
        } else {
            caregiver.getPatientIds().add(body.getPatientId());
        }
        caregiverDao.update(caregiver);

        return VerifyPatientResponseBody.builder()
                .message("OK")
                .build();
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
