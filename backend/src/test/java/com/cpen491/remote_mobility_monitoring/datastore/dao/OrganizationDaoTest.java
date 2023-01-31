package com.cpen491.remote_mobility_monitoring.datastore.dao;

import com.cpen491.remote_mobility_monitoring.datastore.exception.DuplicateRecordException;
import com.cpen491.remote_mobility_monitoring.datastore.exception.RecordDoesNotExistException;
import com.cpen491.remote_mobility_monitoring.datastore.model.Admin;
import com.cpen491.remote_mobility_monitoring.datastore.model.Caregiver;
import com.cpen491.remote_mobility_monitoring.datastore.model.Organization;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.cpen491.remote_mobility_monitoring.TestUtils.assertInvalidInputExceptionThrown;
import static com.cpen491.remote_mobility_monitoring.TestUtils.buildAdmin;
import static com.cpen491.remote_mobility_monitoring.TestUtils.buildCaregiver;
import static com.cpen491.remote_mobility_monitoring.TestUtils.buildOrganization;
import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.OrganizationTable;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.CAREGIVER_ID_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.CAREGIVER_ID_INVALID_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.ORGANIZATION_ID_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.ORGANIZATION_ID_INVALID_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.ORGANIZATION_RECORD_NULL_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.NAME_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.PID_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.PID_NOT_EQUAL_SID_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.SID_BLANK_ERROR_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrganizationDaoTest extends DaoTestParent {
    private static final String PID = "org-1";
    private static final String SID = PID;
    private static final String NAME1 = "ORG1";
    private static final String NAME2 = "ORG2";
    private static final String ADMIN_ID = "adm-1";
    private static final String CAREGIVER_ID = "car-1";

    OrganizationDao cut;

    @BeforeEach
    public void setup() {
        setupTable();
        cut = new OrganizationDao(genericDao);
    }

    @AfterEach
    public void teardown() {
        teardownTable();
    }

    @Test
    public void testCreate_HappyCase() {
        Organization newRecord = buildOrganizationDefault();
        cut.create(newRecord);

        GetItemResponse response = findByPrimaryKey(newRecord.getPid(), newRecord.getPid());
        assertTrue(response.hasItem());

        newRecord = Organization.convertFromMap(response.item());
        assertNotEquals(PID, newRecord.getPid());
        assertNotEquals(SID, newRecord.getSid());
        assertEquals(NAME1, newRecord.getName());
        assertNotNull(newRecord.getCreatedAt());
        assertNotNull(newRecord.getUpdatedAt());
    }

    @Test
    public void testCreate_WHEN_RecordWithNameAlreadyExists_THEN_ThrowDuplicateRecordException() {
        Organization newRecord = buildOrganizationDefault();
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
                Arguments.of(buildOrganization(PID, SID, null), NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildOrganization(PID, SID, ""), NAME_BLANK_ERROR_MESSAGE)
        );
    }

    @Test
    public void testHasCaregiver_HappyCase() {
        putPrimaryKey(PID, CAREGIVER_ID);

        boolean result = cut.hasCaregiver(CAREGIVER_ID, PID);
        assertTrue(result);
    }

    @Test
    public void testHasCaregiver_WHEN_CaregiverNotAdded_THEN_ReturnFalse() {
        boolean result = cut.hasCaregiver(CAREGIVER_ID, PID);
        assertFalse(result);
    }

    @ParameterizedTest
    @MethodSource("invalidInputsForHasCaregiver")
    public void testHasCaregiver_WHEN_InvalidInput_THEN_ThrowInvalidInputException(String caregiverId, String organizationId, String errorMessage) {
        assertInvalidInputExceptionThrown(() -> cut.hasCaregiver(caregiverId, organizationId), errorMessage);
    }

    private static Stream<Arguments> invalidInputsForHasCaregiver() {
        return Stream.of(
                Arguments.of(null, PID, CAREGIVER_ID_BLANK_ERROR_MESSAGE),
                Arguments.of("", PID, CAREGIVER_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(PID, PID, CAREGIVER_ID_INVALID_ERROR_MESSAGE),
                Arguments.of(CAREGIVER_ID, null, ORGANIZATION_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(CAREGIVER_ID, "", ORGANIZATION_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(CAREGIVER_ID, CAREGIVER_ID, ORGANIZATION_ID_INVALID_ERROR_MESSAGE)
        );
    }

    @Test
    public void testFindById_HappyCase() {
        Organization newRecord = buildOrganizationDefault();
        createOrganization(newRecord);

        Organization found = cut.findById(newRecord.getPid());
        assertEquals(newRecord, found);
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
                Arguments.of(null, ORGANIZATION_ID_BLANK_ERROR_MESSAGE),
                Arguments.of("", ORGANIZATION_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(ADMIN_ID, ORGANIZATION_ID_INVALID_ERROR_MESSAGE)
        );
    }

    @Test
    public void testFindByName_HappyCase() {
        Organization newRecord = buildOrganizationDefault();
        createOrganization(newRecord);

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
    public void testFindAllAdmins_HappyCase() {
        Admin admin1 = buildAdmin(ADMIN_ID, ADMIN_ID, null, null, null);
        createAdmin(admin1);
        Organization organization = buildOrganizationDefault();
        createOrganization(organization);

        putPrimaryKey(PID, ADMIN_ID);
        List<Admin> admins = cut.findAllAdmins(PID).stream().peek(admin -> {
            admin.setCreatedAt(null);
            admin.setUpdatedAt(null);
        }).collect(Collectors.toList());
        assertThat(admins).containsExactlyInAnyOrder(admin1);
    }

    @Test
    public void testFindAllAdmins_WHEN_OrganizationRecordDoesNotExist_THEN_ThrowRecordDoesNotExistException() {
        assertThatThrownBy(() -> cut.findAllAdmins(PID)).isInstanceOf(RecordDoesNotExistException.class);
    }

    @Test
    public void testFindAllAdmins_WHEN_NoAdminRecordsAssociated_THEN_ReturnEmptyList() {
        Organization organization = buildOrganizationDefault();
        createOrganization(organization);

        List<Admin> admins = cut.findAllAdmins(PID);
        assertThat(admins).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("invalidInputsForFindAllAdmins")
    public void testFindAllAdmins_WHEN_InvalidInput_THEN_ThrowInvalidInputException(String organizationId, String errorMessage) {
        assertInvalidInputExceptionThrown(() -> cut.findAllAdmins(organizationId), errorMessage);
    }

    private static Stream<Arguments> invalidInputsForFindAllAdmins() {
        return Stream.of(
                Arguments.of(null, ORGANIZATION_ID_BLANK_ERROR_MESSAGE),
                Arguments.of("", ORGANIZATION_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(ADMIN_ID, ORGANIZATION_ID_INVALID_ERROR_MESSAGE)
        );
    }

    @Test
    public void testFindAllCaregivers_HappyCase() {
        Caregiver caregiver1 = buildCaregiver(CAREGIVER_ID, CAREGIVER_ID, null, null, null, null, null);
        createCaregiver(caregiver1);
        Organization organization = buildOrganizationDefault();
        createOrganization(organization);

        putPrimaryKey(PID, CAREGIVER_ID);
        List<Caregiver> caregivers = cut.findAllCaregivers(PID).stream().peek(caregiver -> {
            caregiver.setCreatedAt(null);
            caregiver.setUpdatedAt(null);
        }).collect(Collectors.toList());
        assertThat(caregivers).containsExactlyInAnyOrder(caregiver1);
    }

    @Test
    public void testFindAllCaregivers_WHEN_OrganizationRecordDoesNotExist_THEN_ThrowRecordDoesNotExistException() {
        assertThatThrownBy(() -> cut.findAllCaregivers(PID)).isInstanceOf(RecordDoesNotExistException.class);
    }

    @Test
    public void testFindAllCaregivers_WHEN_NoCaregiverRecordsAssociated_THEN_ReturnEmptyList() {
        Organization organization = buildOrganizationDefault();
        createOrganization(organization);

        List<Caregiver> caregivers = cut.findAllCaregivers(PID);
        assertThat(caregivers).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("invalidInputsForFindAllCaregivers")
    public void testFindAllCaregivers_WHEN_InvalidInput_THEN_ThrowInvalidInputException(String organizationId, String errorMessage) {
        assertInvalidInputExceptionThrown(() -> cut.findAllCaregivers(organizationId), errorMessage);
    }

    private static Stream<Arguments> invalidInputsForFindAllCaregivers() {
        return Stream.of(
                Arguments.of(null, ORGANIZATION_ID_BLANK_ERROR_MESSAGE),
                Arguments.of("", ORGANIZATION_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(CAREGIVER_ID, ORGANIZATION_ID_INVALID_ERROR_MESSAGE)
        );
    }

    @Test
    public void testUpdate_HappyCase() {
        Organization newRecord = buildOrganizationDefault();
        cut.create(newRecord);

        Organization updatedRecord = cut.findById(newRecord.getPid());
        assertEquals(newRecord, updatedRecord);
        updatedRecord.setName(NAME2);
        cut.update(updatedRecord);

        Organization found = cut.findById(newRecord.getPid());
        assertEquals(newRecord.getPid(), found.getPid());
        assertNotEquals(newRecord.getName(), found.getName());
        assertNotEquals(newRecord.getUpdatedAt(), found.getUpdatedAt());
        assertEquals(newRecord.getCreatedAt(), found.getCreatedAt());
    }

    @Test
    public void testUpdate_WHEN_RecordWithNameAlreadyExists_THEN_ThrowDuplicateRecordException() {
        Organization newRecord1 = buildOrganizationDefault();
        cut.create(newRecord1);
        Organization newRecord2 = buildOrganizationDefault();
        newRecord2.setName(NAME2);
        cut.create(newRecord2);

        Organization updatedRecord = cut.findById(newRecord2.getPid());
        updatedRecord.setName(NAME1);
        assertThatThrownBy(() -> cut.update(updatedRecord)).isInstanceOf(DuplicateRecordException.class);
    }

    @Test
    public void testUpdate_WHEN_RecordAlsoExistsInAssociations_THEN_UpdateAllDuplicateRecords() {
        Organization newRecord1 = buildOrganizationDefault();
        createOrganization(newRecord1);
        Organization newRecord2 = buildOrganizationDefault();
        newRecord2.setSid(ADMIN_ID);
        createOrganization(newRecord2);
        Organization newRecord3 = buildOrganizationDefault();
        newRecord3.setSid(CAREGIVER_ID);
        createOrganization(newRecord3);

        Organization updatedRecord = cut.findById(PID);
        updatedRecord.setName(NAME2);
        cut.update(updatedRecord);

        assertEquals(NAME2, findByPrimaryKey(PID, PID).item().get(OrganizationTable.NAME_NAME).s());
        assertEquals(NAME2, findByPrimaryKey(PID, ADMIN_ID).item().get(OrganizationTable.NAME_NAME).s());
        assertEquals(NAME2, findByPrimaryKey(PID, CAREGIVER_ID).item().get(OrganizationTable.NAME_NAME).s());
    }

    @Test
    public void testUpdate_WHEN_RecordDoesNotExist_THEN_ThrowRecordDoesNotExistException() {
        Organization newRecord = buildOrganizationDefault();
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
                Arguments.of(buildOrganization(null, SID, NAME1), PID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildOrganization("", SID, NAME1), PID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildOrganization(PID, null, NAME1), SID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildOrganization(PID, "", NAME1), SID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildOrganization(PID, SID, null), NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildOrganization(PID, SID, ""), NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildOrganization(ADMIN_ID, ADMIN_ID, NAME1), ORGANIZATION_ID_INVALID_ERROR_MESSAGE),
                Arguments.of(buildOrganization(PID, SID + "1", NAME1), PID_NOT_EQUAL_SID_ERROR_MESSAGE)
        );
    }

    @Test
    public void testDelete_HappyCase() {
        Organization newRecord = buildOrganizationDefault();
        createOrganization(newRecord);
        Organization found = cut.findById(newRecord.getPid());
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
                Arguments.of(null, ORGANIZATION_ID_BLANK_ERROR_MESSAGE),
                Arguments.of("", ORGANIZATION_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(ADMIN_ID, ORGANIZATION_ID_INVALID_ERROR_MESSAGE)
        );
    }

    private static Organization buildOrganizationDefault() {
        return buildOrganization(PID, SID, NAME1);
    }
}
