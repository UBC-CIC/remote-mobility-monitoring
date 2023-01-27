package com.cpen491.remote_mobility_monitoring.datastore.dao;

import com.cpen491.remote_mobility_monitoring.datastore.exception.DuplicateRecordException;
import com.cpen491.remote_mobility_monitoring.datastore.exception.RecordDoesNotExistException;
import com.cpen491.remote_mobility_monitoring.datastore.model.Organization;
import com.cpen491.remote_mobility_monitoring.dependency.utility.Validator;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.AdminTable;
import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.BaseTable;
import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.CaregiverTable;
import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.OrganizationTable;

@Slf4j
@AllArgsConstructor
public class OrganizationDao {
    @NonNull
    GenericDao genericDao;

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
            throw new DuplicateRecordException(Organization.class.getSimpleName(), newRecord.getName());
        }

        GenericDao.setId(newRecord, OrganizationTable.ID_PREFIX);
        genericDao.create(Organization.convertToMap(newRecord));
    }

    /**
     * Finds an Organization record by id.
     *
     * @param id The id of the record to find
     * @return {@link Organization}
     * @throws RecordDoesNotExistException If record with the given id does not exist
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if id is empty
     */
    public Organization findById(String id) {
        log.info("Finding Organization record with id [{}]", id);
        Validator.validateId(id);

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
                .findAllByIndexPartitionKey(OrganizationTable.NAME_INDEX_NAME, OrganizationTable.NAME_NAME, name);
        if (!response.hasItems() || response.items().size() == 0) {
            log.info("Cannot find Organization record with name [{}]", name);
            return null;
        }
        return Organization.convertFromMap(response.items().get(0));
    }

    /**
     * Find all Admins belonging to this Organization.
     *
     * @param organizationId The id of the Organization record
     * @return {@link List}
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if organizationId is empty or invalid
     */
    public List<String> findAdminIds(String organizationId) {
        log.info("Finding all Admin records belonging to organization [{}]", organizationId);
        Validator.validateOrganizationId(organizationId);

        List<Map<String, AttributeValue>> result = genericDao
                .findAllAssociations(organizationId, AdminTable.ID_PREFIX).items();
        return result.stream().map(map -> map.get(BaseTable.SID_NAME).s()).collect(Collectors.toList());
    }

    /**
     * Find all Caregivers belonging to this Organization.
     *
     * @param organizationId The id of the Organization record
     * @return {@link List}
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if organizationId is empty or invalid
     */
    public List<String> findCaregiverIds(String organizationId) {
        log.info("Finding all Caregiver records belonging to organization [{}]", organizationId);
        Validator.validateOrganizationId(organizationId);

        List<Map<String, AttributeValue>> result = genericDao
                .findAllAssociations(organizationId, CaregiverTable.ID_PREFIX).items();
        return result.stream().map(map -> map.get(BaseTable.SID_NAME).s()).collect(Collectors.toList());
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
        Validator.validatePid(updatedRecord.getPid());
        Validator.validateSid(updatedRecord.getSid());
        Validator.validatePidEqualsSid(updatedRecord.getPid(), updatedRecord.getSid());

        Organization found = findByName(updatedRecord.getName());
        if (found != null && !found.getPid().equals(updatedRecord.getPid())) {
            throw new DuplicateRecordException(Organization.class.getSimpleName(), updatedRecord.getName());
        }

        try {
            genericDao.update(Organization.convertToMap(updatedRecord));
        } catch (ConditionalCheckFailedException e) {
            throw new RecordDoesNotExistException(Organization.class.getSimpleName(), updatedRecord.getPid());
        }
    }

    /**
     * Deletes an Organization record by id. Does nothing if record does not exist.
     *
     * @param id The id of the record to delete
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if id is empty
     */
    public void delete(String id) {
        log.info("Deleting Organization record with id [{}]", id);
        Validator.validateId(id);

        genericDao.delete(id);

        // TODO: delete all admins, caregivers, and patients
    }
}
