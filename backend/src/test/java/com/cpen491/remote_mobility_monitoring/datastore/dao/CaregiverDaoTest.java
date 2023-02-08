package com.cpen491.remote_mobility_monitoring.datastore.dao;

import com.cpen491.remote_mobility_monitoring.datastore.exception.DuplicateRecordException;
import com.cpen491.remote_mobility_monitoring.datastore.exception.RecordDoesNotExistException;
import com.cpen491.remote_mobility_monitoring.datastore.model.Caregiver;
import com.cpen491.remote_mobility_monitoring.datastore.model.Organization;
import com.cpen491.remote_mobility_monitoring.datastore.model.Patient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.cpen491.remote_mobility_monitoring.TestUtils.assertInvalidInputExceptionThrown;
import static com.cpen491.remote_mobility_monitoring.TestUtils.buildCaregiver;
import static com.cpen491.remote_mobility_monitoring.TestUtils.buildOrganization;
import static com.cpen491.remote_mobility_monitoring.TestUtils.buildPatient;
import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.BaseTable;
import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.CaregiverTable;
import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.OrganizationTable;
import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.PatientTable;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.CAREGIVER_ID_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.CAREGIVER_ID_INVALID_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.CAREGIVER_RECORD_NULL_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.EMAIL_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.FIRST_NAME_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.IDS_NULL_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.LAST_NAME_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.ORGANIZATION_ID_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.ORGANIZATION_ID_INVALID_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.PATIENT_ID_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.PATIENT_ID_INVALID_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.PHONE_NUMBER_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.PID_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.PID_NOT_EQUAL_SID_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.SID_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.TITLE_BLANK_ERROR_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CaregiverDaoTest extends DaoTestParent {
    private static final String PID = "car-1";
    private static final String PID2 = "car-2";
    private static final String SID = PID;
    private static final String SID2 = PID2;
    private static final String EMAIL1 = "janedoe@email.com";
    private static final String EMAIL2 = "janedoeiscool@email.com";
    private static final String FIRST_NAME = "Jane";
    private static final String LAST_NAME = "Doe";
    private static final String TITLE = "caregiver";
    private static final String PHONE_NUMBER = "1234567890";
    private static final String EXISTS_ORGANIZATION_ID = "org-1";
    private static final String NOT_EXISTS_ORGANIZATION_ID = "org-2";
    private static final String ORGANIZATION_NAME = "ORG1";
    private static final String PATIENT_ID1 = "pat-1";
    private static final String PATIENT_ID2 = "pat-2";
    private static final String DEVICE_ID1 = "device-id-1";
    private static final String DEVICE_ID2 = "device-id-2";

    CaregiverDao cut;

    @BeforeEach
    public void setup() {
        setupTable();
        OrganizationDao organizationDao = new OrganizationDao(genericDao);
        PatientDao patientDao = new PatientDao(genericDao);
        cut = new CaregiverDao(genericDao, organizationDao, patientDao);

        Organization organization = buildOrganization(EXISTS_ORGANIZATION_ID, EXISTS_ORGANIZATION_ID, ORGANIZATION_NAME);
        createOrganization(organization);
    }

    @AfterEach
    public void teardown() {
        teardownTable();
    }

    @Test
    public void testCreate_HappyCase() {
        Caregiver newRecord = buildCaregiverDefault();
        cut.create(newRecord, EXISTS_ORGANIZATION_ID);

        GetItemResponse response = findByPrimaryKey(newRecord.getPid(), newRecord.getPid());
        assertTrue(response.hasItem());

        newRecord = Caregiver.convertFromMap(response.item());
        assertEquals(PID, newRecord.getPid());
        assertEquals(SID, newRecord.getSid());
        assertEquals(EMAIL1, newRecord.getEmail());
        assertEquals(FIRST_NAME, newRecord.getFirstName());
        assertEquals(LAST_NAME, newRecord.getLastName());
        assertEquals(TITLE, newRecord.getTitle());
        assertEquals(PHONE_NUMBER, newRecord.getPhoneNumber());
        assertNotNull(newRecord.getCreatedAt());
        assertNotNull(newRecord.getUpdatedAt());
        GetItemResponse response2 = findByPrimaryKey(EXISTS_ORGANIZATION_ID, newRecord.getPid());
        assertTrue(response2.hasItem());
        assertEquals(ORGANIZATION_NAME, response2.item().get(OrganizationTable.NAME_NAME).s());
        assertEquals(EMAIL1, response2.item().get(CaregiverTable.EMAIL_NAME).s());
    }

    @Test
    public void testCreate_WHEN_OrganizationDoesNotExist_THEN_ThrowRecordDoesNotExistException() {
        Caregiver newRecord = buildCaregiverDefault();
        assertThatThrownBy(() -> cut.create(newRecord, NOT_EXISTS_ORGANIZATION_ID)).isInstanceOf(RecordDoesNotExistException.class);
    }

    @Test
    public void testCreate_WHEN_RecordWithEmailAlreadyExists_THEN_ThrowDuplicateRecordException() {
        Caregiver newRecord = buildCaregiverDefault();
        cut.create(newRecord, EXISTS_ORGANIZATION_ID);
        newRecord.setPid(PID2);
        newRecord.setSid(SID2);
        assertThatThrownBy(() -> cut.create(newRecord, EXISTS_ORGANIZATION_ID)).isInstanceOf(DuplicateRecordException.class);
    }

    @ParameterizedTest
    @MethodSource("invalidInputsForCreate")
    public void testCreate_WHEN_InvalidInput_THEN_ThrowInvalidInputException(Caregiver record, String organizationId, String errorMessage) {
        assertInvalidInputExceptionThrown(() -> cut.create(record, organizationId), errorMessage);
    }

    private static Stream<Arguments> invalidInputsForCreate() {
        return Stream.of(
                Arguments.of(null, EXISTS_ORGANIZATION_ID, CAREGIVER_RECORD_NULL_ERROR_MESSAGE),
                Arguments.of(buildCaregiver(null, SID, EMAIL1, FIRST_NAME, LAST_NAME, TITLE, PHONE_NUMBER),
                        EXISTS_ORGANIZATION_ID, PID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCaregiver("", SID, EMAIL1, FIRST_NAME, LAST_NAME, TITLE, PHONE_NUMBER),
                        EXISTS_ORGANIZATION_ID, PID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCaregiver(PID, null, EMAIL1, FIRST_NAME, LAST_NAME, TITLE, PHONE_NUMBER),
                        EXISTS_ORGANIZATION_ID, SID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCaregiver(PID, "", EMAIL1, FIRST_NAME, LAST_NAME, TITLE, PHONE_NUMBER),
                        EXISTS_ORGANIZATION_ID, SID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCaregiver(PID, SID, null, FIRST_NAME, LAST_NAME, TITLE, PHONE_NUMBER),
                        EXISTS_ORGANIZATION_ID, EMAIL_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCaregiver(PID, SID, "", FIRST_NAME, LAST_NAME, TITLE, PHONE_NUMBER),
                        EXISTS_ORGANIZATION_ID, EMAIL_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCaregiver(PID, SID, EMAIL1, null, LAST_NAME, TITLE, PHONE_NUMBER),
                        EXISTS_ORGANIZATION_ID, FIRST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCaregiver(PID, SID, EMAIL1, "", LAST_NAME, TITLE, PHONE_NUMBER),
                        EXISTS_ORGANIZATION_ID, FIRST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCaregiver(PID, SID, EMAIL1, FIRST_NAME, null, TITLE, PHONE_NUMBER),
                        EXISTS_ORGANIZATION_ID, LAST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCaregiver(PID, SID, EMAIL1, FIRST_NAME, "", TITLE, PHONE_NUMBER),
                        EXISTS_ORGANIZATION_ID, LAST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCaregiver(PID, SID, EMAIL1, FIRST_NAME, LAST_NAME, null, PHONE_NUMBER),
                        EXISTS_ORGANIZATION_ID, TITLE_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCaregiver(PID, SID, EMAIL1, FIRST_NAME, LAST_NAME, "", PHONE_NUMBER),
                        EXISTS_ORGANIZATION_ID, TITLE_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCaregiver(PID, SID, EMAIL1, FIRST_NAME, LAST_NAME, TITLE, null),
                        EXISTS_ORGANIZATION_ID, PHONE_NUMBER_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCaregiver(PID, SID, EMAIL1, FIRST_NAME, LAST_NAME, TITLE, ""),
                        EXISTS_ORGANIZATION_ID, PHONE_NUMBER_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCaregiver(PID, SID, EMAIL1, FIRST_NAME, LAST_NAME, TITLE, PHONE_NUMBER),
                        null, ORGANIZATION_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCaregiver(PID, SID, EMAIL1, FIRST_NAME, LAST_NAME, TITLE, PHONE_NUMBER),
                        "", ORGANIZATION_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCaregiver(PID, SID, EMAIL1, FIRST_NAME, LAST_NAME, TITLE, PHONE_NUMBER),
                        PID, ORGANIZATION_ID_INVALID_ERROR_MESSAGE),
                Arguments.of(buildCaregiver(PATIENT_ID1, PATIENT_ID1, EMAIL1, FIRST_NAME, LAST_NAME, TITLE, PHONE_NUMBER),
                        EXISTS_ORGANIZATION_ID, CAREGIVER_ID_INVALID_ERROR_MESSAGE),
                Arguments.of(buildCaregiver(PID, SID + "1", EMAIL1, FIRST_NAME, LAST_NAME, TITLE, PHONE_NUMBER),
                        EXISTS_ORGANIZATION_ID, PID_NOT_EQUAL_SID_ERROR_MESSAGE)
        );
    }

    @Test
    public void testHasPatient_HappyCase() {
        Patient patient = buildPatientDefault();
        createPatient(patient);
        Caregiver caregiver = buildCaregiverDefault();
        createCaregiver(caregiver);
        putPrimaryKey(PID, PATIENT_ID1);

        boolean result = cut.hasPatient(PATIENT_ID1, PID);
        assertTrue(result);
    }

    @Test
    public void testHasPatient_WHEN_PatientRecordDoesNotExist_THEN_ThrowRecordDoesNotExistException() {
        Caregiver caregiver = buildCaregiverDefault();
        createCaregiver(caregiver);
        assertThatThrownBy(() -> cut.hasPatient(PATIENT_ID1, PID)).isInstanceOf(RecordDoesNotExistException.class);
    }

    @Test
    public void testHasPatient_WHEN_CaregiverRecordDoesNotExist_THEN_ThrowRecordDoesNotExistException() {
        Patient patient = buildPatientDefault();
        createPatient(patient);
        assertThatThrownBy(() -> cut.hasPatient(PATIENT_ID1, PID)).isInstanceOf(RecordDoesNotExistException.class);
    }

    @Test
    public void testHasPatient_WHEN_PatientNotAdded_THEN_ReturnFalse() {
        Patient patient = buildPatientDefault();
        createPatient(patient);
        Caregiver caregiver = buildCaregiverDefault();
        createCaregiver(caregiver);

        boolean result = cut.hasPatient(PATIENT_ID1, PID);
        assertFalse(result);
    }

    @ParameterizedTest
    @MethodSource("invalidInputsForHasPatient")
    public void testHasPatient_WHEN_InvalidInput_THEN_ThrowInvalidInputException(String patientId, String caregiverId, String errorMessage) {
        assertInvalidInputExceptionThrown(() -> cut.hasPatient(patientId, caregiverId), errorMessage);
    }

    private static Stream<Arguments> invalidInputsForHasPatient() {
        return Stream.of(
                Arguments.of(null, PID, PATIENT_ID_BLANK_ERROR_MESSAGE),
                Arguments.of("", PID, PATIENT_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(PID, PID, PATIENT_ID_INVALID_ERROR_MESSAGE),
                Arguments.of(PATIENT_ID1, null, CAREGIVER_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(PATIENT_ID1, "", CAREGIVER_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(PATIENT_ID1, PATIENT_ID1, CAREGIVER_ID_INVALID_ERROR_MESSAGE)
        );
    }

    @Test
    public void testAddPatient_HappyCase() {
        Patient patient1 = buildPatientDefault();
        createPatient(patient1);
        Caregiver caregiver = buildCaregiverDefault();
        createCaregiver(caregiver);
        cut.addPatient(PATIENT_ID1, PID);

        GetItemResponse response1 = findByPrimaryKey(PID, PATIENT_ID1);
        assertTrue(response1.hasItem());
        assertEquals(PID, response1.item().get(BaseTable.PID_NAME).s());
        assertEquals(EMAIL1, response1.item().get(CaregiverTable.EMAIL_NAME).s());
        assertEquals(FIRST_NAME, response1.item().get(CaregiverTable.FIRST_NAME_NAME).s());
        assertEquals(PATIENT_ID1, response1.item().get(BaseTable.SID_NAME).s());
        assertNull(response1.item().get(PatientTable.FIRST_NAME_NAME));

        GetItemResponse response2 = findByPrimaryKey(PID, PATIENT_ID2);
        assertFalse(response2.hasItem());

        Patient patient2 = buildPatient(PATIENT_ID2, PATIENT_ID2, DEVICE_ID2, null, null, null, null,
                null, null);
        createPatient(patient2);
        cut.addPatient(PATIENT_ID2, PID);

        GetItemResponse response3 = findByPrimaryKey(PID, PATIENT_ID2);
        assertTrue(response3.hasItem());

        GetItemResponse response4 = findByPrimaryKey(PATIENT_ID1, PID);
        assertFalse(response4.hasItem());
    }

    @Test
    public void testAddPatient_WHEN_PatientRecordDoesNotExist_THEN_ThrowRecordDoesNotExistException() {
        Caregiver caregiver = buildCaregiverDefault();
        createCaregiver(caregiver);
        assertThatThrownBy(() -> cut.addPatient(PATIENT_ID1, PID)).isInstanceOf(RecordDoesNotExistException.class);
    }

    @Test
    public void testAddPatient_WHEN_CaregiverRecordDoesNotExist_THEN_ThrowRecordDoesNotExistException() {
        Patient patient = buildPatientDefault();
        createPatient(patient);
        assertThatThrownBy(() -> cut.addPatient(PATIENT_ID1, PID)).isInstanceOf(RecordDoesNotExistException.class);
    }

    @Test
    public void testAddPatient_WHEN_PatientAlreadyAdded_THEN_ThrowDuplicateRecordException() {
        Caregiver caregiver = buildCaregiverDefault();
        createCaregiver(caregiver);
        Patient patient = buildPatientDefault();
        createPatient(patient);
        cut.addPatient(PATIENT_ID1, PID);

        assertThatThrownBy(() -> cut.addPatient(PATIENT_ID1, PID)).isInstanceOf(DuplicateRecordException.class);
    }

    @ParameterizedTest
    @MethodSource("invalidInputsForAddPatient")
    public void testAddPatient_WHEN_InvalidInput_THEN_ThrowInvalidInputException(String patientId, String caregiverId, String errorMessage) {
        assertInvalidInputExceptionThrown(() -> cut.addPatient(patientId, caregiverId), errorMessage);
    }

    private static Stream<Arguments> invalidInputsForAddPatient() {
        return Stream.of(
                Arguments.of(null, PID, PATIENT_ID_BLANK_ERROR_MESSAGE),
                Arguments.of("", PID, PATIENT_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(PID, PID, PATIENT_ID_INVALID_ERROR_MESSAGE),
                Arguments.of(PATIENT_ID1, null, CAREGIVER_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(PATIENT_ID1, "", CAREGIVER_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(PATIENT_ID1, PATIENT_ID1, CAREGIVER_ID_INVALID_ERROR_MESSAGE)
        );
    }

    @Test
    public void testRemovePatient_HappyCase() {
        Patient patient = buildPatientDefault();
        createPatient(patient);
        Caregiver caregiver = buildCaregiverDefault();
        createCaregiver(caregiver);
        cut.addPatient(PATIENT_ID1, PID);

        GetItemResponse response1 = findByPrimaryKey(PID, PATIENT_ID1);
        assertTrue(response1.hasItem());

        cut.removePatient(PATIENT_ID1, PID);

        GetItemResponse response2 = findByPrimaryKey(PID, PATIENT_ID1);
        assertFalse(response2.hasItem());
    }

    @Test
    public void testRemovePatient_WHEN_RecordsDoNotExist_THEN_DoNothing() {
        cut.removePatient(PATIENT_ID1, PID);
    }

    @ParameterizedTest
    @MethodSource("invalidInputsForRemovePatient")
    public void testRemovePatient_WHEN_InvalidInput_THEN_ThrowInvalidInputException(String patientId, String caregiverId, String errorMessage) {
        assertInvalidInputExceptionThrown(() -> cut.removePatient(patientId, caregiverId), errorMessage);
    }

    private static Stream<Arguments> invalidInputsForRemovePatient() {
        return Stream.of(
                Arguments.of(null, PID, PATIENT_ID_BLANK_ERROR_MESSAGE),
                Arguments.of("", PID, PATIENT_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(PID, PID, PATIENT_ID_INVALID_ERROR_MESSAGE),
                Arguments.of(PATIENT_ID1, null, CAREGIVER_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(PATIENT_ID1, "", CAREGIVER_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(PATIENT_ID1, PATIENT_ID1, CAREGIVER_ID_INVALID_ERROR_MESSAGE)
        );
    }

    @Test
    public void testFindById_HappyCase() {
        Caregiver newRecord = buildCaregiverDefault();
        createCaregiver(newRecord);

        Caregiver record = cut.findById(newRecord.getPid());
        assertEquals(newRecord, record);
    }

    @Test
    public void testFindById_WHEN_RecordDoesNotExist_THEN_ThrowRecordDoesNotExistException() {
        assertThatThrownBy(() -> cut.findById(PID)).isInstanceOf(RecordDoesNotExistException.class);
    }

    @ParameterizedTest
    @MethodSource("invalidInputsForFindById")
    public void testFindById_WHEN_InvalidInput_THEN_ThrowInvalidInputException(String id, String errorMessage) {
        assertInvalidInputExceptionThrown(() -> cut.findById(id), errorMessage);
    }

    private static Stream<Arguments> invalidInputsForFindById() {
        return Stream.of(
                Arguments.of(null, CAREGIVER_ID_BLANK_ERROR_MESSAGE),
                Arguments.of("", CAREGIVER_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(PATIENT_ID1, CAREGIVER_ID_INVALID_ERROR_MESSAGE)
        );
    }

    @Test
    public void testFindByEmail_HappyCase() {
        Caregiver newRecord = buildCaregiverDefault();
        createCaregiver(newRecord);

        Caregiver record = cut.findByEmail(EMAIL1);
        assertEquals(newRecord, record);
    }

    @Test
    public void testFindByEmail_WHEN_RecordDoesNotExist_THEN_ReturnNull() {
        Caregiver record = cut.findByEmail(EMAIL1);
        assertNull(record);
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void testFindByEmail_WHEN_InvalidInput_THEN_ThrowInvalidInputException(String email) {
        assertInvalidInputExceptionThrown(() -> cut.findByEmail(email), EMAIL_BLANK_ERROR_MESSAGE);
    }

    @Test
    public void testBatchFindById_HappyCase() {
        Caregiver newRecord1 = buildCaregiverDefault();
        cut.create(newRecord1, EXISTS_ORGANIZATION_ID);
        Caregiver newRecord2 = buildCaregiverDefault();
        newRecord2.setPid(PID2);
        newRecord2.setSid(SID2);
        newRecord2.setEmail(EMAIL2);
        cut.create(newRecord2, EXISTS_ORGANIZATION_ID);

        List<String> ids = Arrays.asList(newRecord1.getPid(), newRecord2.getPid());
        List<Caregiver> results = cut.batchFindById(ids);
        assertThat(results).containsExactlyInAnyOrder(newRecord1, newRecord2);
    }

    @Test
    public void testBatchFindById_WHEN_RecordsDoNotExist_THEN_ReturnEmptyList() {
        List<String> ids = Arrays.asList(PID);
        List<Caregiver> results = cut.batchFindById(ids);
        assertThat(results).isEmpty();
    }

    @Test
    public void testBatchFindById_WHEN_InvalidInput_THEN_ThrowInvalidInputException() {
        assertInvalidInputExceptionThrown(() -> cut.batchFindById(null), IDS_NULL_ERROR_MESSAGE);
    }

    @Test
    public void testFindOrganization_HappyCase() {
        Caregiver newRecord = buildCaregiverDefault();
        cut.create(newRecord, EXISTS_ORGANIZATION_ID);

        Organization organization = cut.findOrganization(newRecord.getPid());
        assertNotNull(organization);
        assertEquals(EXISTS_ORGANIZATION_ID, organization.getPid());
        assertEquals(EXISTS_ORGANIZATION_ID, organization.getSid());
        assertEquals(ORGANIZATION_NAME, organization.getName());
    }

    @Test
    public void testFindOrganization_WHEN_CaregiverRecordDoesNotExist_THEN_ThrowRecordDoesNotExistException() {
        assertThatThrownBy(() -> cut.findOrganization(PID)).isInstanceOf(RecordDoesNotExistException.class);
    }

    @Test
    public void testFindOrganization_WHEN_NoOrganizationRecordAssociated_THEN_ReturnNull() {
        Caregiver newRecord = buildCaregiverDefault();
        createCaregiver(newRecord);

        Organization organization = cut.findOrganization(PID);
        assertNull(organization);
    }

    @ParameterizedTest
    @MethodSource("invalidInputsForFindOrganization")
    public void testFindOrganization_WHEN_InvalidInput_THEN_ThrowInvalidInputException(String caregiverId, String errorMessage) {
        assertInvalidInputExceptionThrown(() -> cut.findOrganization(caregiverId), errorMessage);
    }

    private static Stream<Arguments> invalidInputsForFindOrganization() {
        return Stream.of(
                Arguments.of(null, CAREGIVER_ID_BLANK_ERROR_MESSAGE),
                Arguments.of("", CAREGIVER_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(PATIENT_ID1, CAREGIVER_ID_INVALID_ERROR_MESSAGE)
        );
    }

    @Test
    public void testFindAllPatients_HappyCase() {
        Patient patient1 = buildPatientDefault();
        createPatient(patient1);
        Patient patient2 = buildPatient(PATIENT_ID2, PATIENT_ID2, DEVICE_ID2, null, null, null, null,
                null, null);
        createPatient(patient2);
        Caregiver caregiver = buildCaregiverDefault();
        createCaregiver(caregiver);

        cut.addPatient(PATIENT_ID1, PID);
        cut.addPatient(PATIENT_ID2, PID);
        List<Patient> patients = cut.findAllPatients(PID).stream().peek(patient -> {
            patient.setCreatedAt(null);
            patient.setUpdatedAt(null);
        }).collect(Collectors.toList());
        assertThat(patients).containsExactlyInAnyOrder(patient1, patient2);
    }

    @Test
    public void testFindAllPatients_WHEN_CaregiverRecordDoesNotExist_THEN_ThrowRecordDoesNotExistException() {
        assertThatThrownBy(() -> cut.findAllPatients(PID)).isInstanceOf(RecordDoesNotExistException.class);
    }

    @Test
    public void testFindAllPatients_WHEN_NoPatientRecordsAssociated_THEN_ReturnEmptyList() {
        Caregiver caregiver = buildCaregiverDefault();
        createCaregiver(caregiver);

        List<Patient> patients = cut.findAllPatients(PID);
        assertThat(patients).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("invalidInputsForFindAllPatients")
    public void testFindAllPatients_WHEN_InvalidInput_THEN_ThrowInvalidInputException(String caregiverId, String errorMessage) {
        assertInvalidInputExceptionThrown(() -> cut.findAllPatients(caregiverId), errorMessage);
    }

    private static Stream<Arguments> invalidInputsForFindAllPatients() {
        return Stream.of(
                Arguments.of(null, CAREGIVER_ID_BLANK_ERROR_MESSAGE),
                Arguments.of("", CAREGIVER_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(PATIENT_ID1, CAREGIVER_ID_INVALID_ERROR_MESSAGE)
        );
    }

    @Test
    public void testUpdate_HappyCase() {
        Caregiver newRecord = buildCaregiverDefault();
        cut.create(newRecord, EXISTS_ORGANIZATION_ID);

        Caregiver updatedRecord = cut.findById(newRecord.getPid());
        assertEquals(newRecord, updatedRecord);
        updatedRecord.setEmail(EMAIL2);
        cut.update(updatedRecord);

        Caregiver found = cut.findById(newRecord.getPid());
        assertEquals(newRecord.getPid(), found.getPid());
        assertNotEquals(newRecord.getEmail(), found.getEmail());
        assertNotEquals(newRecord.getUpdatedAt(), found.getUpdatedAt());
        assertEquals(newRecord.getCreatedAt(), found.getCreatedAt());
    }

    @Test
    public void testUpdate_WHEN_RecordWithEmailAlreadyExists_THEN_ThrowDuplicateRecordException() {
        Caregiver newRecord1 = buildCaregiverDefault();
        cut.create(newRecord1, EXISTS_ORGANIZATION_ID);
        Caregiver newRecord2 = buildCaregiverDefault();
        newRecord2.setPid(PID2);
        newRecord2.setSid(SID2);
        newRecord2.setEmail(EMAIL2);
        cut.create(newRecord2, EXISTS_ORGANIZATION_ID);

        Caregiver updatedRecord = cut.findById(newRecord2.getPid());
        updatedRecord.setEmail(EMAIL1);
        assertThatThrownBy(() -> cut.update(updatedRecord)).isInstanceOf(DuplicateRecordException.class);
    }

    @Test
    public void testUpdate_WHEN_RecordAlsoExistsInAssociations_THEN_UpdateAllDuplicateRecords() {
        Caregiver newRecord1 = buildCaregiverDefault();
        createCaregiver(newRecord1);
        Patient newRecord2 = buildPatientDefault();
        newRecord2.setPid(PID);
        createPatient(newRecord2);
        Caregiver newRecord3 = buildCaregiverDefault();
        newRecord3.setPid(EXISTS_ORGANIZATION_ID);
        createCaregiver(newRecord3);

        Caregiver updatedRecord = cut.findById(PID);
        updatedRecord.setEmail(EMAIL2);
        cut.update(updatedRecord);

        assertEquals(EMAIL2, findByPrimaryKey(PID, PID).item().get(CaregiverTable.EMAIL_NAME).s());
        assertEquals(EMAIL2, findByPrimaryKey(PID, PATIENT_ID1).item().get(CaregiverTable.EMAIL_NAME).s());
        assertEquals(DEVICE_ID1, findByPrimaryKey(PID, PATIENT_ID1).item().get(PatientTable.DEVICE_ID_NAME).s());
        assertEquals(EMAIL2, findByPrimaryKey(EXISTS_ORGANIZATION_ID, PID).item().get(CaregiverTable.EMAIL_NAME).s());
    }

    @Test
    public void testUpdate_WHEN_RecordDoesNotExist_THEN_ThrowRecordDoesNotExistException() {
        Caregiver newRecord = buildCaregiverDefault();
        assertThatThrownBy(() -> cut.update(newRecord)).isInstanceOf(RecordDoesNotExistException.class);
    }

    @ParameterizedTest
    @MethodSource("invalidInputsForUpdate")
    public void testUpdate_WHEN_InvalidInput_THEN_ThrowInvalidInputException(Caregiver record, String errorMessage) {
        assertInvalidInputExceptionThrown(() -> cut.update(record), errorMessage);
    }

    private static Stream<Arguments> invalidInputsForUpdate() {
        return Stream.of(
                Arguments.of(null, CAREGIVER_RECORD_NULL_ERROR_MESSAGE),
                Arguments.of(buildCaregiver(null, SID, EMAIL1, FIRST_NAME, LAST_NAME, TITLE, PHONE_NUMBER), PID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCaregiver("", SID, EMAIL1, FIRST_NAME, LAST_NAME, TITLE, PHONE_NUMBER), PID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCaregiver(PID, null, EMAIL1, FIRST_NAME, LAST_NAME, TITLE, PHONE_NUMBER), SID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCaregiver(PID, "", EMAIL1, FIRST_NAME, LAST_NAME, TITLE, PHONE_NUMBER), SID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCaregiver(PID, SID, null, FIRST_NAME, LAST_NAME, TITLE, PHONE_NUMBER), EMAIL_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCaregiver(PID, SID, "", FIRST_NAME, LAST_NAME, TITLE, PHONE_NUMBER), EMAIL_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCaregiver(PID, SID, EMAIL1, null, LAST_NAME, TITLE, PHONE_NUMBER), FIRST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCaregiver(PID, SID, EMAIL1, "", LAST_NAME, TITLE, PHONE_NUMBER), FIRST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCaregiver(PID, SID, EMAIL1, FIRST_NAME, null, TITLE, PHONE_NUMBER), LAST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCaregiver(PID, SID, EMAIL1, FIRST_NAME, "", TITLE, PHONE_NUMBER), LAST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCaregiver(PID, SID, EMAIL1, FIRST_NAME, LAST_NAME, null, PHONE_NUMBER), TITLE_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCaregiver(PID, SID, EMAIL1, FIRST_NAME, LAST_NAME, "", PHONE_NUMBER), TITLE_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCaregiver(PID, SID, EMAIL1, FIRST_NAME, LAST_NAME, TITLE, null), PHONE_NUMBER_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCaregiver(PID, SID, EMAIL1, FIRST_NAME, LAST_NAME, TITLE, ""), PHONE_NUMBER_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCaregiver(PATIENT_ID1, PATIENT_ID1, EMAIL1, FIRST_NAME, LAST_NAME, TITLE, PHONE_NUMBER), CAREGIVER_ID_INVALID_ERROR_MESSAGE),
                Arguments.of(buildCaregiver(PID, SID + "1", EMAIL1, FIRST_NAME, LAST_NAME, TITLE, PHONE_NUMBER), PID_NOT_EQUAL_SID_ERROR_MESSAGE)
        );
    }

    @Test
    public void testDelete_HappyCase() {
        Patient patient1 = buildPatientDefault();
        createPatient(patient1);
        Patient patient2 = buildPatient(PATIENT_ID2, PATIENT_ID2, DEVICE_ID2, null, null, null, null,
                null, null);
        createPatient(patient2);

        Caregiver newRecord = buildCaregiverDefault();
        cut.create(newRecord, EXISTS_ORGANIZATION_ID);
        cut.addPatient(PATIENT_ID1, newRecord.getPid());
        cut.addPatient(PATIENT_ID2, newRecord.getPid());

        Caregiver found = cut.findById(newRecord.getPid());
        assertNotNull(found);

        GetItemResponse response1 = findByPrimaryKey(newRecord.getPid(), PATIENT_ID1);
        GetItemResponse response2 = findByPrimaryKey(newRecord.getPid(), PATIENT_ID2);
        GetItemResponse response3 = findByPrimaryKey(EXISTS_ORGANIZATION_ID, newRecord.getPid());
        assertTrue(response1.hasItem());
        assertTrue(response2.hasItem());
        assertTrue(response3.hasItem());

        cut.delete(newRecord.getPid());
        assertThatThrownBy(() -> cut.findById(newRecord.getPid())).isInstanceOf(RecordDoesNotExistException.class);
        response1 = findByPrimaryKey(newRecord.getPid(), PATIENT_ID1);
        response2 = findByPrimaryKey(newRecord.getPid(), PATIENT_ID2);
        response3 = findByPrimaryKey(EXISTS_ORGANIZATION_ID, newRecord.getPid());
        assertFalse(response1.hasItem());
        assertFalse(response2.hasItem());
        assertFalse(response3.hasItem());
    }

    @Test
    public void testDelete_WHEN_RecordDoesNotExist_THEN_DoNothing() {
        assertDoesNotThrow(() -> cut.delete(PID));
    }

    @ParameterizedTest
    @MethodSource("invalidInputsForDelete")
    public void testDelete_WHEN_InvalidInput_THEN_ThrowInvalidInputException(String id, String errorMessage) {
        assertInvalidInputExceptionThrown(() -> cut.delete(id), errorMessage);
    }

    private static Stream<Arguments> invalidInputsForDelete() {
        return Stream.of(
                Arguments.of(null, CAREGIVER_ID_BLANK_ERROR_MESSAGE),
                Arguments.of("", CAREGIVER_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(PATIENT_ID1, CAREGIVER_ID_INVALID_ERROR_MESSAGE)
        );
    }

    private static Caregiver buildCaregiverDefault() {
        return buildCaregiver(PID, SID, EMAIL1, FIRST_NAME, LAST_NAME, TITLE, PHONE_NUMBER);
    }

    private static Patient buildPatientDefault() {
        return buildPatient(PATIENT_ID1, PATIENT_ID1, DEVICE_ID1, null, null, null, null,
                null, null);
    }
}
