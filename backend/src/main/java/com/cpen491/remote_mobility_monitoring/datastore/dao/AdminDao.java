package com.cpen491.remote_mobility_monitoring.datastore.dao;

import com.cpen491.remote_mobility_monitoring.datastore.exception.DuplicateRecordException;
import com.cpen491.remote_mobility_monitoring.datastore.exception.RecordDoesNotExistException;
import com.cpen491.remote_mobility_monitoring.datastore.model.Admin;
import com.cpen491.remote_mobility_monitoring.datastore.model.Organization;
import com.cpen491.remote_mobility_monitoring.dependency.utility.Validator;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.AdminTable;

@Slf4j
@AllArgsConstructor
public class AdminDao {
    @NonNull
    GenericDao<Admin> genericDao;
    @NonNull
    OrganizationDao organizationDao;

    /**
     * Creates a new Admin record. Record with the given email must not already exist.
     *
     * @param newRecord The Admin record to create
     * @throws RecordDoesNotExistException If Organization record with given organizationId does not exist
     * @throws DuplicateRecordException If record with the given email already exists
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if any of email, firstName, lastName, or organizationId are empty
     */
    public void create(Admin newRecord) {
        log.info("Creating new Admin record {}", newRecord);
        Validator.validateAdmin(newRecord);

        if (organizationDao.findById(newRecord.getOrganizationId()) == null) {
            throw new RecordDoesNotExistException(Organization.class.getSimpleName(), newRecord.getOrganizationId());
        }

        if (findByEmail(newRecord.getEmail()) != null) {
            throw new DuplicateRecordException(Admin.class.getSimpleName(), newRecord.getEmail());
        }

        genericDao.create(newRecord);
    }

    /**
     * Finds an Admin record by id. Returns null if record does not exist.
     *
     * @param id The id of the record to find
     * @return {@link Admin}
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if id is empty
     */
    public Admin findById(String id) {
        log.info("Finding Admin record with id [{}]", id);
        Validator.validateId(id);

        Admin found = genericDao.findByPartitionKey(id);
        if (found == null) {
            log.info("Cannot find Admin record with id [{}]", id);
        }
        return found;
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

        Admin found = genericDao.findOneByIndexPartitionKey(AdminTable.EMAIL_INDEX_NAME, email);
        if (found == null) {
            log.info("Cannot find Admin record with email [{}]", email);
        }
        return found;
    }

    /**
     * Updates an Admin record. Record with given id must already exist.
     *
     * @param updatedRecord The Admin record to update
     * @throws RecordDoesNotExistException If record with the given id does not exist
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if any of id, email,
     *                              firstName, lastName, or organizationId are empty
     */
    public void update(Admin updatedRecord) {
        log.info("Updating Admin record {}", updatedRecord);
        Validator.validateAdmin(updatedRecord);
        Validator.validateId(updatedRecord.getId());

        genericDao.update(updatedRecord, Admin.class);
    }

    /**
     * Deletes an Admin record by id. Does nothing if record does not exist.
     *
     * @param id The id of the record to delete
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if id is empty
     */
    public void delete(String id) {
        log.info("Deleting Admin record with id [{}]", id);
        Validator.validateId(id);

        genericDao.delete(id);
    }
}
