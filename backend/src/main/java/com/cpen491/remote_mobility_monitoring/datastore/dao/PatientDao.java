package com.cpen491.remote_mobility_monitoring.datastore.dao;

import com.cpen491.remote_mobility_monitoring.datastore.exception.DuplicateRecordException;
import com.cpen491.remote_mobility_monitoring.datastore.exception.RecordDoesNotExistException;
import com.cpen491.remote_mobility_monitoring.datastore.model.Caregiver;
import com.cpen491.remote_mobility_monitoring.datastore.model.Patient;
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

import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.CaregiverTable;
import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.PatientTable;

@Slf4j
@AllArgsConstructor
public class PatientDao {
    @NonNull
    GenericDao genericDao;

    /**
     * Creates a new Patient record. If deviceId is provided, record with the deviceId must not already exist.
     *
     * @param newRecord The Patient record to create
     * @throws DuplicateRecordException If record with the given deviceId already exists
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if any of firstName, lastName,
     *                              dateOfBirth, phoneNumber, authCode, authCodeTimestamp, or verified are empty
     */
    public void create(Patient newRecord) {
        log.info("Creating new Patient record {}", newRecord);
        Validator.validatePatient(newRecord);

        String deviceId = newRecord.getDeviceId();
        if (deviceId != null && findByDeviceId(deviceId) != null) {
            log.error("Patient record with device id [{}] already exists", deviceId);
            throw new DuplicateRecordException(Patient.class.getSimpleName(), deviceId);
        }

        genericDao.setIdAndDate(newRecord, PatientTable.ID_PREFIX);
        genericDao.put(Patient.convertToMap(newRecord));
    }

    /**
     * Finds a Patient record by id.
     *
     * @param id The id of the record to find
     * @return {@link Patient}
     * @throws RecordDoesNotExistException If record with the given id does not exist
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if id is empty or invalid
     */
    public Patient findById(String id) {
        log.info("Finding Patient record with id [{}]", id);
        Validator.validatePatientId(id);

        GetItemResponse response = genericDao.findByPartitionKey(id);
        if (!response.hasItem()) {
            log.error("Cannot find Patient record with id [{}]", id);
            throw new RecordDoesNotExistException(Patient.class.getSimpleName(), id);
        }
        return Patient.convertFromMap(response.item());
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

        QueryResponse response = genericDao
                .findAllByPartitionKeyOnIndex(PatientTable.DEVICE_ID_NAME, deviceId, PatientTable.DEVICE_ID_INDEX_NAME);
        if (!response.hasItems() || response.items().size() == 0) {
            log.info("Cannot find Patient record with deviceId [{}]", deviceId);
            return null;
        }
        Patient patient = Patient.convertFromMap(response.items().get(0));
        genericDao.setCorrectId(patient, PatientTable.ID_PREFIX);
        return patient;
    }

    /**
     * Batch finds all Patient records by IDs.
     *
     * @param ids Set of IDs of the records to find
     * @return {@link List}
     * @throws NullPointerException If ids is null
     */
    public List<Patient> batchFindById(List<String> ids) {
        log.info("Batch finding Patient records matching IDs {}", ids);
        Validator.validateIds(ids);
        for (String id : ids) {
            Validator.validatePatientId(id);
        }

        List<Map<String, AttributeValue>> result = genericDao.batchFindByPartitionKey(ids);
        return result.stream().map(Patient::convertFromMap).collect(Collectors.toList());
    }

    /**
     * Find all Caregivers caring for this patient.
     *
     * @param patientId The id of the Patient record
     * @return {@link List}
     * @throws RecordDoesNotExistException If record with the given patientId does not exist
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if patientId is empty or invalid
     */
    public List<Caregiver> findAllCaregivers(String patientId) {
        log.info("Finding all Caregiver records caring for Patient [{}]", patientId);
        Validator.validatePatientId(patientId);

        findById(patientId);

        List<Map<String, AttributeValue>> result = genericDao
                .findAllAssociationsOnSidIndex(patientId, CaregiverTable.ID_PREFIX).items();
        return result.stream().map(map -> {
            Caregiver caregiver = Caregiver.convertFromMap(map);
            caregiver.setSid(caregiver.getPid());
            return caregiver;
        }).collect(Collectors.toList());
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
     *                              dateOfBirth, or phoneNumber are empty
     */
    public void update(Patient updatedRecord) {
        log.info("Updating Patient record {}", updatedRecord);
        Validator.validatePatient(updatedRecord);
        Validator.validatePidEqualsSid(updatedRecord.getPid(), updatedRecord.getSid());
        Validator.validatePatientId(updatedRecord.getPid());
        Validator.validateDeviceId(updatedRecord.getDeviceId());

        Patient found = findByDeviceId(updatedRecord.getDeviceId());
        if (found != null && !found.getPid().equals(updatedRecord.getPid())) {
            log.error("Patient record with device id [{}] already exists", updatedRecord.getDeviceId());
            throw new DuplicateRecordException(Patient.class.getSimpleName(), updatedRecord.getDeviceId());
        }

        findById(updatedRecord.getPid());

        genericDao.update(Patient.convertToMap(updatedRecord));
    }

    /**
     * Deletes a Patient record by id and all of its associations. Does nothing if record does not exist.
     *
     * @param id The id of the record to delete
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if id is empty or invalid
     */
    public void delete(String id) {
        log.info("Deleting Patient record with id [{}]", id);
        Validator.validatePatientId(id);

        genericDao.delete(id);
    }
}
