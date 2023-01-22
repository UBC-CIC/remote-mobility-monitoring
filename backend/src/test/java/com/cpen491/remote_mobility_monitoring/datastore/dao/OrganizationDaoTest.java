package com.cpen491.remote_mobility_monitoring.datastore.dao;

import com.cpen491.remote_mobility_monitoring.datastore.exception.DuplicateRecordException;
import com.cpen491.remote_mobility_monitoring.datastore.exception.RecordDoesNotExistException;
import com.cpen491.remote_mobility_monitoring.datastore.model.Organization;
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
import java.util.Map;
import java.util.stream.Stream;

import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.ID_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.ORGANIZATION_RECORD_NULL_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.NAME_BLANK_ERROR_MESSAGE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.OrganizationTable;
import static org.junit.jupiter.api.Assertions.assertNull;

public class OrganizationDaoTest extends DaoTestParent {
    private static final String ID = "org-id-abc";
    private static final String NAME1 = "ORG1";
    private static final String NAME2 = "ORG2";

    DynamoDbTable<Organization> table;
    OrganizationDao cut;

    @BeforeEach
    public void setup() {
        setupOrganizationTable();
        table = ddbEnhancedClient.table(OrganizationTable.TABLE_NAME, TableSchema.fromBean(Organization.class));
        Map<String, DynamoDbIndex<Organization>> indexMap = new HashMap<>();
        indexMap.put(OrganizationTable.NAME_INDEX_NAME, table.index(OrganizationTable.NAME_INDEX_NAME));
        cut = new OrganizationDao(new GenericDao<>(table, indexMap));
    }

    @AfterEach
    public void teardown() {
        teardownOrganizationTable();
    }

    @Test
    public void testCreate_HappyCase() {
        Organization newRecord = buildOrganization();
        cut.create(newRecord);

        assertNotEquals(ID, newRecord.getId());
        assertEquals(NAME1, newRecord.getName());
        assertNotNull(newRecord.getCreatedAt());
        assertNotNull(newRecord.getUpdatedAt());
    }

    @Test
    public void testCreate_WHEN_RecordWithNameAlreadyExists_THEN_ThrowDuplicateRecordException() {
        Organization newRecord = buildOrganization();
        cut.create(newRecord);
        assertThatThrownBy(() -> cut.create(newRecord)).isInstanceOf(DuplicateRecordException.class);
    }

    @ParameterizedTest
    @MethodSource("invalidInputsForCreate")
    public void testCreate_WHEN_InvalidInput_THEN_ThrowInvalidInputException(Organization record, String errorMessage) {
        assertInvalidInputExceptionThrown(() -> cut.create(record), errorMessage);
    }

    private static Stream<Arguments> invalidInputsForCreate() {
        return Stream.of(
                Arguments.of(null, ORGANIZATION_RECORD_NULL_ERROR_MESSAGE),
                Arguments.of(buildOrganization(ID, null), NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildOrganization(ID, ""), NAME_BLANK_ERROR_MESSAGE)
        );
    }

    @Test
    public void testFindById_HappyCase() {
        Organization newRecord = buildOrganization();
        table.putItem(newRecord);

        Organization found = cut.findById(ID);
        assertEquals(newRecord, found);
    }

    @Test
    public void testFindById_WHEN_RecordDoesNotExist_THEN_ReturnNull() {
        Organization found = cut.findById(ID);
        assertNull(found);
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void testFindById_WHEN_InvalidInput_THEN_ThrowInvalidInputException(String id) {
        assertInvalidInputExceptionThrown(() -> cut.findById(id), ID_BLANK_ERROR_MESSAGE);
    }

    @Test
    public void testFindByName_HappyCase() {
        Organization newRecord = buildOrganization();
        table.putItem(newRecord);

        Organization found = cut.findByName(NAME1);
        assertEquals(newRecord, found);
    }

    @Test
    public void testFindByName_WHEN_RecordDoesNotExist_THEN_ReturnNull() {
        Organization found = cut.findByName(NAME1);
        assertNull(found);
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void testFindByName_WHEN_InvalidInput_THEN_ThrowInvalidInputException(String name) {
        assertInvalidInputExceptionThrown(() -> cut.findByName(name), NAME_BLANK_ERROR_MESSAGE);
    }

    @Test
    public void testUpdate_HappyCase() {
        Organization newRecord = buildOrganization();
        cut.create(newRecord);

        Organization updatedRecord = cut.findById(newRecord.getId());
        assertEquals(newRecord, updatedRecord);
        updatedRecord.setName(NAME2);
        cut.update(updatedRecord);

        Organization found = cut.findById(newRecord.getId());
        assertEquals(newRecord.getId(), found.getId());
        assertNotEquals(newRecord.getName(), found.getName());
        assertNotEquals(newRecord.getUpdatedAt(), found.getUpdatedAt());
        assertEquals(newRecord.getCreatedAt(), found.getCreatedAt());
    }

    @Test
    public void testUpdate_WHEN_RecordDoesNotExist_THEN_ThrowRecordDoesNotExistException() {
        Organization newRecord = buildOrganization();
        assertThatThrownBy(() -> cut.update(newRecord)).isInstanceOf(RecordDoesNotExistException.class);
    }

    @ParameterizedTest
    @MethodSource("invalidInputsForUpdate")
    public void testUpdate_WHEN_InvalidInput_THEN_ThrowInvalidInputException(Organization record, String errorMessage) {
        assertInvalidInputExceptionThrown(() -> cut.update(record), errorMessage);
    }

    private static Stream<Arguments> invalidInputsForUpdate() {
        return Stream.of(
                Arguments.of(null, ORGANIZATION_RECORD_NULL_ERROR_MESSAGE),
                Arguments.of(buildOrganization(null, NAME1), ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildOrganization("", NAME1), ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildOrganization(ID, null), NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildOrganization(ID, ""), NAME_BLANK_ERROR_MESSAGE)
        );
    }

    @Test
    public void testDelete_HappyCase() {
        Organization newRecord = buildOrganization();
        table.putItem(newRecord);
        Organization found = table.getItem(newRecord);
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

    private static Organization buildOrganization() {
        return buildOrganization(ID, NAME1);
    }

    private static Organization buildOrganization(String id, String name) {
        return Organization.builder()
                .id(id)
                .name(name)
                .build();
    }
}
