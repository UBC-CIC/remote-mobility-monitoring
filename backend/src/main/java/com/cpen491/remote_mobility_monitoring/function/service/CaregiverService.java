package com.cpen491.remote_mobility_monitoring.function.service;

import com.cpen491.remote_mobility_monitoring.datastore.dao.CaregiverDao;
import com.cpen491.remote_mobility_monitoring.datastore.model.Caregiver;
import com.cpen491.remote_mobility_monitoring.dependency.utility.Validator;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.CreateCaregiverRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.CreateCaregiverResponseBody;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CaregiverService {
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
                // TODO: remove this field?
                .imageUrl("123")
                .organizationId(body.getOrganizationId())
                .build();
        caregiverDao.create(caregiver);

        return CreateCaregiverResponseBody.builder()
                .message("OK")
                .build();
    }
}
