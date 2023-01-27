package com.cpen491.remote_mobility_monitoring.function.service;

import com.cpen491.remote_mobility_monitoring.datastore.dao.CaregiverDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.PatientDao;
import com.cpen491.remote_mobility_monitoring.datastore.exception.RecordDoesNotExistException;
import com.cpen491.remote_mobility_monitoring.datastore.model.Caregiver;
import com.cpen491.remote_mobility_monitoring.datastore.model.Patient;
import com.cpen491.remote_mobility_monitoring.dependency.utility.Validator;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.CreateCaregiverRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.CreateCaregiverResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.DeleteCaregiverRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.DeleteCaregiverResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.DeletePatientRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.DeletePatientResponseBody;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CaregiverService {
    @NonNull
    private PatientDao patientDao;
    @NonNull
    private CaregiverDao caregiverDao;

    public CreateCaregiverResponseBody createCaregiver(CreateCaregiverRequestBody body) {
        Validator.validateCreateCaregiverRequestBody(body);

        Caregiver caregiver = Caregiver.builder()
                .email(body.getEmail())
                .firstName(body.getFirstName())
                .lastName(body.getLastName())
                .title(body.getTitle())
                .phoneNumber(body.getPhoneNumber())
                .build();
        caregiverDao.create(caregiver, body.getOrganizationId());

        return CreateCaregiverResponseBody.builder()
                .message("OK")
                .build();
    }

    public DeleteCaregiverResponseBody deleteCaregiver(DeleteCaregiverRequestBody body) {
        Validator.validateDeleteCaregiverRequestBody(body);

//        Caregiver caregiver = findCaregiverById(body.getCaregiverId());
//        List<Patient> patients = patientDao.batchFindById(caregiver.getPatientIds());
//        for (Patient patient : patients) {
//            patient.getCaregiverIds().remove(body.getCaregiverId());
//        }
//
//        caregiverDao.delete(body.getCaregiverId());

        return DeleteCaregiverResponseBody.builder()
                .message("OK")
                .build();
    }
}
