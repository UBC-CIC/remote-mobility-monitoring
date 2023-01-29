package com.cpen491.remote_mobility_monitoring.function.service;

import com.cpen491.remote_mobility_monitoring.datastore.dao.CaregiverDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.PatientDao;
import com.cpen491.remote_mobility_monitoring.datastore.exception.DuplicateRecordException;
import com.cpen491.remote_mobility_monitoring.datastore.exception.RecordDoesNotExistException;
import com.cpen491.remote_mobility_monitoring.datastore.model.Caregiver;
import com.cpen491.remote_mobility_monitoring.dependency.utility.Validator;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.CreateCaregiverRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.CreateCaregiverResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.DeleteCaregiverRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.DeleteCaregiverResponseBody;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CaregiverService {
    @NonNull
    private PatientDao patientDao;
    @NonNull
    private CaregiverDao caregiverDao;

    /**
     * Creates a Caregiver and adds it to an Organization.
     *
     * @param body The request body
     * @return {@link CreateCaregiverResponseBody}
     * @throws RecordDoesNotExistException If Organization record with given organizationId does not exist
     * @throws DuplicateRecordException If record with the given email already exists
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if any of email, firstName, lastName,
     *                              title, phoneNumber, or organizationId are empty
     */
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

    /**
     * Deletes a Caregiver.
     *
     * @param body The request body
     * @return {@link DeleteCaregiverResponseBody}
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if caregiverId is empty
     */
    public DeleteCaregiverResponseBody deleteCaregiver(DeleteCaregiverRequestBody body) {
        Validator.validateDeleteCaregiverRequestBody(body);

        caregiverDao.delete(body.getCaregiverId());

        return DeleteCaregiverResponseBody.builder()
                .message("OK")
                .build();
    }
}
