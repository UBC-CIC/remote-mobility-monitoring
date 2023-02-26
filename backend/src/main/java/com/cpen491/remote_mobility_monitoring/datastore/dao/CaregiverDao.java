package com.cpen491.remote_mobility_monitoring.datastore.dao;

import com.cpen491.remote_mobility_monitoring.datastore.exception.DuplicateRecordException;
import com.cpen491.remote_mobility_monitoring.datastore.exception.RecordDoesNotExistException;
import com.cpen491.remote_mobility_monitoring.datastore.model.Caregiver;
import com.cpen491.remote_mobility_monitoring.datastore.model.Organization;
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
import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.OrganizationTable;
import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.PatientTable;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.DynamoDbUtils.getBoolFromMap;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.DynamoDbUtils.getFromMap;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.TimeUtils.getCurrentUtcTimeString;

@Slf4j
@AllArgsConstructor
public class CaregiverDao {
    @NonNull
    private GenericDao genericDao;
    @NonNull
    private OrganizationDao organizationDao;
    @NonNull
    private PatientDao patientDao;

    /**
     * Creates a new Caregiver record and adds it to an organization. Record with the given email must not already exist.
     *
     * @param newRecord The Caregiver record to create
     * @param organizationId The id of the Organization record
     * @throws RecordDoesNotExistException If Organization record with given organizationId does not exist
     * @throws DuplicateRecordException If record with the given email already exists
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if any of pid, sid, email, firstName, lastName,
     *                              title, phoneNumber, or organizationId are empty
     */
    public void create(Caregiver newRecord, String organizationId) {
        log.info("Creating new Caregiver record {}", newRecord);
        Validator.validateCaregiver(newRecord);
        Validator.validateOrganizationId(organizationId);

        Organization organization = organizationDao.findById(organizationId);

        if (findByEmail(newRecord.getEmail()) != null) {
            log.error("Caregiver record with email [{}] already exists", newRecord.getEmail());
            throw new DuplicateRecordException(Caregiver.class.getSimpleName(), newRecord.getEmail());
        }

        genericDao.setDate(newRecord);
        Map<String, AttributeValue> caregiverMap = Caregiver.convertToMap(newRecord);
        genericDao.put(caregiverMap);
        genericDao.addAssociation(Organization.convertToMap(organization), caregiverMap);
    }

    /**
     * Finds an unverified primary Caregiver record.
     *
     * @param patientId The id of the Patient record
     * @param caregiverId The id of the Caregiver record
     * @return {@link Caregiver}
     * @throws RecordDoesNotExistException If Patient or Caregiver records do not exist, or if Caregiver is not
     *                                     unverified primary Caregiver of Patient
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if patientId or caregiverId is empty or invalid
     */
    public Caregiver findUnverifiedPrimaryCaregiver(String patientId, String caregiverId) {
        log.info("Finding primary Caregiver [{}] with Patient [{}]", caregiverId, patientId);

        GetItemResponse response = findPatientCaregiverAssociation(patientId, caregiverId);
        if (!response.hasItem()) {
            log.error("Cannot find Caregiver [{}] and Patient [{}] association", caregiverId, patientId);
            throw new RecordDoesNotExistException(Caregiver.class.getSimpleName(), patientId + ":" + caregiverId);
        }

        Caregiver caregiver = Caregiver.convertUnverifiedPrimaryFromMap(response.item());
        if (caregiver.getAuthCode() == null) {
            log.error("Caregiver [{}] is not unverified primary Caregiver of Patient [{}]", caregiverId, patientId);
            throw new RecordDoesNotExistException(Caregiver.class.getSimpleName(), patientId + ":" + caregiverId);
        }

        genericDao.setCorrectId(caregiver, CaregiverTable.ID_PREFIX);
        return caregiver;
    }

