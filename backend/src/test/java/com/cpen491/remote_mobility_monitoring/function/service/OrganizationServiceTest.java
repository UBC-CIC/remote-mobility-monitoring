package com.cpen491.remote_mobility_monitoring.function.service;

import com.cpen491.remote_mobility_monitoring.datastore.dao.OrganizationDao;
import com.cpen491.remote_mobility_monitoring.datastore.model.Admin;
import com.cpen491.remote_mobility_monitoring.datastore.model.Caregiver;
import com.cpen491.remote_mobility_monitoring.datastore.model.Organization;
import com.cpen491.remote_mobility_monitoring.function.schema.organization.CreateOrganizationRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.organization.CreateOrganizationResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.organization.GetOrganizationRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.organization.GetOrganizationResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.organization.GetOrganizationResponseBody.AdminSerialization;
import com.cpen491.remote_mobility_monitoring.function.schema.organization.GetOrganizationResponseBody.CaregiverSerialization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.cpen491.remote_mobility_monitoring.TestUtils.assertInvalidInputExceptionThrown;
import static com.cpen491.remote_mobility_monitoring.TestUtils.buildAdmin;
import static com.cpen491.remote_mobility_monitoring.TestUtils.buildCaregiver;
import static com.cpen491.remote_mobility_monitoring.TestUtils.buildOrganization;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.CREATE_ORGANIZATION_NULL_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.GET_ORGANIZATION_NULL_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.NAME_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.ORGANIZATION_ID_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.ORGANIZATION_ID_INVALID_ERROR_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrganizationServiceTest {
    private static final String ORGANIZATION_ID = "org-1";
    private static final String ORGANIZATION_NAME = "Organization1";
    private static final String CAREGIVER_ID1 = "car-1";
    private static final String CAREGIVER_ID2 = "car-2";
    private static final String ADMIN_ID1 = "adm-1";
    private static final String ADMIN_ID2 = "adm-2";
    private static final String EMAIL = "jackjackson@email.com";
    private static final String TITLE = "caregiver";
    private static final String FIRST_NAME = "Jack";
    private static final String LAST_NAME = "Jackson";
    private static final String PHONE_NUMBER = "1234567890";

    OrganizationService cut;
    @Mock
    OrganizationDao organizationDao;
    ArgumentCaptor<Organization> organizationCaptor;

    @BeforeEach
    public void setup() {
        organizationCaptor = ArgumentCaptor.forClass(Organization.class);
        cut = new OrganizationService(organizationDao);
    }

    @Test
    public void testCreateOrganization_HappyCase() {
        CreateOrganizationRequestBody requestBody = buildCreateOrganizationRequestBody();
        CreateOrganizationResponseBody responseBody = cut.createOrganization(requestBody);

        verify(organizationDao, times(1)).create(organizationCaptor.capture());
        assertEquals(ORGANIZATION_NAME, organizationCaptor.getValue().getName());
        assertNotNull(responseBody);
    }

    @Test
    public void testCreateOrganization_WHEN_OrganizationDaoCreateThrows_THEN_ThrowSameException() {
        NullPointerException toThrow = new NullPointerException();
        Mockito.doThrow(toThrow).when(organizationDao).create(any(Organization.class));

        CreateOrganizationRequestBody requestBody = buildCreateOrganizationRequestBody();
        assertThatThrownBy(() -> cut.createOrganization(requestBody)).isSameAs(toThrow);
    }

    @ParameterizedTest
    @MethodSource("invalidInputsForCreateOrganization")
    public void testCreateOrganization_WHEN_InvalidInput_THEN_ThrowInvalidInputException(CreateOrganizationRequestBody body, String errorMessage) {
        assertInvalidInputExceptionThrown(() -> cut.createOrganization(body), errorMessage);
    }

    private static Stream<Arguments> invalidInputsForCreateOrganization() {
        return Stream.of(
                Arguments.of(null, CREATE_ORGANIZATION_NULL_ERROR_MESSAGE),
                Arguments.of(buildCreateOrganizationRequestBody(null), NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreateOrganizationRequestBody(""), NAME_BLANK_ERROR_MESSAGE)
        );
    }

    @Test
    public void testGetOrganization_HappyCase() {
        when(organizationDao.findById(anyString())).thenReturn(buildOrganizationDefault());
        Admin admin1 = buildAdminDefault();
        Admin admin2 = buildAdminDefault();
        admin2.setPid(ADMIN_ID2);
        admin2.setSid(ADMIN_ID2);
        List<Admin> admins = Arrays.asList(admin1, admin2);
        when(organizationDao.findAllAdmins(anyString())).thenReturn(admins);
        Caregiver caregiver1 = buildCaregiverDefault();
        Caregiver caregiver2 = buildCaregiverDefault();
        caregiver2.setPid(CAREGIVER_ID2);
        caregiver2.setSid(CAREGIVER_ID2);
        List<Caregiver> caregivers = Arrays.asList(caregiver1, caregiver2);
        when(organizationDao.findAllCaregivers(anyString())).thenReturn(caregivers);

        GetOrganizationRequestBody requestBody = buildGetOrganizationRequestBody();
        GetOrganizationResponseBody responseBody = cut.getOrganization(requestBody);

        assertEquals(ORGANIZATION_NAME, responseBody.getOrganizationName());
        List<AdminSerialization> expectedAdmins = admins.stream().map(AdminSerialization::fromAdmin).collect(Collectors.toList());
        assertThat(responseBody.getAdmins()).containsExactlyInAnyOrderElementsOf(expectedAdmins);
        List<CaregiverSerialization> expectedCaregivers = caregivers.stream().map(CaregiverSerialization::fromCaregiver).collect(Collectors.toList());
        assertThat(responseBody.getCaregivers()).containsExactlyInAnyOrderElementsOf(expectedCaregivers);
    }

    @Test
    public void testGetOrganization_WHEN_OrganizationDaoFindByIdThrows_THEN_ThrowSameException() {
        NullPointerException toThrow = new NullPointerException();
        Mockito.doThrow(toThrow).when(organizationDao).findById(anyString());

        GetOrganizationRequestBody requestBody = buildGetOrganizationRequestBody();
        assertThatThrownBy(() -> cut.getOrganization(requestBody)).isSameAs(toThrow);
    }

    @Test
    public void testGetOrganization_WHEN_OrganizationDaoFindAllCaregiversThrows_THEN_ThrowSameException() {
        NullPointerException toThrow = new NullPointerException();
        Mockito.doThrow(toThrow).when(organizationDao).findAllCaregivers(anyString());

        GetOrganizationRequestBody requestBody = buildGetOrganizationRequestBody();
        assertThatThrownBy(() -> cut.getOrganization(requestBody)).isSameAs(toThrow);
    }

    @ParameterizedTest
    @MethodSource("invalidInputsForGetOrganization")
    public void testGetOrganization_WHEN_InvalidInput_THEN_ThrowInvalidInputException(GetOrganizationRequestBody body, String errorMessage) {
        assertInvalidInputExceptionThrown(() -> cut.getOrganization(body), errorMessage);
    }

    private static Stream<Arguments> invalidInputsForGetOrganization() {
        return Stream.of(
                Arguments.of(null, GET_ORGANIZATION_NULL_ERROR_MESSAGE),
                Arguments.of(buildGetOrganizationRequestBody(null), ORGANIZATION_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildGetOrganizationRequestBody(""), ORGANIZATION_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildGetOrganizationRequestBody(CAREGIVER_ID1), ORGANIZATION_ID_INVALID_ERROR_MESSAGE)
        );
    }

    private static CreateOrganizationRequestBody buildCreateOrganizationRequestBody() {
        return buildCreateOrganizationRequestBody(ORGANIZATION_NAME);
    }

    private static CreateOrganizationRequestBody buildCreateOrganizationRequestBody(String organizationName) {
        return CreateOrganizationRequestBody.builder()
                .organizationName(organizationName)
                .build();
    }

    private static GetOrganizationRequestBody buildGetOrganizationRequestBody() {
        return buildGetOrganizationRequestBody(ORGANIZATION_ID);
    }

    private static GetOrganizationRequestBody buildGetOrganizationRequestBody(String organizationId) {
        return GetOrganizationRequestBody.builder()
                .organizationId(organizationId)
                .build();
    }

    private static Organization buildOrganizationDefault() {
        return buildOrganization(ORGANIZATION_ID, ORGANIZATION_ID, ORGANIZATION_NAME);
    }

    private static Admin buildAdminDefault() {
        return buildAdmin(ADMIN_ID1, ADMIN_ID2, EMAIL, FIRST_NAME, LAST_NAME);
    }

    private static Caregiver buildCaregiverDefault() {
        return buildCaregiver(CAREGIVER_ID1, CAREGIVER_ID1, EMAIL, FIRST_NAME, LAST_NAME, TITLE, PHONE_NUMBER);
    }
}
