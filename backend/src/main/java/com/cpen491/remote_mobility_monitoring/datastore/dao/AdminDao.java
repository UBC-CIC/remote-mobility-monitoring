package com.cpen491.remote_mobility_monitoring.datastore.dao;

import com.cpen491.remote_mobility_monitoring.datastore.exception.DuplicateRecordException;
import com.cpen491.remote_mobility_monitoring.datastore.exception.RecordDoesNotExistException;
import com.cpen491.remote_mobility_monitoring.datastore.model.Admin;
import com.cpen491.remote_mobility_monitoring.datastore.model.Organization;
import com.cpen491.remote_mobility_monitoring.dependency.utility.Validator;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.util.Map;

import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.AdminTable;
import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.OrganizationTable;

@Slf4j
@AllArgsConstructor
public class AdminDao {
    @NonNull
    GenericDao genericDao;
    @NonNull
    OrganizationDao organizationDao;

    /**
     * Creates a new Admin record and adds it to an organization. Record with the given email must not already exist.
     *
     * @param newRecord The Admin record to create
     * @param organizationId The id of the Organization record
     * @throws RecordDoesNotExistException If Organization record with given organizationId does not exist
     * @throws DuplicateRecordException If record with the given email already exists
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if any of pid, sid, email, firstName,
     *                              lastName, or organizationId are empty
     */
    public void create(Admin newRecord, String organizationId) {
        log.info("Creating new Admin record {}", newRecord);
        Validator.validateAdmin(newRecord);
        Validator.validateOrganizationId(organizationId);

        Organization organization = organizationDao.findById(organizationId);

        if (findByEmail(newRecord.getEmail()) != null) {
            log.error("Admin record with email [{}] already exists", newRecord.getEmail());
            throw new DuplicateRecordException(Admin.class.getSimpleName(), newRecord.getEmail());
        }

        genericDao.setDate(newRecord);
        Map<String, AttributeValue> adminMap = Admin.convertToMap(newRecord);
        genericDao.put(adminMap);
        genericDao.addAssociation(Organization.convertToMap(organization), adminMap);
    }

    /**
     * Finds an Admin record by id.
     *
     * @param id The id of the record to find
     * @return {@link Admin}
     * @throws RecordDoesNotExistException If record with the given id does not exist
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if id is empty or invalid
     */
    public Admin findById(String id) {
        log.info("Finding Admin record with id [{}]", id);
        Validator.validateAdminId(id);

        GetItemResponse response = genericDao.findByPartitionKey(id);
        if (!response.hasItem()) {
            log.error("Cannot find Admin record with id [{}]", id);
            throw new RecordDoesNotExistException(Admin.class.getSimpleName(), id);
        }
        return Admin.convertFromMap(response.item());
    }

    /**
     * Finds an Admin record by email. Returns null if record does not exist.
     *
     * @param email The email of the record to find
     * @return {@link Admin}
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if email is empty
     */
    public Admin findByEmail(String email) {
        log.info("Finding Admin record with email [{}]", email);
        Validator.validateEmail(email);

        QueryResponse response = genericDao
                .findAllByPartitionKeyOnIndex(AdminTable.EMAIL_NAME, email, AdminTable.EMAIL_INDEX_NAME);
        if (!response.hasItems() || response.items().size() == 0) {
            log.info("Cannot find Admin record with email [{}]", email);
            return null;
        }
        Admin admin = Admin.convertFromMap(response.items().get(0));
        genericDao.setCorrectId(admin, AdminTable.ID_PREFIX);
        return admin;
    }

    /**
     * Find the Organization this Admin belongs to.
     *
     * @param adminId The id of the Admin record
     * @return {@link Organization}
     * @throws RecordDoesNotExistException If record with the given adminId does not exist
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if adminId is empty or invalid
     */
    public Organization findOrganization(String adminId) {
        log.info("Finding Organization of Admin [{}]", adminId);
        Validator.validateAdminId(adminId);

        findById(adminId);

        QueryResponse response = genericDao
                .findAllAssociationsOnSidIndex(adminId, OrganizationTable.ID_PREFIX);
        if (!response.hasItems() || response.items().size() == 0) {
            log.info("Cannot find Organization of Admin [{}]", adminId);
            return null;
        }
        Organization organization = Organization.convertFromMap(response.items().get(0));
        organization.setSid(organization.getPid());
        return organization;
    }

    /**
     * Updates an Admin record. Record with given id must already exist.
     * Record with given email should not already exist unless it is the same record being updated.
     *
     * @param updatedRecord The Admin record to update
     * @throws DuplicateRecordException If record with the given email already exists
     * @throws RecordDoesNotExistException If record with the given id does not exist
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if any of pid, sid, email,
     *                              firstName, or lastName are empty
     */
    public void update(Admin updatedRecord) {
        log.info("Updating Admin record {}", updatedRecord);
        Validator.validateAdmin(updatedRecord);

        Admin found = findByEmail(updatedRecord.getEmail());
        if (found != null && !found.getPid().equals(updatedRecord.getPid())) {
            log.error("Admin record with email [{}] already exists", updatedRecord.getEmail());
            throw new DuplicateRecordException(Admin.class.getSimpleName(), updatedRecord.getEmail());
        }

        findById(updatedRecord.getPid());

        genericDao.update(Admin.convertToMap(updatedRecord));
    }

    /**
     * Deletes an Admin record by id and all of its associations. Does nothing if record does not exist.
     *
     * @param id The id of the record to delete
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if id is empty or invalid
     */
    public void delete(String id) {
        log.info("Deleting Admin record with id [{}]", id);
        Validator.validateAdminId(id);

        genericDao.delete(id);
    }
}
