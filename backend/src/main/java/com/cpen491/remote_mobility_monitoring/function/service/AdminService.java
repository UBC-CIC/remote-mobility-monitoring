package com.cpen491.remote_mobility_monitoring.function.service;

import com.cpen491.remote_mobility_monitoring.datastore.dao.AdminDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.OrganizationDao;
import com.cpen491.remote_mobility_monitoring.datastore.exception.DuplicateRecordException;
import com.cpen491.remote_mobility_monitoring.datastore.exception.RecordDoesNotExistException;
import com.cpen491.remote_mobility_monitoring.datastore.model.Admin;
import com.cpen491.remote_mobility_monitoring.datastore.model.Organization;
import com.cpen491.remote_mobility_monitoring.dependency.auth.CognitoWrapper;
import com.cpen491.remote_mobility_monitoring.dependency.auth.CognitoWrapper.CognitoUser;
import com.cpen491.remote_mobility_monitoring.dependency.exception.CognitoException;
import com.cpen491.remote_mobility_monitoring.dependency.utility.Validator;
import com.cpen491.remote_mobility_monitoring.function.schema.admin.CreateAdminRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.admin.CreateAdminResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.admin.GetAdminRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.admin.GetAdminResponseBody;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.AdminTable;

@Slf4j
@RequiredArgsConstructor
public class AdminService {
    @NonNull
    private AdminDao adminDao;
    @NonNull
    private OrganizationDao organizationDao;
    @NonNull
    private CognitoWrapper cognitoWrapper;

    /**
     * Creates an Admin in database and Cognito and adds it to an Organization.
     *
     * @param body The request body
     * @return {@link CreateAdminResponseBody}
     * @throws CognitoException If Cognito fails to create user
     * @throws RecordDoesNotExistException If Organization record with given organizationId does not exist
     * @throws DuplicateRecordException If record with the given email already exists
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if any of email, firstName,
     *                              lastName, or organizationId are empty
     */
    public CreateAdminResponseBody createAdmin(CreateAdminRequestBody body) {
        log.info("Creating Admin {}", body);
        Validator.validateCreateAdminRequestBody(body);

        organizationDao.findById(body.getOrganizationId());

        CognitoUser user = cognitoWrapper.createUser(body.getEmail());
        String adminId = AdminTable.ID_PREFIX + user.getId();

        Admin admin = Admin.builder()
                .pid(adminId)
                .sid(adminId)
                .email(body.getEmail())
                .firstName(body.getFirstName())
                .lastName(body.getLastName())
                .build();
        adminDao.create(admin, body.getOrganizationId());

        return CreateAdminResponseBody.builder()
                .adminId(adminId)
                .password(user.getPassword())
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
