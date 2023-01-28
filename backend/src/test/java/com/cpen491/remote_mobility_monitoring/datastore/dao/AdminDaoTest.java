package com.cpen491.remote_mobility_monitoring.datastore.dao;

import com.cpen491.remote_mobility_monitoring.datastore.exception.DuplicateRecordException;
import com.cpen491.remote_mobility_monitoring.datastore.exception.RecordDoesNotExistException;
import com.cpen491.remote_mobility_monitoring.datastore.model.Admin;
import com.cpen491.remote_mobility_monitoring.datastore.model.Organization;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;

import java.util.stream.Stream;

import static com.cpen491.remote_mobility_monitoring.TestUtils.assertInvalidInputExceptionThrown;
import static com.cpen491.remote_mobility_monitoring.TestUtils.buildAdmin;
import static com.cpen491.remote_mobility_monitoring.TestUtils.buildOrganization;
import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.AdminTable;
import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.OrganizationTable;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.ADMIN_ID_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.ADMIN_ID_INVALID_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.ADMIN_RECORD_NULL_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.EMAIL_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.FIRST_NAME_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.LAST_NAME_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.PID_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.PID_NOT_EQUAL_SID_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.SID_BLANK_ERROR_MESSAGE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdminDaoTest extends DaoTestParent {
    private static final String PID = "adm-1";
    private static final String SID = PID;
    private static final String EMAIL1 = "johnsmith@email.com";
    private static final String EMAIL2 = "johnsmithiscool@email.com";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Smith";
    private static final String EXISTS_ORGANIZATION_ID = "org-1";
    private static final String NOT_EXISTS_ORGANIZATION_ID = "org-2";
    private static final String ORGANIZATION_NAME = "ORG1";

    AdminDao cut;

    @BeforeEach
    public void setup() {
        setupTable();
        GenericDao genericDao = new GenericDao(ddbClient);
        OrganizationDao organizationDao = new OrganizationDao(genericDao);
        cut = new AdminDao(genericDao, organizationDao);

        Organization organization = buildOrganization(EXISTS_ORGANIZATION_ID, EXISTS_ORGANIZATION_ID, ORGANIZATION_NAME);
        createOrganization(organization);
    }

    @AfterEach
    public void teardown() {
        teardownTable();
    }

    @Test
    public void testCreate_HappyCase() {
        Admin newRecord = buildAdminDefault();
        cut.create(newRecord, EXISTS_ORGANIZATION_ID);

        assertNotEquals(PID, newRecord.getPid());
        assertNotEquals(SID, newRecord.getSid());
        assertEquals(EMAIL1, newRecord.getEmail());
        assertEquals(FIRST_NAME, newRecord.getFirstName());
        assertEquals(LAST_NAME, newRecord.getLastName());
        assertNotNull(newRecord.getCreatedAt());
        assertNotNull(newRecord.getUpdatedAt());
        GetItemResponse response = findByPrimaryKey(EXISTS_ORGANIZATION_ID, newRecord.getPid());
        assertTrue(response.hasItem());
        assertEquals(ORGANIZATION_NAME, response.item().get(OrganizationTable.NAME_NAME).s());
        assertEquals(EMAIL1, response.item().get(AdminTable.EMAIL_NAME).s());
    }

    @Test
    public void testCreate_WHEN_OrganizationDoesNotExist_THEN_ThrowRecordDoesNotExistException() {
        Admin newRecord = buildAdminDefault();
        assertThatThrownBy(() -> cut.create(newRecord, NOT_EXISTS_ORGANIZATION_ID)).isInstanceOf(RecordDoesNotExistException.class);
    }

    @Test
    public void testCreate_WHEN_RecordWithEmailAlreadyExists_THEN_ThrowDuplicateRecordException() {
        Admin newRecord = buildAdminDefault();
        cut.create(newRecord, EXISTS_ORGANIZATION_ID);
        assertThatThrownBy(() -> cut.create(newRecord, EXISTS_ORGANIZATION_ID)).isInstanceOf(DuplicateRecordException.class);
    }

    @ParameterizedTest
    @MethodSource("invalidInputsForCreate")
    public void testCreate_WHEN_InvalidInput_THEN_ThrowInvalidInputException(Admin record, String errorMessage) {
        assertInvalidInputExceptionThrown(() -> cut.create(record, EXISTS_ORGANIZATION_ID), errorMessage);
    }

    private static Stream<Arguments> invalidInputsForCreate() {
        return Stream.of(
                Arguments.of(null, ADMIN_RECORD_NULL_ERROR_MESSAGE),
                Arguments.of(buildAdmin(PID, SID, null, FIRST_NAME, LAST_NAME), EMAIL_BLANK_ERROR_MESSAGE),
                Arguments.of(buildAdmin(PID, SID, "", FIRST_NAME, LAST_NAME), EMAIL_BLANK_ERROR_MESSAGE),
                Arguments.of(buildAdmin(PID, SID, EMAIL1, null, LAST_NAME), FIRST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildAdmin(PID, SID, EMAIL1, "", LAST_NAME), FIRST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildAdmin(PID, SID, EMAIL1, FIRST_NAME, null), LAST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildAdmin(PID, SID, EMAIL1, FIRST_NAME, ""), LAST_NAME_BLANK_ERROR_MESSAGE)
        );
    }

    @Test
    public void testFindById_HappyCase() {
        Admin newRecord = buildAdminDefault();
        createAdmin(newRecord);

        Admin record = cut.findById(newRecord.getPid());
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
                Arguments.of(null, ADMIN_ID_BLANK_ERROR_MESSAGE),
                Arguments.of("", ADMIN_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(EXISTS_ORGANIZATION_ID, ADMIN_ID_INVALID_ERROR_MESSAGE)
        );
    }

    @Test
    public void testFindByEmail_HappyCase() {
        Admin newRecord = buildAdminDefault();
        createAdmin(newRecord);

        Admin record = cut.findByEmail(EMAIL1);
        assertEquals(newRecord, record);
    }

    @Test
    public void testFindByEmail_WHEN_RecordDoesNotExist_THEN_ReturnNull() {
        Admin record = cut.findByEmail(EMAIL1);
        assertNull(record);
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void testFindByEmail_WHEN_InvalidInput_THEN_ThrowInvalidInputException(String email) {
        assertInvalidInputExceptionThrown(() -> cut.findByEmail(email), EMAIL_BLANK_ERROR_MESSAGE);
    }

    @Test
    public void testFindOrganization_HappyCase() {
        Admin newRecord = buildAdminDefault();
        cut.create(newRecord, EXISTS_ORGANIZATION_ID);

        Organization organization = cut.findOrganization(newRecord.getPid());
        assertNotNull(organization);
        assertEquals(EXISTS_ORGANIZATION_ID, organization.getPid());
        assertEquals(EXISTS_ORGANIZATION_ID, organization.getSid());
        assertEquals(ORGANIZATION_NAME, organization.getName());
    }

    @Test
    public void testFindOrganization_WHEN_AdminRecordDoesNotExist_THEN_ReturnNull() {
        Organization organization = cut.findOrganization(PID);
        assertNull(organization);
    }

    @Test
    public void testFindOrganization_WHEN_NoOrganizationRecordAssociated_THEN_ReturnNull() {
        Admin newRecord = buildAdminDefault();
        createAdmin(newRecord);

        Organization organization = cut.findOrganization(PID);
        assertNull(organization);
    }

    @ParameterizedTest
    @MethodSource("invalidInputsForFindOrganization")
    public void testFindOrganization_WHEN_InvalidInput_THEN_ThrowInvalidInputException(String adminId, String errorMessage) {
        assertInvalidInputExceptionThrown(() -> cut.findOrganization(adminId), errorMessage);
    }

    private static Stream<Arguments> invalidInputsForFindOrganization() {
        return Stream.of(
                Arguments.of(null, ADMIN_ID_BLANK_ERROR_MESSAGE),
                Arguments.of("", ADMIN_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(EXISTS_ORGANIZATION_ID, ADMIN_ID_INVALID_ERROR_MESSAGE)
        );
    }

    @Test
    public void testUpdate_HappyCase() {
        Admin newRecord = buildAdminDefault();
        cut.create(newRecord, EXISTS_ORGANIZATION_ID);

        Admin updatedRecord = cut.findById(newRecord.getPid());
        assertEquals(newRecord, updatedRecord);
        updatedRecord.setEmail(EMAIL2);
        cut.update(updatedRecord);

        Admin found = cut.findById(newRecord.getPid());
        assertEquals(newRecord.getPid(), found.getPid());
        assertNotEquals(newRecord.getEmail(), found.getEmail());
        assertNotEquals(newRecord.getUpdatedAt(), found.getUpdatedAt());
        assertEquals(newRecord.getCreatedAt(), found.getCreatedAt());
    }

    @Test
    public void testUpdate_WHEN_RecordWithEmailAlreadyExists_THEN_ThrowDuplicateRecordException() {
        Admin newRecord1 = buildAdminDefault();
        cut.create(newRecord1, EXISTS_ORGANIZATION_ID);
        Admin newRecord2 = buildAdminDefault();
        newRecord2.setEmail(EMAIL2);
        cut.create(newRecord2, EXISTS_ORGANIZATION_ID);

        Admin updatedRecord = cut.findById(newRecord2.getPid());
        updatedRecord.setEmail(EMAIL1);
        assertThatThrownBy(() -> cut.update(updatedRecord)).isInstanceOf(DuplicateRecordException.class);
    }

    @Test
    public void testUpdate_WHEN_RecordDoesNotExist_THEN_ThrowRecordDoesNotExistException() {
        Admin newRecord = buildAdminDefault();
        assertThatThrownBy(() -> cut.update(newRecord)).isInstanceOf(RecordDoesNotExistException.class);
    }

    @ParameterizedTest
    @MethodSource("invalidInputsForUpdate")
    public void testUpdate_WHEN_InvalidInput_THEN_ThrowInvalidInputException(Admin record, String errorMessage) {
        assertInvalidInputExceptionThrown(() -> cut.update(record), errorMessage);
    }

    private static Stream<Arguments> invalidInputsForUpdate() {
        return Stream.of(
                Arguments.of(null, ADMIN_RECORD_NULL_ERROR_MESSAGE),
                Arguments.of(buildAdmin(null, SID, EMAIL1, FIRST_NAME, LAST_NAME), PID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildAdmin("", SID, EMAIL1, FIRST_NAME, LAST_NAME), PID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildAdmin(PID, null, EMAIL1, FIRST_NAME, LAST_NAME), SID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildAdmin(PID, "", EMAIL1, FIRST_NAME, LAST_NAME), SID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildAdmin(PID, SID, null, FIRST_NAME, LAST_NAME), EMAIL_BLANK_ERROR_MESSAGE),
                Arguments.of(buildAdmin(PID, SID, "", FIRST_NAME, LAST_NAME), EMAIL_BLANK_ERROR_MESSAGE),
                Arguments.of(buildAdmin(PID, SID, EMAIL1, null, LAST_NAME), FIRST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildAdmin(PID, SID, EMAIL1, "", LAST_NAME), FIRST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildAdmin(PID, SID, EMAIL1, FIRST_NAME, null), LAST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildAdmin(PID, SID, EMAIL1, FIRST_NAME, ""), LAST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildAdmin(EXISTS_ORGANIZATION_ID, EXISTS_ORGANIZATION_ID, EMAIL1, FIRST_NAME, LAST_NAME),
                        ADMIN_ID_INVALID_ERROR_MESSAGE),
                Arguments.of(buildAdmin(PID, SID + "1", EMAIL1, FIRST_NAME, LAST_NAME), PID_NOT_EQUAL_SID_ERROR_MESSAGE)
        );
    }

    @Test
    public void testDelete_HappyCase() {
        Admin newRecord = buildAdminDefault();
        createAdmin(newRecord);
        Admin found = cut.findById(newRecord.getPid());
        assertNotNull(found);

        cut.delete(newRecord.getPid());
        assertThatThrownBy(() -> cut.findById(newRecord.getPid())).isInstanceOf(RecordDoesNotExistException.class);
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
                Arguments.of(null, ADMIN_ID_BLANK_ERROR_MESSAGE),
                Arguments.of("", ADMIN_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(EXISTS_ORGANIZATION_ID, ADMIN_ID_INVALID_ERROR_MESSAGE)
        );
    }

    private static Admin buildAdminDefault() {
        return buildAdmin(PID, SID, EMAIL1, FIRST_NAME, LAST_NAME);
    }

}
