package com.cpen491.remote_mobility_monitoring.function.service;

import com.cpen491.remote_mobility_monitoring.datastore.dao.OrganizationDao;
import com.cpen491.remote_mobility_monitoring.datastore.exception.RecordDoesNotExistException;
import com.cpen491.remote_mobility_monitoring.datastore.model.Caregiver;
import com.cpen491.remote_mobility_monitoring.datastore.model.Organization;
import com.cpen491.remote_mobility_monitoring.dependency.utility.Validator;
import com.cpen491.remote_mobility_monitoring.function.schema.organization.GetOrganizationRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.organization.GetOrganizationResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.organization.GetOrganizationResponseBody.CaregiverSerialization;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class OrganizationService {
    @NonNull
    OrganizationDao organizationDao;

    /**
     * Gets the Organization specified by organizationId. Also returns all caregivers in this Organization.
     *
     * @param body The request body
     * @return {@link GetOrganizationResponseBody}
     * @throws RecordDoesNotExistException If Organization record with the given organizationId does not exist
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if organizationId is empty
     */
    public GetOrganizationResponseBody getOrganization(GetOrganizationRequestBody body) {
        Validator.validateGetOrganizationRequestBody(body);

        Organization organization = organizationDao.findById(body.getOrganizationId());
        List<Caregiver> caregivers = organizationDao.findAllCaregivers(body.getOrganizationId());
        // TODO: get all admins as well

        return GetOrganizationResponseBody.builder()
                .organizationName(organization.getName())
                .caregivers(caregivers.stream().map(CaregiverSerialization::fromCaregiver).collect(Collectors.toList()))
                .build();
    }
}
