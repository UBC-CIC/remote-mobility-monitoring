package com.cpen491.remote_mobility_monitoring.datastore.dao;

import com.cpen491.remote_mobility_monitoring.datastore.exception.DuplicateRecordException;
import com.cpen491.remote_mobility_monitoring.datastore.exception.RecordDoesNotExistException;
import com.cpen491.remote_mobility_monitoring.datastore.model.Caregiver;
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

import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.CaregiverTable;

@Slf4j
@AllArgsConstructor
public class CaregiverDao {
    @NonNull
    GenericDao genericDao;
    @NonNull
    OrganizationDao organizationDao;

    /**
     * Creates a new Caregiver record and adds it to an organization. Record with the given email must not already exist.
     *
     * @param newRecord The Caregiver record to create
     * @param organizationId The id of the Organization record
     * @throws RecordDoesNotExistException If Organization record with given organizationId does not exist
     * @throws DuplicateRecordException If record with the given email already exists
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if any of email, firstName, lastName,
     *                              title, or phoneNumber are empty
     */
    public void create(Caregiver newRecord, String organizationId) {
        log.info("Creating new Caregiver record {}", newRecord);
        Validator.validateCaregiver(newRecord);

        Organization organization = organizationDao.findById(organizationId);

        if (findByEmail(newRecord.getEmail()) != null) {
            throw new DuplicateRecordException(Caregiver.class.getSimpleName(), newRecord.getEmail());
        }

        GenericDao.setId(newRecord, CaregiverTable.ID_PREFIX);
        Map<String, AttributeValue> caregiverMap = Caregiver.convertToMap(newRecord);
        genericDao.create(caregiverMap);
        genericDao.addAssociation(Organization.convertToMap(organization), caregiverMap);
    }

    /**
     * Finds a Caregiver record by id.
     *
     * @param id The id of the record to find
     * @return {@link Caregiver}
     * @throws RecordDoesNotExistException If record with the given id does not exist
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if id is empty
     */
    public Caregiver findById(String id) {
        log.info("Finding Caregiver record with id [{}]", id);
        Validator.validateId(id);

        GetItemResponse response = genericDao.findByPartitionKey(id);
        if (!response.hasItem()) {
            log.error("Cannot find Caregiver record with id [{}]", id);
            throw new RecordDoesNotExistException(Caregiver.class.getSimpleName(), id);
        }
        return Caregiver.convertFromMap(response.item());
    }

    /**
     * Finds a Caregiver record by email. Returns null if record does not exist.
     *
     * @param email The email of the record to find
     * @return {@link Caregiver}
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if email is empty
     */
    public Caregiver findByEmail(String email) {
        log.info("Finding Caregiver record with email [{}]", email);
        Validator.validateEmail(email);

        QueryResponse response = genericDao
                .findAllByIndexPartitionKey(CaregiverTable.EMAIL_INDEX_NAME, CaregiverTable.EMAIL_NAME, email);
        if (!response.hasItems() || response.items().size() == 0) {
            log.info("Cannot find Caregiver record with email [{}]", email);
            return null;
        }
        return Caregiver.convertFromMap(response.items().get(0));
    }

    /**
     * Batch finds all Caregiver records by IDs.
     *
     * @param ids Set of IDs of the records to find
     * @return {@link List}
     * @throws NullPointerException If ids is null
     */
    public List<Caregiver> batchFindById(List<String> ids) {
        log.info("Batch finding caregiver records matching IDs {}", ids);
        Validator.validateIds(ids);
        for (String id : ids) {
            Validator.validateCaregiverId(id);
        }

        List<Map<String, AttributeValue>> result = genericDao.batchFindByPartitionKey(ids);
        return result.stream().map(Caregiver::convertFromMap).collect(Collectors.toList());
    }

    // TODO: find patient IDs

    /**
     * Updates a Caregiver record. Record with given id must already exist.
     * Record with given email should not already exist unless it is the same record being updated.
     *
     * @param updatedRecord The Caregiver record to update
     * @throws DuplicateRecordException If record with the given email already exists
     * @throws RecordDoesNotExistException If record with the given id does not exist
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if any of pid, sid, email, firstName, lastName,
     *                              title, or phoneNumber are empty
     */
    public void update(Caregiver updatedRecord) {
        log.info("Updating Caregiver record {}", updatedRecord);
        Validator.validateCaregiver(updatedRecord);
        Validator.validatePid(updatedRecord.getPid());
        Validator.validateSid(updatedRecord.getSid());
        Validator.validatePidEqualsSid(updatedRecord.getPid(), updatedRecord.getSid());

        Caregiver found = findByEmail(updatedRecord.getEmail());
        if (found != null && !found.getPid().equals(updatedRecord.getPid())) {
            throw new DuplicateRecordException(Caregiver.class.getSimpleName(), updatedRecord.getEmail());
        }

        try {
            genericDao.update(Caregiver.convertToMap(updatedRecord));
        } catch (ConditionalCheckFailedException e) {
            throw new RecordDoesNotExistException(Caregiver.class.getSimpleName(), updatedRecord.getPid());
        }
    }

    /**
     * Deletes a Caregiver record by id. Does nothing if record does not exist.
     *
     * @param id The id of the record to delete
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if id is empty
     */
    public void delete(String id) {
        log.info("Deleting Caregiver record with id [{}]", id);
        Validator.validateId(id);

        genericDao.delete(id);

        // TODO: delete caregiver associations
    }
}
