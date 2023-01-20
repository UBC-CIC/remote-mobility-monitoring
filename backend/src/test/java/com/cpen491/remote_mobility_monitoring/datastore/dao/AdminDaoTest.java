package com.cpen491.remote_mobility_monitoring.datastore.dao;

import com.cpen491.remote_mobility_monitoring.datastore.exception.DuplicateRecordException;
import com.cpen491.remote_mobility_monitoring.datastore.model.Admin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.stream.Stream;

import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.AdminTable;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.ADMIN_RECORD_NULL_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.EMAIL_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.FIRST_NAME_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.LAST_NAME_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.ORGANIZATION_ID_BLANK_ERROR_MESSAGE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class AdminDaoTest extends DaoTestParent {
    private static final String EMAIL = "johnsmith@email.com";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Smith";
    private static final String ORGANIZATION_ID = "abc";

    DynamoDbTable<Admin> table;
    AdminDao cut;

    @BeforeEach
    public void setup() {
        setupAdminTable();
        table = ddbEnhancedClient.table(AdminTable.TABLE_NAME, TableSchema.fromBean(Admin.class));
        cut = new AdminDao(table);
    }

    @AfterEach
    public void teardown() {
        teardownAdminTable();
    }

    @Test
    public void testCreate_HappyCase() {
        Admin newRecord = buildAdmin();
        cut.create(newRecord);

        assertEquals(EMAIL, newRecord.getEmail());
        assertEquals(FIRST_NAME, newRecord.getFirstName());
        assertEquals(LAST_NAME, newRecord.getLastName());
        assertEquals(ORGANIZATION_ID, newRecord.getOrganizationId());
        assertNotNull(newRecord.getCreatedAt());
        assertNotNull(newRecord.getUpdatedAt());
    }

    @Test
    public void testCreate_WHEN_RecordWithEmailAlreadyExists_THEN_ThrowDuplicateRecordException() {
        Admin newRecord = buildAdmin();
        cut.create(newRecord);
        assertThatThrownBy(() -> cut.create(newRecord)).isInstanceOf(DuplicateRecordException.class);
    }

    @ParameterizedTest
    @MethodSource("invalidInputsForCreate")
    public void testCreate_WHEN_InvalidInput_THEN_ThrowInvalidInputException(Admin record, String errorMessage) {
        assertInvalidInputExceptionThrown(() -> cut.create(record), errorMessage);
    }

    private static Stream<Arguments> invalidInputsForCreate() {
        return Stream.of(
                Arguments.of(null, ADMIN_RECORD_NULL_ERROR_MESSAGE),
                Arguments.of(buildAdmin(null, FIRST_NAME, LAST_NAME, ORGANIZATION_ID), EMAIL_BLANK_ERROR_MESSAGE),
                Arguments.of(buildAdmin("", FIRST_NAME, LAST_NAME, ORGANIZATION_ID), EMAIL_BLANK_ERROR_MESSAGE),
                Arguments.of(buildAdmin(EMAIL, null, LAST_NAME, ORGANIZATION_ID), FIRST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildAdmin(EMAIL, "", LAST_NAME, ORGANIZATION_ID), FIRST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildAdmin(EMAIL, FIRST_NAME, null, ORGANIZATION_ID), LAST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildAdmin(EMAIL, FIRST_NAME, "", ORGANIZATION_ID), LAST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildAdmin(EMAIL, FIRST_NAME, LAST_NAME, null), ORGANIZATION_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildAdmin(EMAIL, FIRST_NAME, LAST_NAME, ""), ORGANIZATION_ID_BLANK_ERROR_MESSAGE)
        );
    }

    @Test
    public void testFind_HappyCase() {
        Admin newRecord = buildAdmin();
        table.putItem(newRecord);

        Admin record = cut.find(EMAIL);
        assertEquals(newRecord, record);
    }

    @Test
    public void testFind_WHEN_RecordDoesNotExist_THEN_ReturnNull() {
        Admin record = cut.find(EMAIL);
        assertNull(record);
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void testFind_WHEN_InvalidInput_THEN_ThrowInvalidInputException(String email) {
        assertInvalidInputExceptionThrown(() -> cut.find(email), EMAIL_BLANK_ERROR_MESSAGE);
    }

    @Test
    public void testDelete_HappyCase() {
        Admin newRecord = buildAdmin();
        table.putItem(newRecord);
        Admin found = table.getItem(newRecord);
        assertNotNull(found);

        cut.delete(EMAIL);
        found = table.getItem(newRecord);
        assertNull(found);
    }

    @Test
    public void testDelete_WHEN_RecordDoesNotExist_THEN_DoNothing() {
        assertDoesNotThrow(() -> cut.delete(EMAIL));
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void testDelete_WHEN_InvalidInput_THEN_ThrowInvalidInputException(String email) {
        assertInvalidInputExceptionThrown(() -> cut.delete(email), EMAIL_BLANK_ERROR_MESSAGE);
    }

    private static Admin buildAdmin() {
        return buildAdmin(EMAIL, FIRST_NAME, LAST_NAME, ORGANIZATION_ID);
    }

    private static Admin buildAdmin(String email, String firstName, String lastName, String organizationId) {
        return Admin.builder()
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .organizationId(organizationId)
                .build();
    }

    // TODO: maybe move to parent class
    private static void assertInvalidInputExceptionThrown(ThrowingCallable shouldRaiseThrowable, String errorMessage) {
        assertThatThrownBy(shouldRaiseThrowable)
                .isInstanceOfAny(IllegalArgumentException.class, NullPointerException.class)
                .hasMessage(errorMessage);
    }
}