    /**
     * Checks whether Caregiver is the primary Caregiver of Patient.
     *
     * @param patientId The id of the Patient record
     * @param caregiverId The id of the Caregiver record
     * @return {@link Boolean}
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if patientId or caregiverId is empty or invalid
     */
    public boolean isPrimaryCaregiverOfPatient(String patientId, String caregiverId) {
        log.info("Checking that Caregiver [{}] is primary Caregiver of Patient [{}]", caregiverId, patientId);

        GetItemResponse response = findPatientCaregiverAssociation(patientId, caregiverId);
        if (!response.hasItem()) {
            return false;
        }
        return Boolean.TRUE.equals(getBoolFromMap(response.item(), CaregiverTable.IS_PRIMARY_NAME));
    }

    /**
     * Checks whether Caregiver has Patient.
     *
     * @param patientId The id of the Patient record
     * @param caregiverId The id of the Caregiver record
     * @return {@link Boolean}
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if patientId or caregiverId is empty or invalid
     */
    public boolean hasPatient(String patientId, String caregiverId) {
        log.info("Checking that Caregiver [{}] has Patient [{}]", caregiverId, patientId);

        GetItemResponse response = findPatientCaregiverAssociation(patientId, caregiverId);
        return response.hasItem();
    }

    private GetItemResponse findPatientCaregiverAssociation(String patientId, String caregiverId) {
        Validator.validatePatientId(patientId);
        Validator.validateCaregiverId(caregiverId);

        return genericDao.findByPrimaryKey(caregiverId, patientId);
    }

    /**
     * Adds a Patient to a Caregiver. The Caregiver will be set as the primary Caregiver.
     * The adding process will only be completed after the patient accepts the add using the authCode.
     * Patient and Caregiver must already exist.
     *
     * @param patientEmail The email of the Patient record
     * @param caregiverId The id of the Caregiver record
     * @param authCode The authCode
     * @throws RecordDoesNotExistException If Patient or Caregiver records do not exist
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if patientEmail or caregiverId is empty or invalid
     */
    public void addPatientPrimary(String patientEmail, String caregiverId, String authCode) {
        log.info("Adding Patient [{}] to primary Caregiver [{}]", patientEmail, caregiverId);
        Validator.validateEmail(patientEmail);
        Validator.validateCaregiverId(caregiverId);
        Validator.validateAuthCode(authCode);

        Patient patient = patientDao.findByEmail(patientEmail);
        if (patient == null) {
            throw new RecordDoesNotExistException(Patient.class.getSimpleName(), patientEmail);
        }
        Caregiver caregiver = findById(caregiverId);
        caregiver.setAuthCode(authCode);
        caregiver.setAuthCodeTimestamp(getCurrentUtcTimeString());

        genericDao.addAssociation(Caregiver.convertPrimaryToMap(caregiver), Patient.convertToMap(patient));
    }

    /**
     * Accepts a Caregiver as the primary Caregiver of a Patient.
     *
     * @param patientId The id of the Patient record
     * @param caregiverId The id of the Caregiver record
     * @throws RecordDoesNotExistException If Patient or Caregiver records do not exist
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if patientId or caregiverId is empty or invalid
     */
    public void acceptPatientPrimary(String patientId, String caregiverId) {
        log.info("Accepting Caregiver [{}] as primary Caregiver for Patient [{}]", caregiverId, patientId);

        addPatient(patientId, caregiverId, true);
    }

    /**
     * Adds a Patient to a Caregiver. Patient and Caregiver must already exist.
     *
     * @param patientId The id of the Patient record
     * @param caregiverId The id of the Caregiver record
     * @throws RecordDoesNotExistException If Patient or Caregiver records do not exist
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if patientId or caregiverId is empty or invalid
     */
    public void addPatient(String patientId, String caregiverId) {
        log.info("Adding Patient [{}] to Caregiver [{}]", patientId, caregiverId);

        addPatient(patientId, caregiverId, false);
    }

    private void addPatient(String patientId, String caregiverId, boolean primary) {
        Validator.validatePatientId(patientId);
        Validator.validateCaregiverId(caregiverId);

        Patient patient = patientDao.findById(patientId);
        Caregiver caregiver = findById(caregiverId);

        Map<String, AttributeValue> caregiverMap;
        if (primary) {
            caregiverMap = Caregiver.convertPrimaryToMap(caregiver);
        } else {
            caregiverMap = Caregiver.convertToMap(caregiver);
        }

        genericDao.addAssociation(caregiverMap, Patient.convertToMap(patient));
    }

