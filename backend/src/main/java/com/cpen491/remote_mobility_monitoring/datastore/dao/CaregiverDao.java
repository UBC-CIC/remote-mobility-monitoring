package com.cpen491.remote_mobility_monitoring.datastore.dao;

import com.cpen491.remote_mobility_monitoring.datastore.exception.DuplicateRecordException;
import com.cpen491.remote_mobility_monitoring.datastore.exception.RecordDoesNotExistException;
import com.cpen491.remote_mobility_monitoring.datastore.model.Caregiver;
import com.cpen491.remote_mobility_monitoring.datastore.model.Organization;
import com.cpen491.remote_mobility_monitoring.dependency.utility.Validator;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;

import java.util.Iterator;

import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.CaregiverTable;

@Slf4j
@AllArgsConstructor
public class CaregiverDao {
    @NonNull
    GenericDao<Caregiver> genericDao;
    @NonNull
    OrganizationDao organizationDao;

    /**
     * Creates a new Caregiver record. Record with the given email must not already exist.
     *
     * @param newRecord The Caregiver record to create
     * @throws RecordDoesNotExistException If Organization record with given organizationId does not exist
     * @throws DuplicateRecordException If record with the given email already exists
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if any of email, firstName, lastName,
     *                              title, phoneNumber, imageUrl, or organizationId are empty
     */
    public void create(Caregiver newRecord) {
        log.info("Creating new Caregiver record {}", newRecord);
        Validator.validateCaregiver(newRecord);

        if (organizationDao.findById(newRecord.getOrganizationId()) == null) {
            throw new RecordDoesNotExistException(Organization.class.getSimpleName(), newRecord.getOrganizationId());
        }

        if (findByEmail(newRecord.getEmail()) != null) {
            throw new DuplicateRecordException(Caregiver.class.getSimpleName(), newRecord.getEmail());
        }

        genericDao.create(newRecord);
    }

    /**
     * Finds a Caregiver record by id. Returns null if record does not exist.
     *
     * @param id The id of the record to find
     * @return {@link Caregiver}
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if id is empty
     */
    public Caregiver findById(String id) {
        log.info("Finding Caregiver record with id [{}]", id);
        Validator.validateId(id);

        Caregiver found = genericDao.findByPartitionKey(id);
        if (found == null) {
            log.info("Cannot find Caregiver record with id [{}]", id);
        }
        return found;
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

        Caregiver found = genericDao.findOneByIndexPartitionKey(CaregiverTable.EMAIL_INDEX_NAME, email);
        if (found == null) {
            log.info("Cannot find Caregiver record with email [{}]", email);
        }
        return found;
    }

    /**
     * Finds all Caregiver records by organizationId. Returns a paged iterator of Caregiver records.
     *
     * @param organizationId The organizationId of the records to find
     * @return {@link Iterator}
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if organizationId is empty
     */
    public Iterator<Page<Caregiver>> findAllInOrganization(String organizationId) {
        log.info("Finding all Caregiver records with organizationId [{}]", organizationId);
        Validator.validateOrganizationId(organizationId);

        return genericDao.findAllByIndexPartitionKey(CaregiverTable.ORGANIZATION_ID_INDEX_NAME, organizationId);
    }

    /**
     * Updates a Caregiver record. Record with given id must already exist.
     *
     * @param updatedRecord The Caregiver record to update
     * @throws RecordDoesNotExistException If record with the given id does not exist
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if any of id, email, firstName, lastName,
     *                              title, phoneNumber, imageUrl, or organizationId are empty
     */
    public void update(Caregiver updatedRecord) {
        log.info("Updating Caregiver record {}", updatedRecord);
        Validator.validateCaregiver(updatedRecord);
        Validator.validateId(updatedRecord.getId());

        genericDao.update(updatedRecord, Caregiver.class);
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
    }
}
