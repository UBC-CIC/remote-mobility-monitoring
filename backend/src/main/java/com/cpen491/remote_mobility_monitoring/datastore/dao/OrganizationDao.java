package com.cpen491.remote_mobility_monitoring.datastore.dao;

import com.cpen491.remote_mobility_monitoring.datastore.exception.DuplicateRecordException;
import com.cpen491.remote_mobility_monitoring.datastore.exception.RecordDoesNotExistException;
import com.cpen491.remote_mobility_monitoring.datastore.model.Const;
import com.cpen491.remote_mobility_monitoring.datastore.model.Organization;
import com.cpen491.remote_mobility_monitoring.dependency.utility.Validator;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class OrganizationDao {
    @NonNull
    GenericDao<Organization> dao;

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

        dao.create(newRecord);
    }

    /**
     * Finds an Organization record by id. Returns null if record does not exist.
     *
     * @param id The id of the record to find
     * @return {@link Organization}
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if id is empty
     */
    public Organization findById(String id) {
        log.info("Finding Organization record with id [{}]", id);
        Validator.validateId(id);

        Organization found = dao.findByPartitionKey(id);
        if (found == null) {
            log.info("Cannot find Organization record with id [{}]", id);
        }
        return found;
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

        Organization found = dao.findOneByIndexPartitionKey(Const.OrganizationTable.NAME_INDEX_NAME, name);
        if (found == null) {
            log.info("Cannot find Organization record with name [{}]", name);
        }
        return found;
    }

    /**
     * Updates an Organization record. Record with given ID must already exist.
     *
     * @param updatedRecord The Organization record to update
     * @throws RecordDoesNotExistException If record with the given ID does not exist
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if id or name are empty
     */
    public void update(Organization updatedRecord) {
        log.info("Updating Organization record {}", updatedRecord);
        Validator.validateOrganization(updatedRecord);
        Validator.validateId(updatedRecord.getId());

        dao.update(updatedRecord, Organization.class);
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

        dao.delete(id);

        // TODO: delete all admins, caregivers, and patients
    }
}
