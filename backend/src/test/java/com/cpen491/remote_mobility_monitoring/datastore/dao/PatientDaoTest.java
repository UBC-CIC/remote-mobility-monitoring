package com.cpen491.remote_mobility_monitoring.datastore.dao;

import com.cpen491.remote_mobility_monitoring.datastore.exception.DuplicateRecordException;
import com.cpen491.remote_mobility_monitoring.datastore.exception.RecordDoesNotExistException;
import com.cpen491.remote_mobility_monitoring.datastore.model.Patient;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.PatientTable;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.DATE_OF_BIRTH_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.DEVICE_ID_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.FIRST_NAME_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.IDS_NULL_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.ID_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.LAST_NAME_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.PATIENT_RECORD_NULL_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.PHONE_NUMBER_BLANK_ERROR_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PatientDaoTest extends DaoTestParent {
    private static final String ID = "patient-id-123";
    private static final String DEVICE_ID1 = "device-id-1";
    private static final String DEVICE_ID2 = "device-id-2";
    private static final String FIRST_NAME = "Jack";
    private static final String LAST_NAME = "Jackson";
    private static final String DATE_OF_BIRTH = "2000-12-31";
    private static final String PHONE_NUMBER = "1234567890";

    DynamoDbTable<Patient> table;
    PatientDao cut;

    @BeforeEach
    public void setup() {
        setupPatientTable();

        table = ddbEnhancedClient.table(PatientTable.TABLE_NAME, TableSchema.fromBean(Patient.class));
        Map<String, DynamoDbIndex<Patient>> indexMap = new HashMap<>();
        for (Pair<String, String> indexNameAndKey : PatientTable.INDEX_NAMES_AND_KEYS) {
            String indexName = indexNameAndKey.getLeft();
            indexMap.put(indexName, table.index(indexName));
        }
        cut = new PatientDao(new GenericDao<>(table, indexMap, ddbEnhancedClient));
    }

    @AfterEach
    public void teardown() {
        teardownPatientTable();
    }

    @Test
    public void testCreate_HappyCase() {
        Patient newRecord = buildPatient();
        cut.create(newRecord);

        assertNotEquals(ID, newRecord.getId());
        assertEquals(DEVICE_ID1, newRecord.getDeviceId());
        assertEquals(FIRST_NAME, newRecord.getFirstName());
        assertEquals(LAST_NAME, newRecord.getLastName());
        assertEquals(DATE_OF_BIRTH, newRecord.getDateOfBirth());
        assertEquals(PHONE_NUMBER, newRecord.getPhoneNumber());
        assertNotNull(newRecord.getAuthCode());
        assertNotNull(newRecord.getAuthCodeTimestamp());
        assertFalse(newRecord.getVerified());
        assertNotNull(newRecord.getCreatedAt());
        assertNotNull(newRecord.getUpdatedAt());
    }

    @Test
    public void testCreate_WHEN_RecordWithDeviceIdAlreadyExists_THEN_ThrowDuplicateRecordException() {
        Patient newRecord = buildPatient();
        cut.create(newRecord);
        assertThatThrownBy(() -> cut.create(newRecord)).isInstanceOf(DuplicateRecordException.class);
    }

    @ParameterizedTest
    @MethodSource("invalidInputsForCreate")
    public void testCreate_WHEN_InvalidInput_THEN_ThrowInvalidInputException(Patient record, String errorMessage) {
        assertInvalidInputExceptionThrown(() -> cut.create(record), errorMessage);
    }

    private static Stream<Arguments> invalidInputsForCreate() {
        return Stream.of(
                Arguments.of(null, PATIENT_RECORD_NULL_ERROR_MESSAGE),
                Arguments.of(buildPatient(ID, null, FIRST_NAME, LAST_NAME, DATE_OF_BIRTH, PHONE_NUMBER), DEVICE_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildPatient(ID, "", FIRST_NAME, LAST_NAME, DATE_OF_BIRTH, PHONE_NUMBER), DEVICE_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildPatient(ID, DEVICE_ID1, null, LAST_NAME, DATE_OF_BIRTH, PHONE_NUMBER), FIRST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildPatient(ID, DEVICE_ID1, "", LAST_NAME, DATE_OF_BIRTH, PHONE_NUMBER), FIRST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildPatient(ID, DEVICE_ID1, FIRST_NAME, null, DATE_OF_BIRTH, PHONE_NUMBER), LAST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildPatient(ID, DEVICE_ID1, FIRST_NAME, "", DATE_OF_BIRTH, PHONE_NUMBER), LAST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildPatient(ID, DEVICE_ID1, FIRST_NAME, LAST_NAME, null, PHONE_NUMBER), DATE_OF_BIRTH_BLANK_ERROR_MESSAGE),
                Arguments.of(buildPatient(ID, DEVICE_ID1, FIRST_NAME, LAST_NAME, "", PHONE_NUMBER), DATE_OF_BIRTH_BLANK_ERROR_MESSAGE),
                Arguments.of(buildPatient(ID, DEVICE_ID1, FIRST_NAME, LAST_NAME, DATE_OF_BIRTH, null), PHONE_NUMBER_BLANK_ERROR_MESSAGE),
                Arguments.of(buildPatient(ID, DEVICE_ID1, FIRST_NAME, LAST_NAME, DATE_OF_BIRTH, ""), PHONE_NUMBER_BLANK_ERROR_MESSAGE)
        );
    }

    @Test
    public void testFindById_HappyCase() {
        Patient newRecord = buildPatient();
        table.putItem(newRecord);

        Patient record = cut.findById(ID);
        assertEquals(newRecord, record);
    }

    @Test
    public void testFindById_WHEN_RecordDoesNotExist_THEN_ReturnNull() {
        Patient record = cut.findById(ID);
        assertNull(record);
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void testFindById_WHEN_InvalidInput_THEN_ThrowInvalidInputException(String id) {
        assertInvalidInputExceptionThrown(() -> cut.findById(id), ID_BLANK_ERROR_MESSAGE);
    }

    @Test
    public void testFindByDeviceId_HappyCase() {
        Patient newRecord = buildPatient();
        table.putItem(newRecord);

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
        Patient newRecord1 = buildPatient();
        cut.create(newRecord1);
        Patient newRecord2 = buildPatient();
        newRecord2.setDeviceId(DEVICE_ID2);
        cut.create(newRecord2);

        Set<String> ids = Set.of(newRecord1.getId(), newRecord2.getId());
        List<Patient> results = cut.batchFindById(ids);
        assertThat(results).containsExactlyInAnyOrder(newRecord1, newRecord2);
    }

    @Test
    public void testBatchFindById_WHEN_RecordsDoNotExist_THEN_ReturnEmptyList() {
        Set<String> ids = Set.of(ID);
        List<Patient> results = cut.batchFindById(ids);
        assertThat(results).isEmpty();
    }

    @Test
    public void testBatchFindById_WHEN_InvalidInput_THEN_ThrowInvalidInputException() {
        assertInvalidInputExceptionThrown(() -> cut.batchFindById(null), IDS_NULL_ERROR_MESSAGE);
    }

    @Test
    public void testUpdate_HappyCase() {
        Patient newRecord = buildPatient();
        cut.create(newRecord);

        Patient updatedRecord = cut.findById(newRecord.getId());
        assertEquals(newRecord, updatedRecord);
        updatedRecord.setDeviceId(DEVICE_ID2);
        cut.update(updatedRecord);

        Patient found = cut.findById(newRecord.getId());
        assertEquals(newRecord.getId(), found.getId());
        assertNotEquals(newRecord.getDeviceId(), found.getDeviceId());
        assertNotEquals(newRecord.getUpdatedAt(), found.getUpdatedAt());
        assertEquals(newRecord.getCreatedAt(), found.getCreatedAt());
    }

    @Test
    public void testUpdate_WHEN_RecordDoesNotExist_THEN_ThrowRecordDoesNotExistException() {
        Patient newRecord = buildPatient();
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
                Arguments.of(buildPatient(null, DEVICE_ID1, FIRST_NAME, LAST_NAME, DATE_OF_BIRTH, PHONE_NUMBER), ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildPatient("", DEVICE_ID1, FIRST_NAME, LAST_NAME, DATE_OF_BIRTH, PHONE_NUMBER), ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildPatient(ID, null, FIRST_NAME, LAST_NAME, DATE_OF_BIRTH, PHONE_NUMBER), DEVICE_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildPatient(ID, "", FIRST_NAME, LAST_NAME, DATE_OF_BIRTH, PHONE_NUMBER), DEVICE_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildPatient(ID, DEVICE_ID1, null, LAST_NAME, DATE_OF_BIRTH, PHONE_NUMBER), FIRST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildPatient(ID, DEVICE_ID1, "", LAST_NAME, DATE_OF_BIRTH, PHONE_NUMBER), FIRST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildPatient(ID, DEVICE_ID1, FIRST_NAME, null, DATE_OF_BIRTH, PHONE_NUMBER), LAST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildPatient(ID, DEVICE_ID1, FIRST_NAME, "", DATE_OF_BIRTH, PHONE_NUMBER), LAST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildPatient(ID, DEVICE_ID1, FIRST_NAME, LAST_NAME, null, PHONE_NUMBER), DATE_OF_BIRTH_BLANK_ERROR_MESSAGE),
                Arguments.of(buildPatient(ID, DEVICE_ID1, FIRST_NAME, LAST_NAME, "", PHONE_NUMBER), DATE_OF_BIRTH_BLANK_ERROR_MESSAGE),
                Arguments.of(buildPatient(ID, DEVICE_ID1, FIRST_NAME, LAST_NAME, DATE_OF_BIRTH, null), PHONE_NUMBER_BLANK_ERROR_MESSAGE),
                Arguments.of(buildPatient(ID, DEVICE_ID1, FIRST_NAME, LAST_NAME, DATE_OF_BIRTH, ""), PHONE_NUMBER_BLANK_ERROR_MESSAGE)
        );
    }

    @Test
    public void testDelete_HappyCase() {
        Patient newRecord = buildPatient();
        table.putItem(newRecord);
        Patient found = table.getItem(newRecord);
        assertNotNull(found);

        cut.delete(ID);
        found = table.getItem(newRecord);
        assertNull(found);
    }

    @Test
    public void testDelete_WHEN_RecordDoesNotExist_THEN_DoNothing() {
        assertDoesNotThrow(() -> cut.delete(ID));
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void testDelete_WHEN_InvalidInput_THEN_ThrowInvalidInputException(String id) {
        assertInvalidInputExceptionThrown(() -> cut.delete(id), ID_BLANK_ERROR_MESSAGE);
    }

    private static Patient buildPatient() {
        return buildPatient(ID, DEVICE_ID1, FIRST_NAME, LAST_NAME, DATE_OF_BIRTH, PHONE_NUMBER);
    }

    private static Patient buildPatient(String id, String deviceId, String firstName, String lastName,
                                            String dateOfBirth, String phoneNumber) {
        return Patient.builder()
                .id(id)
                .deviceId(deviceId)
                .firstName(firstName)
                .lastName(lastName)
                .dateOfBirth(dateOfBirth)
                .phoneNumber(phoneNumber)
                .build();
    }
}
