package com.cpen491.remote_mobility_monitoring.datastore.dao;

import com.cpen491.remote_mobility_monitoring.datastore.exception.DuplicateRecordException;
import com.cpen491.remote_mobility_monitoring.datastore.exception.RecordDoesNotExistException;
import com.cpen491.remote_mobility_monitoring.datastore.model.Patient;
import com.cpen491.remote_mobility_monitoring.dependency.utility.Validator;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.PatientTable;

@Slf4j
@AllArgsConstructor
public class PatientDao {
    @NonNull
    GenericDao<Patient> genericDao;

    /**
     * Creates a new Patient record. If deviceId is provided, record with the deviceId must not already exist.
     *
     * @param newRecord The Patient record to create
     * @throws DuplicateRecordException If record with the given deviceId already exists
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if any of deviceId, firstName, lastName,
     *                              dateOfBirth, or phoneNumber are empty
     */
    public void create(Patient newRecord) {
        log.info("Creating new Patient record {}", newRecord);
        Validator.validatePatient(newRecord);

        String deviceId = newRecord.getDeviceId();
        if (deviceId != null && findByDeviceId(deviceId) != null) {
            throw new DuplicateRecordException(Patient.class.getSimpleName(), deviceId);
        }

        newRecord.setAuthCode(UUID.randomUUID().toString().replace("-", ""));
        String currentTime = LocalDateTime.now(ZoneOffset.UTC).toString();
        newRecord.setAuthCodeTimestamp(currentTime);
        newRecord.setVerified(false);
        genericDao.create(newRecord);
    }

    /**
     * Finds a Patient record by id. Returns null if record does not exist.
     *
     * @param id The id of the record to find
     * @return {@link Patient}
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if id is empty
     */
    public Patient findById(String id) {
        log.info("Finding Patient record with id [{}]", id);
        Validator.validateId(id);

        Patient found = genericDao.findByPartitionKey(id);
        if (found == null) {
            log.info("Cannot find Patient record with id [{}]", id);
        }
        return found;
    }

    /**
     * Finds a Patient record by deviceId. Returns null if record does not exist.
     *
     * @param deviceId The deviceId of the record to find
     * @return {@link Patient}
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if deviceId is empty
     */
    public Patient findByDeviceId(String deviceId) {
        log.info("Finding Patient record with deviceId [{}]", deviceId);
        Validator.validateDeviceId(deviceId);

        Patient found = genericDao.findOneByIndexPartitionKey(PatientTable.DEVICE_ID_INDEX_NAME, deviceId);
        if (found == null) {
            log.info("Cannot find Patient record with deviceId [{}]", deviceId);
        }
        return found;
    }

    /**
     * Batch finds all Patient records by IDs.
     *
     * @param ids Set of IDs of the records to find
     * @return {@link List}
     * @throws NullPointerException If ids is null
     */
    public List<Patient> batchFindById(Set<String> ids) {
        log.info("Batch finding patient records matching IDs {}", ids);
        Validator.validateIds(ids);

        return genericDao.batchFindByPartitionKey(ids, Patient.class);
    }

    /**
     * Updates a Patient record. Record with given id must already exist.
     * Record with given deviceId should not already exist unless it is the same record being updated.
     *
     * @param updatedRecord The Patient record to update
     * @throws DuplicateRecordException If record with the given deviceId already exists
     * @throws RecordDoesNotExistException If record with the given id does not exist
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if any of id, deviceId, firstName, lastName,
     *      *                              dateOfBirth, or phoneNumber are empty
     */
    public void update(Patient updatedRecord) {
        log.info("Updating Patient record {}", updatedRecord);
        Validator.validatePatient(updatedRecord);
        Validator.validateId(updatedRecord.getId());
        Validator.validateDeviceId(updatedRecord.getDeviceId());

        Patient found = findByDeviceId(updatedRecord.getDeviceId());
        if (found != null && !found.getId().equals(updatedRecord.getId())) {
            throw new DuplicateRecordException(Patient.class.getSimpleName(), updatedRecord.getDeviceId());
        }

        genericDao.update(updatedRecord, Patient.class);
    }

    /**
     * Deletes a Patient record by id. Does nothing if record does not exist.
     *
     * @param id The id of the record to delete
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if id is empty
     */
    public void delete(String id) {
        log.info("Deleting Patient record with id [{}]", id);
        Validator.validateId(id);

        genericDao.delete(id);

        // TODO: remove ID from any caregiver patientIds
    }
}
