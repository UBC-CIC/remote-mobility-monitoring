package com.cpen491.remote_mobility_monitoring.function.service;

import com.cpen491.remote_mobility_monitoring.datastore.dao.AdminDao;
import com.cpen491.remote_mobility_monitoring.datastore.exception.DuplicateRecordException;
import com.cpen491.remote_mobility_monitoring.datastore.exception.RecordDoesNotExistException;
import com.cpen491.remote_mobility_monitoring.datastore.model.Admin;
import com.cpen491.remote_mobility_monitoring.datastore.model.Organization;
import com.cpen491.remote_mobility_monitoring.dependency.utility.Validator;
import com.cpen491.remote_mobility_monitoring.function.schema.admin.CreateAdminRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.admin.CreateAdminResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.admin.GetAdminRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.admin.GetAdminResponseBody;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class AdminService {
    @NonNull
    private AdminDao adminDao;

    /**
     * Creates an Admin and adds it to an Organization.
     *
     * @param body The request body
     * @return {@link CreateAdminResponseBody}
     * @throws RecordDoesNotExistException If Organization record with given organizationId does not exist
     * @throws DuplicateRecordException If record with the given email already exists
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if any of email, firstName,
     *                              lastName, or organizationId are empty
     */
    public CreateAdminResponseBody createAdmin(CreateAdminRequestBody body) {
        log.info("Creating Admin {}", body);
        Validator.validateCreateAdminRequestBody(body);

        Admin admin = Admin.builder()
                .email(body.getEmail())
                .firstName(body.getFirstName())
                .lastName(body.getLastName())
                .build();
        adminDao.create(admin, body.getOrganizationId());

        return CreateAdminResponseBody.builder()
                .message("OK")
                .build();
    }

    /**
     * Gets the Admin specified by adminId and its Organization.
     *
     * @param body The request body
     * @return {@link GetAdminResponseBody}
     * @throws RecordDoesNotExistException If record with the given id does not exist
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if id is empty or invalid
     */
    public GetAdminResponseBody getAdmin(GetAdminRequestBody body) {
        log.info("Getting Admin {}", body);
        Validator.validateGetAdminRequestBody(body);

        Admin admin = adminDao.findById(body.getAdminId());
        Organization organization = adminDao.findOrganization(body.getAdminId());

        return GetAdminResponseBody.builder()
                .email(admin.getEmail())
                .firstName(admin.getFirstName())
                .lastName(admin.getLastName())
                .organizationId(organization.getPid())
                .organizationName(organization.getName())
                .createdAt(admin.getCreatedAt())
                .build();
    }

    public void deleteAdmin() {
    }
}
