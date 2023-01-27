package com.cpen491.remote_mobility_monitoring.datastore.dao;

import com.cpen491.remote_mobility_monitoring.datastore.exception.DuplicateRecordException;
import com.cpen491.remote_mobility_monitoring.datastore.exception.RecordDoesNotExistException;
import com.cpen491.remote_mobility_monitoring.datastore.model.Caregiver;
import com.cpen491.remote_mobility_monitoring.datastore.model.Patient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.cpen491.remote_mobility_monitoring.TestUtils.assertInvalidInputExceptionThrown;
import static com.cpen491.remote_mobility_monitoring.TestUtils.buildCaregiver;
import static com.cpen491.remote_mobility_monitoring.TestUtils.buildPatient;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.AUTH_CODE_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.AUTH_CODE_TIMESTAMP_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.CAREGIVER_ID_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.CAREGIVER_ID_INVALID_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.DATE_OF_BIRTH_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.DEVICE_ID_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.FIRST_NAME_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.IDS_NULL_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.ID_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.LAST_NAME_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.PATIENT_ID_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.PATIENT_ID_INVALID_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.PATIENT_RECORD_NULL_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.PHONE_NUMBER_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.PID_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.PID_NOT_EQUAL_SID_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.SID_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.VERIFIED_NULL_ERROR_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PatientDaoTest extends DaoTestParent {
    private static final String PID = "pat-1";
    private static final String SID = PID;
    private static final String DEVICE_ID1 = "device-id-1";
    private static final String DEVICE_ID2 = "device-id-2";
    private static final String FIRST_NAME = "Jack";
    private static final String LAST_NAME = "Jackson";
    private static final String DATE_OF_BIRTH = "2000-12-31";
    private static final String PHONE_NUMBER = "1234567890";
    private static final String AUTH_CODE = "auth_code-123";
    private static final String AUTH_CODE_TIMESTAMP = "2020-01-01T05:00:00.000000";
    private static final boolean VERIFIED = false;
    private static final String CAREGIVER_ID1 = "car-1";
    private static final String CAREGIVER_ID2 = "car-2";
    private static final String EMAIL1 = "caregiver1@email.com";
    private static final String EMAIL2 = "caregiver2@email.com";

    PatientDao cut;

    @BeforeEach
    public void setup() {
        setupTable();
        GenericDao genericDao = new GenericDao(ddbClient);
        OrganizationDao organizationDao = new OrganizationDao(genericDao);
        CaregiverDao caregiverDao = new CaregiverDao(genericDao, organizationDao);
        cut = new PatientDao(genericDao, caregiverDao);
    }

    @AfterEach
    public void teardown() {
        teardownTable();
    }

    @Test
    public void testCreate_HappyCase() {
        Patient newRecord = buildPatientDefault();
        cut.create(newRecord);

        assertNotEquals(PID, newRecord.getPid());
        assertEquals(DEVICE_ID1, newRecord.getDeviceId());
        assertEquals(FIRST_NAME, newRecord.getFirstName());
        assertEquals(LAST_NAME, newRecord.getLastName());
        assertEquals(DATE_OF_BIRTH, newRecord.getDateOfBirth());
        assertEquals(PHONE_NUMBER, newRecord.getPhoneNumber());
        assertEquals(AUTH_CODE, newRecord.getAuthCode());
        assertEquals(AUTH_CODE_TIMESTAMP, newRecord.getAuthCodeTimestamp());
        assertEquals(VERIFIED, newRecord.getVerified());
        assertNotNull(newRecord.getCreatedAt());
        assertNotNull(newRecord.getUpdatedAt());
    }

    @Test
    public void testCreate_WHEN_RecordWithDeviceIdAlreadyExists_THEN_ThrowDuplicateRecordException() {
        Patient newRecord = buildPatientDefault();
        cut.create(newRecord);
        assertThatThrownBy(() -> cut.create(newRecord)).isInstanceOf(DuplicateRecordException.class);
    }

    @Test
    public void testCreate_WHEN_RecordHasNoDeviceId_THEN_NoThrow() {
        Patient newRecord = buildPatientDefault();
        newRecord.setDeviceId(null);
        cut.create(newRecord);
        cut.create(newRecord);
    }

    @ParameterizedTest
    @MethodSource("invalidInputsForCreate")
    public void testCreate_WHEN_InvalidInput_THEN_ThrowInvalidInputException(Patient record, String errorMessage) {
        assertInvalidInputExceptionThrown(() -> cut.create(record), errorMessage);
    }

    private static Stream<Arguments> invalidInputsForCreate() {
        return Stream.of(
                Arguments.of(null, PATIENT_RECORD_NULL_ERROR_MESSAGE),
                Arguments.of(buildPatient(PID, SID, DEVICE_ID1, null, LAST_NAME, DATE_OF_BIRTH, PHONE_NUMBER, AUTH_CODE,
                        AUTH_CODE_TIMESTAMP, VERIFIED), FIRST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildPatient(PID, SID, DEVICE_ID1, "", LAST_NAME, DATE_OF_BIRTH, PHONE_NUMBER, AUTH_CODE,
                        AUTH_CODE_TIMESTAMP, VERIFIED), FIRST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildPatient(PID, SID, DEVICE_ID1, FIRST_NAME, null, DATE_OF_BIRTH, PHONE_NUMBER, AUTH_CODE,
                        AUTH_CODE_TIMESTAMP, VERIFIED), LAST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildPatient(PID, SID, DEVICE_ID1, FIRST_NAME, "", DATE_OF_BIRTH, PHONE_NUMBER, AUTH_CODE,
                        AUTH_CODE_TIMESTAMP, VERIFIED), LAST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildPatient(PID, SID, DEVICE_ID1, FIRST_NAME, LAST_NAME, null, PHONE_NUMBER, AUTH_CODE,
                        AUTH_CODE_TIMESTAMP, VERIFIED), DATE_OF_BIRTH_BLANK_ERROR_MESSAGE),
                Arguments.of(buildPatient(PID, SID, DEVICE_ID1, FIRST_NAME, LAST_NAME, "", PHONE_NUMBER, AUTH_CODE,
                        AUTH_CODE_TIMESTAMP, VERIFIED), DATE_OF_BIRTH_BLANK_ERROR_MESSAGE),
                Arguments.of(buildPatient(PID, SID, DEVICE_ID1, FIRST_NAME, LAST_NAME, DATE_OF_BIRTH, null, AUTH_CODE,
                        AUTH_CODE_TIMESTAMP, VERIFIED), PHONE_NUMBER_BLANK_ERROR_MESSAGE),
                Arguments.of(buildPatient(PID, SID, DEVICE_ID1, FIRST_NAME, LAST_NAME, DATE_OF_BIRTH, "", AUTH_CODE,
                        AUTH_CODE_TIMESTAMP, VERIFIED), PHONE_NUMBER_BLANK_ERROR_MESSAGE),
                Arguments.of(buildPatient(PID, SID, DEVICE_ID1, FIRST_NAME, LAST_NAME, DATE_OF_BIRTH, PHONE_NUMBER, null,
                        AUTH_CODE_TIMESTAMP, VERIFIED), AUTH_CODE_BLANK_ERROR_MESSAGE),
                Arguments.of(buildPatient(PID, SID, DEVICE_ID1, FIRST_NAME, LAST_NAME, DATE_OF_BIRTH, PHONE_NUMBER, "",
                        AUTH_CODE_TIMESTAMP, VERIFIED), AUTH_CODE_BLANK_ERROR_MESSAGE),
                Arguments.of(buildPatient(PID, SID, DEVICE_ID1, FIRST_NAME, LAST_NAME, DATE_OF_BIRTH, PHONE_NUMBER, AUTH_CODE,
                        null, VERIFIED), AUTH_CODE_TIMESTAMP_BLANK_ERROR_MESSAGE),
                Arguments.of(buildPatient(PID, SID, DEVICE_ID1, FIRST_NAME, LAST_NAME, DATE_OF_BIRTH, PHONE_NUMBER, AUTH_CODE,
                        "", VERIFIED), AUTH_CODE_TIMESTAMP_BLANK_ERROR_MESSAGE),
                Arguments.of(buildPatient(PID, SID, DEVICE_ID1, FIRST_NAME, LAST_NAME, DATE_OF_BIRTH, PHONE_NUMBER, AUTH_CODE,
                        AUTH_CODE_TIMESTAMP, null), VERIFIED_NULL_ERROR_MESSAGE)
        );
    }

    // HappyCase tested together with testFindAllCaregivers
    @Test
    public void testAddCaregiver_WHEN_PatientRecordDoesNotExist_THEN_ThrowRecordDoesNotExistException() {
        assertThatThrownBy(() -> cut.addCaregiver(PID, CAREGIVER_ID1)).isInstanceOf(RecordDoesNotExistException.class);
    }

    @Test
    public void testAddCaregiver_WHEN_CaregiverRecordDoesNotExist_THEN_ThrowRecordDoesNotExistException() {
        Patient newRecord = buildPatientDefault();
        cut.create(newRecord);
        assertThatThrownBy(() -> cut.addCaregiver(newRecord.getPid(), CAREGIVER_ID1)).isInstanceOf(RecordDoesNotExistException.class);
    }

    @ParameterizedTest
    @MethodSource("invalidInputsForAddCaregiver")
    public void testAddCaregiver_WHEN_InvalidInput_THEN_ThrowInvalidInputException(String patientId, String caregiverId, String errorMessage) {
        assertInvalidInputExceptionThrown(() -> cut.addCaregiver(patientId, caregiverId), errorMessage);
    }

    private static Stream<Arguments> invalidInputsForAddCaregiver() {
        return Stream.of(
                Arguments.of(null, CAREGIVER_ID1, PATIENT_ID_BLANK_ERROR_MESSAGE),
                Arguments.of("", CAREGIVER_ID1, PATIENT_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(CAREGIVER_ID1, CAREGIVER_ID1, PATIENT_ID_INVALID_ERROR_MESSAGE),
                Arguments.of(PID, null, CAREGIVER_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(PID, "", CAREGIVER_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(PID, PID, CAREGIVER_ID_INVALID_ERROR_MESSAGE)
        );
    }

    @Test
    public void testFindById_HappyCase() {
        Patient newRecord = buildPatientDefault();
        createPatient(newRecord);

        Patient record = cut.findById(PID);
        assertEquals(newRecord, record);
    }

    @Test
    public void testFindById_WHEN_RecordDoesNotExist_THEN_ThrowRecordDoesNotExistException() {
        assertThatThrownBy(() -> cut.findById(PID)).isInstanceOf(RecordDoesNotExistException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void testFindById_WHEN_InvalidInput_THEN_ThrowInvalidInputException(String id) {
        assertInvalidInputExceptionThrown(() -> cut.findById(id), ID_BLANK_ERROR_MESSAGE);
    }

    @Test
    public void testFindByDeviceId_HappyCase() {
        Patient newRecord = buildPatientDefault();
        createPatient(newRecord);

        Patient record = cut.findByDeviceId(DEVICE_ID1);
        assertEquals(newRecord, record);
    }

    @Test
    public void testFindByDeviceId_WHEN_RecordDoesNotExist_THEN_ReturnNull() {
        Patient record = cut.findByDeviceId(DEVICE_ID1);
        assertNull(record);
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void testFindByDeviceId_WHEN_InvalidInput_THEN_ThrowInvalidInputException(String deviceId) {
        assertInvalidInputExceptionThrown(() -> cut.findByDeviceId(deviceId), DEVICE_ID_BLANK_ERROR_MESSAGE);
    }

    @Test
    public void testBatchFindById_HappyCase() {
        Patient newRecord1 = buildPatientDefault();
        cut.create(newRecord1);
        Patient newRecord2 = buildPatientDefault();
        newRecord2.setDeviceId(DEVICE_ID2);
        cut.create(newRecord2);

        List<String> ids = Arrays.asList(newRecord1.getPid(), newRecord2.getPid());
        List<Patient> results = cut.batchFindById(ids);
        assertThat(results).containsExactlyInAnyOrder(newRecord1, newRecord2);
    }

    @Test
    public void testBatchFindById_WHEN_RecordsDoNotExist_THEN_ReturnEmptyList() {
        List<String> ids = Arrays.asList(PID);
        List<Patient> results = cut.batchFindById(ids);
        assertThat(results).isEmpty();
    }

    @Test
    public void testBatchFindById_WHEN_InvalidInput_THEN_ThrowInvalidInputException() {
        assertInvalidInputExceptionThrown(() -> cut.batchFindById(null), IDS_NULL_ERROR_MESSAGE);
    }

    @Test
    public void testFindAllCaregivers_HappyCase() {
        Caregiver caregiver1 = buildCaregiver(CAREGIVER_ID1, CAREGIVER_ID1, EMAIL1, null, null, null, null);
        createCaregiver(caregiver1);
        Caregiver caregiver2 = buildCaregiver(CAREGIVER_ID2, CAREGIVER_ID2, EMAIL2, null, null, null, null);
        createCaregiver(caregiver2);
        Patient patient = buildPatientDefault();
        createPatient(patient);

        cut.addCaregiver(patient.getPid(), CAREGIVER_ID1);
        cut.addCaregiver(patient.getPid(), CAREGIVER_ID2);
        List<Caregiver> caregivers = cut.findAllCaregivers(patient.getPid()).stream().peek(caregiver -> {
            caregiver.setCreatedAt(null);
            caregiver.setUpdatedAt(null);
        }).collect(Collectors.toList());
        assertThat(caregivers).containsExactlyInAnyOrder(caregiver1, caregiver2);
    }

    @Test
    public void testFindAllCaregivers_WHEN_PatientRecordDoesNotExist_THEN_ReturnEmptyList() {
        List<Caregiver> caregivers = cut.findAllCaregivers(PID);
        assertThat(caregivers).isEmpty();
    }

    @Test
    public void testFindAllCaregivers_WHEN_NoCaregiverRecordsAssociated_THEN_ReturnEmptyList() {
        Patient patient = buildPatientDefault();
        createPatient(patient);

        List<Caregiver> caregivers = cut.findAllCaregivers(patient.getPid());
        assertThat(caregivers).isEmpty();
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void testFindAllCaregivers_WHEN_InvalidInput_THEN_ThrowInvalidInputException(String patientId) {
        assertInvalidInputExceptionThrown(() -> cut.findAllCaregivers(patientId), PATIENT_ID_BLANK_ERROR_MESSAGE);
    }

    @Test
    public void testUpdate_HappyCase() {
        Patient newRecord = buildPatientDefault();
        cut.create(newRecord);

        Patient updatedRecord = cut.findById(newRecord.getPid());
        assertEquals(newRecord, updatedRecord);
        updatedRecord.setVerified(true);
        cut.update(updatedRecord);

        Patient found = cut.findById(newRecord.getPid());
        assertEquals(newRecord.getPid(), found.getPid());
        assertNotEquals(newRecord.getVerified(), found.getVerified());
        assertNotEquals(newRecord.getUpdatedAt(), found.getUpdatedAt());
        assertEquals(newRecord.getCreatedAt(), found.getCreatedAt());
    }

    @Test
    public void testUpdate_WHEN_RecordWithDeviceIdAlreadyExists_THEN_ThrowDuplicateRecordException() {
        Patient newRecord1 = buildPatientDefault();
        cut.create(newRecord1);
        Patient newRecord2 = buildPatientDefault();
        newRecord2.setDeviceId(DEVICE_ID2);
        cut.create(newRecord2);

        Patient updatedRecord = cut.findById(newRecord2.getPid());
        updatedRecord.setDeviceId(DEVICE_ID1);
        assertThatThrownBy(() -> cut.update(updatedRecord)).isInstanceOf(DuplicateRecordException.class);
    }

    @Test
    public void testUpdate_WHEN_RecordDoesNotExist_THEN_ThrowRecordDoesNotExistException() {
        Patient newRecord = buildPatientDefault();
        assertThatThrownBy(() -> cut.update(newRecord)).isInstanceOf(RecordDoesNotExistException.class);
    }

    @ParameterizedTest
    @MethodSource("invalidInputsForUpdate")
    public void testUpdate_WHEN_InvalidInput_THEN_ThrowInvalidInputException(Patient record, String errorMessage) {
        assertInvalidInputExceptionThrown(() -> cut.update(record), errorMessage);
    }

    private static Stream<Arguments> invalidInputsForUpdate() {
        return Stream.of(
                Arguments.of(null, PATIENT_RECORD_NULL_ERROR_MESSAGE),
                Arguments.of(buildPatient(null, SID, DEVICE_ID1, FIRST_NAME, LAST_NAME, DATE_OF_BIRTH, PHONE_NUMBER, AUTH_CODE,
                        AUTH_CODE_TIMESTAMP, VERIFIED), PID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildPatient("", SID, DEVICE_ID1, FIRST_NAME, LAST_NAME, DATE_OF_BIRTH, PHONE_NUMBER, AUTH_CODE,
                        AUTH_CODE_TIMESTAMP, VERIFIED), PID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildPatient(PID, null, DEVICE_ID1, FIRST_NAME, LAST_NAME, DATE_OF_BIRTH, PHONE_NUMBER, AUTH_CODE,
                        AUTH_CODE_TIMESTAMP, VERIFIED), SID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildPatient(PID, "", DEVICE_ID1, FIRST_NAME, LAST_NAME, DATE_OF_BIRTH, PHONE_NUMBER, AUTH_CODE,
                        AUTH_CODE_TIMESTAMP, VERIFIED), SID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildPatient(PID, SID, null, FIRST_NAME, LAST_NAME, DATE_OF_BIRTH, PHONE_NUMBER, AUTH_CODE,
                        AUTH_CODE_TIMESTAMP, VERIFIED), DEVICE_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildPatient(PID, SID, "", FIRST_NAME, LAST_NAME, DATE_OF_BIRTH, PHONE_NUMBER, AUTH_CODE,
                        AUTH_CODE_TIMESTAMP, VERIFIED), DEVICE_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildPatient(PID, SID, DEVICE_ID1, null, LAST_NAME, DATE_OF_BIRTH, PHONE_NUMBER, AUTH_CODE,
                        AUTH_CODE_TIMESTAMP, VERIFIED), FIRST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildPatient(PID, SID, DEVICE_ID1, "", LAST_NAME, DATE_OF_BIRTH, PHONE_NUMBER, AUTH_CODE,
                        AUTH_CODE_TIMESTAMP, VERIFIED), FIRST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildPatient(PID, SID, DEVICE_ID1, FIRST_NAME, null, DATE_OF_BIRTH, PHONE_NUMBER, AUTH_CODE,
                        AUTH_CODE_TIMESTAMP, VERIFIED), LAST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildPatient(PID, SID, DEVICE_ID1, FIRST_NAME, "", DATE_OF_BIRTH, PHONE_NUMBER, AUTH_CODE,
                        AUTH_CODE_TIMESTAMP, VERIFIED), LAST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildPatient(PID, SID, DEVICE_ID1, FIRST_NAME, LAST_NAME, null, PHONE_NUMBER, AUTH_CODE,
                        AUTH_CODE_TIMESTAMP, VERIFIED), DATE_OF_BIRTH_BLANK_ERROR_MESSAGE),
                Arguments.of(buildPatient(PID, SID, DEVICE_ID1, FIRST_NAME, LAST_NAME, "", PHONE_NUMBER, AUTH_CODE,
                        AUTH_CODE_TIMESTAMP, VERIFIED), DATE_OF_BIRTH_BLANK_ERROR_MESSAGE),
                Arguments.of(buildPatient(PID, SID, DEVICE_ID1, FIRST_NAME, LAST_NAME, DATE_OF_BIRTH, null, AUTH_CODE,
                        AUTH_CODE_TIMESTAMP, VERIFIED), PHONE_NUMBER_BLANK_ERROR_MESSAGE),
                Arguments.of(buildPatient(PID, SID, DEVICE_ID1, FIRST_NAME, LAST_NAME, DATE_OF_BIRTH, "", AUTH_CODE,
                        AUTH_CODE_TIMESTAMP, VERIFIED), PHONE_NUMBER_BLANK_ERROR_MESSAGE),
                Arguments.of(buildPatient(PID, SID, DEVICE_ID1, FIRST_NAME, LAST_NAME, DATE_OF_BIRTH, PHONE_NUMBER, null,
                        AUTH_CODE_TIMESTAMP, VERIFIED), AUTH_CODE_BLANK_ERROR_MESSAGE),
                Arguments.of(buildPatient(PID, SID, DEVICE_ID1, FIRST_NAME, LAST_NAME, DATE_OF_BIRTH, PHONE_NUMBER, "",
                        AUTH_CODE_TIMESTAMP, VERIFIED), AUTH_CODE_BLANK_ERROR_MESSAGE),
                Arguments.of(buildPatient(PID, SID, DEVICE_ID1, FIRST_NAME, LAST_NAME, DATE_OF_BIRTH, PHONE_NUMBER, AUTH_CODE,
                        null, VERIFIED), AUTH_CODE_TIMESTAMP_BLANK_ERROR_MESSAGE),
                Arguments.of(buildPatient(PID, SID, DEVICE_ID1, FIRST_NAME, LAST_NAME, DATE_OF_BIRTH, PHONE_NUMBER, AUTH_CODE,
                        "", VERIFIED), AUTH_CODE_TIMESTAMP_BLANK_ERROR_MESSAGE),
                Arguments.of(buildPatient(PID, SID, DEVICE_ID1, FIRST_NAME, LAST_NAME, DATE_OF_BIRTH, PHONE_NUMBER, AUTH_CODE,
                        AUTH_CODE_TIMESTAMP, null), VERIFIED_NULL_ERROR_MESSAGE),
                Arguments.of(buildPatient(PID, SID + "1", DEVICE_ID1, FIRST_NAME, LAST_NAME, DATE_OF_BIRTH, PHONE_NUMBER, AUTH_CODE,
                        AUTH_CODE_TIMESTAMP, VERIFIED), PID_NOT_EQUAL_SID_ERROR_MESSAGE)
        );
    }

    @Test
    public void testDelete_HappyCase() {
        Patient newRecord = buildPatientDefault();
        createPatient(newRecord);
        Patient found = cut.findById(newRecord.getPid());
        assertNotNull(found);

        cut.delete(PID);
        assertThatThrownBy(() -> cut.findById(newRecord.getPid())).isInstanceOf(RecordDoesNotExistException.class);
    }

    @Test
    public void testDelete_WHEN_RecordDoesNotExist_THEN_DoNothing() {
        assertDoesNotThrow(() -> cut.delete(PID));
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void testDelete_WHEN_InvalidInput_THEN_ThrowInvalidInputException(String id) {
        assertInvalidInputExceptionThrown(() -> cut.delete(id), ID_BLANK_ERROR_MESSAGE);
    }

    private static Patient buildPatientDefault() {
        return buildPatient(PID, SID, DEVICE_ID1, FIRST_NAME, LAST_NAME, DATE_OF_BIRTH, PHONE_NUMBER,
                AUTH_CODE, AUTH_CODE_TIMESTAMP, VERIFIED);
    }
}
