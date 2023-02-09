package com.cpen491.remote_mobility_monitoring.function.service;

import com.cpen491.remote_mobility_monitoring.datastore.dao.OrganizationDao;
import com.cpen491.remote_mobility_monitoring.datastore.exception.DuplicateRecordException;
import com.cpen491.remote_mobility_monitoring.datastore.exception.RecordDoesNotExistException;
import com.cpen491.remote_mobility_monitoring.datastore.model.Admin;
import com.cpen491.remote_mobility_monitoring.datastore.model.Caregiver;
import com.cpen491.remote_mobility_monitoring.datastore.model.Organization;
import com.cpen491.remote_mobility_monitoring.dependency.utility.Validator;
import com.cpen491.remote_mobility_monitoring.function.schema.organization.CreateOrganizationRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.organization.CreateOrganizationResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.organization.GetOrganizationRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.organization.GetOrganizationResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.organization.GetOrganizationResponseBody.AdminSerialization;
import com.cpen491.remote_mobility_monitoring.function.schema.organization.GetOrganizationResponseBody.CaregiverSerialization;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class OrganizationService {
    @NonNull
    private OrganizationDao organizationDao;

    /**
     * Creates an Organization.
     *
     * @param body The request body
     * @return {@link CreateOrganizationResponseBody}
     * @throws DuplicateRecordException If record with the given organizationName already exists
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if organizationName is empty
     */
    public CreateOrganizationResponseBody createOrganization(CreateOrganizationRequestBody body) {
        log.info("Creating Organization {}", body);
        Validator.validateCreateOrganizationRequestBody(body);

        Organization organization = Organization.builder()
                .name(body.getOrganizationName())
                .build();
        organizationDao.create(organization);

        return CreateOrganizationResponseBody.builder()
                .organizationId(organization.getPid())
                .build();
    }

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
        log.info("Getting Organization {}", body);
        Validator.validateGetOrganizationRequestBody(body);

        Organization organization = organizationDao.findById(body.getOrganizationId());
        List<Admin> admins = organizationDao.findAllAdmins(body.getOrganizationId());
        List<Caregiver> caregivers = organizationDao.findAllCaregivers(body.getOrganizationId());

        return GetOrganizationResponseBody.builder()
                .organizationName(organization.getName())
                .admins(admins.stream().map(AdminSerialization::fromAdmin).collect(Collectors.toList()))
                .caregivers(caregivers.stream().map(CaregiverSerialization::fromCaregiver).collect(Collectors.toList()))
                .build();
    }
}
