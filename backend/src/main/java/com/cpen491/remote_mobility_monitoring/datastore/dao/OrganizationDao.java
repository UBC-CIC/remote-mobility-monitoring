package com.cpen491.remote_mobility_monitoring.datastore.dao;

import com.cpen491.remote_mobility_monitoring.datastore.exception.DuplicateRecordException;
import com.cpen491.remote_mobility_monitoring.datastore.exception.RecordDoesNotExistException;
import com.cpen491.remote_mobility_monitoring.datastore.model.Admin;
import com.cpen491.remote_mobility_monitoring.datastore.model.Caregiver;
import com.cpen491.remote_mobility_monitoring.datastore.model.Organization;
import com.cpen491.remote_mobility_monitoring.dependency.utility.Validator;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.AdminTable;
import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.CaregiverTable;
import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.OrganizationTable;

@Slf4j
@AllArgsConstructor
public class OrganizationDao {
    @NonNull
    private GenericDao genericDao;

    /**
     * Creates a new Organization record. Record with the given name must not already exist.
     *
     * @param newRecord The Organization record to create
     * @throws DuplicateRecordException If record with the given name already exists
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if name is empty
     */
    public void create(Organization newRecord) {
        log.info("Creating new Organization record {}", newRecord);
        Validator.validateOrganization(newRecord);

        if (findByName(newRecord.getName()) != null) {
            log.error("Organization record with name [{}] already exists", newRecord.getName());
            throw new DuplicateRecordException(Organization.class.getSimpleName(), newRecord.getName());
        }

        genericDao.setIdAndDate(newRecord, OrganizationTable.ID_PREFIX);
        genericDao.put(Organization.convertToMap(newRecord));
    }

    /**
     * Checks whether Organization has Caregiver.
     *
     * @param caregiverId The id of the Caregiver record
     * @param organizationId The id of the Organization record
     * @return {@link Boolean}
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if organizationId or caregiverId is empty or invalid
     */
    public boolean hasCaregiver(String caregiverId, String organizationId) {
        log.info("Checking that Organization [{}] has Caregiver [{}]", organizationId, caregiverId);
        Validator.validateOrganizationId(organizationId);
        Validator.validateCaregiverId(caregiverId);

        GetItemResponse response = genericDao.findByPrimaryKey(organizationId, caregiverId);
        return response.hasItem();
    }

    /**
     * Finds an Organization record by id.
     *
     * @param id The id of the record to find
     * @return {@link Organization}
     * @throws RecordDoesNotExistException If record with the given id does not exist
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if id is empty or invalid
     */
    public Organization findById(String id) {
        log.info("Finding Organization record with id [{}]", id);
        Validator.validateOrganizationId(id);

        GetItemResponse response = genericDao.findByPartitionKey(id);
        if (!response.hasItem()) {
            log.error("Cannot find Organization record with id [{}]", id);
            throw new RecordDoesNotExistException(Organization.class.getSimpleName(), id);
        }
        return Organization.convertFromMap(response.item());
    }

    /**
     * Finds an Organization record by name. Returns null if record does not exist.
     *
     * @param name The name of the record to find
     * @return {@link Organization}
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if name is empty
     */
    public Organization findByName(String name) {
        log.info("Finding Organization record with name [{}]", name);
        Validator.validateName(name);

        QueryResponse response = genericDao
                .findAllByPartitionKeyOnIndex(OrganizationTable.NAME_NAME, name, OrganizationTable.NAME_INDEX_NAME);
        if (!response.hasItems() || response.items().size() == 0) {
            log.info("Cannot find Organization record with name [{}]", name);
            return null;
        }
        Organization organization = Organization.convertFromMap(response.items().get(0));
        genericDao.setCorrectId(organization, OrganizationTable.ID_PREFIX);
        return organization;
    }

    /**
     * Find all Admins belonging to this Organization.
     *
     * @param organizationId The id of the Organization record
     * @return {@link List}
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if organizationId is empty or invalid
     */
    public List<Admin> findAllAdmins(String organizationId) {
        log.info("Finding all Admin records belonging to Organization [{}]", organizationId);
        Validator.validateOrganizationId(organizationId);

        List<Map<String, AttributeValue>> result = genericDao
                .findAllAssociations(organizationId, AdminTable.ID_PREFIX).items();
        return result.stream().map(map -> {
            Admin admin = Admin.convertFromMap(map);
            admin.setPid(admin.getSid());
            return admin;
        }).collect(Collectors.toList());
    }

    /**
     * Find all Caregivers belonging to this Organization.
     *
     * @param organizationId The id of the Organization record
     * @return {@link List}
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if organizationId is empty or invalid
     */
    public List<Caregiver> findAllCaregivers(String organizationId) {
        log.info("Finding all Caregiver records belonging to Organization [{}]", organizationId);
        Validator.validateOrganizationId(organizationId);

        List<Map<String, AttributeValue>> result = genericDao
                .findAllAssociations(organizationId, CaregiverTable.ID_PREFIX).items();
        return result.stream().map(map -> {
            Caregiver caregiver = Caregiver.convertFromMap(map);
            caregiver.setPid(caregiver.getSid());
            return caregiver;
        }).collect(Collectors.toList());
    }

    /**
     * Updates an Organization record. Record with given id must already exist.
     * Record with given name should not already exist unless it is the same record being updated.
     *
     * @param updatedRecord The Organization record to update
     * @throws DuplicateRecordException If record with the given name already exists
     * @throws RecordDoesNotExistException If record with the given id does not exist
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if pid, sid, or name are empty
     */
    public void update(Organization updatedRecord) {
        log.info("Updating Organization record {}", updatedRecord);
        Validator.validateOrganization(updatedRecord);
        Validator.validatePidEqualsSid(updatedRecord.getPid(), updatedRecord.getSid());
        Validator.validateOrganizationId(updatedRecord.getPid());

        Organization found = findByName(updatedRecord.getName());
        if (found != null && !found.getPid().equals(updatedRecord.getPid())) {
            log.error("Organization record with name [{}] already exists", updatedRecord.getName());
            throw new DuplicateRecordException(Organization.class.getSimpleName(), updatedRecord.getName());
        }

        findById(updatedRecord.getPid());

        genericDao.update(Organization.convertToMap(updatedRecord));
    }

    /**
     * Deletes an Organization record by id and all of its associations. Does nothing if record does not exist.
     *
     * @param id The id of the record to delete
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if id is empty or invalid
     */
    public void delete(String id) {
        log.info("Deleting Organization record with id [{}]", id);
        Validator.validateOrganizationId(id);

        genericDao.delete(id);
    }
}