    /**
     * Removes a Patient from a Caregiver.
     *
     * @param patientId The id of the Patient record
     * @param caregiverId The id of the Caregiver record
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if patientId or caregiverId is empty or invalid
     */
    public void removePatient(String patientId, String caregiverId) {
        log.info("Removing Patient [{}] from Caregiver [{}]", patientId, caregiverId);
        Validator.validatePatientId(patientId);
        Validator.validateCaregiverId(caregiverId);

        genericDao.deleteByPrimaryKey(caregiverId, patientId);
    }

    /**
     * Finds a Caregiver record by id.
     *
     * @param id The id of the record to find
     * @return {@link Caregiver}
     * @throws RecordDoesNotExistException If record with the given id does not exist
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if id is empty or invalid
     */
    public Caregiver findById(String id) {
        log.info("Finding Caregiver record with id [{}]", id);
        Validator.validateCaregiverId(id);

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
                .findAllByPartitionKeyOnIndex(CaregiverTable.EMAIL_NAME, email, CaregiverTable.EMAIL_INDEX_NAME);
        if (!response.hasItems() || response.items().size() == 0) {
            log.info("Cannot find Caregiver record with email [{}]", email);
            return null;
        }
        Caregiver caregiver = Caregiver.convertFromMap(response.items().get(0));
        genericDao.setCorrectId(caregiver, CaregiverTable.ID_PREFIX);
        return caregiver;
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

    /**
     * Find the Organization this Caregiver belongs to.
     *
     * @param caregiverId The id of the Caregiver record
     * @return {@link Organization}
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if caregiverId is empty or invalid
     */
    public Organization findOrganization(String caregiverId) {
        log.info("Finding Organization of Caregiver [{}]", caregiverId);
        Validator.validateCaregiverId(caregiverId);

        QueryResponse response = genericDao
                .findAllAssociationsOnSidIndex(caregiverId, OrganizationTable.ID_PREFIX);
        if (!response.hasItems() || response.items().size() == 0) {
            log.info("Cannot find Organization of Caregiver [{}]", caregiverId);
            return null;
        }
        Organization organization = Organization.convertFromMap(response.items().get(0));
        organization.setSid(organization.getPid());
        return organization;
    }

    /**
     * Find all Patients of this Caregiver.
     *
     * @param caregiverId The id of the Caregiver record
     * @return {@link List}
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if caregiverId is empty or invalid
     */
    public List<Patient> findAllPatients(String caregiverId) {
        log.info("Finding all Patient records of Caregiver [{}]", caregiverId);
        Validator.validateCaregiverId(caregiverId);

        List<Map<String, AttributeValue>> result = genericDao
                .findAllAssociations(caregiverId, PatientTable.ID_PREFIX).items();
        return result.stream().map(map -> {
            Patient patient = Patient.convertFromMap(map);
            patient.setPid(patient.getSid());
            if (getBoolFromMap(map, CaregiverTable.IS_PRIMARY_NAME)) {
                patient.setIsPrimary(true);
                patient.setVerified(getFromMap(map, CaregiverTable.AUTH_CODE_NAME) == null);
            }
            return patient;
        }).collect(Collectors.toList());
    }

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

        Caregiver found = findByEmail(updatedRecord.getEmail());
        if (found != null && !found.getPid().equals(updatedRecord.getPid())) {
            log.error("Caregiver record with email [{}] already exists", updatedRecord.getEmail());
            throw new DuplicateRecordException(Caregiver.class.getSimpleName(), updatedRecord.getEmail());
        }

        findById(updatedRecord.getPid());

        genericDao.update(Caregiver.convertToMap(updatedRecord));
    }

    /**
     * Deletes a Caregiver record by id and all of its associations. Does nothing if record does not exist.
     *
     * @param id The id of the record to delete
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if id is empty or invalid
     */
    public void delete(String id) {
        log.info("Deleting Caregiver record with id [{}]", id);
        Validator.validateCaregiverId(id);

        genericDao.delete(id);
    }
}
