package com.cpen491.remote_mobility_monitoring.function.service;

import com.cpen491.remote_mobility_monitoring.datastore.dao.OrganizationDao;
import com.cpen491.remote_mobility_monitoring.datastore.model.Caregiver;
import com.cpen491.remote_mobility_monitoring.datastore.model.Organization;
import com.cpen491.remote_mobility_monitoring.function.schema.organization.GetOrganizationRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.organization.GetOrganizationResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.organization.GetOrganizationResponseBody.CaregiverSerialization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.cpen491.remote_mobility_monitoring.TestUtils.assertInvalidInputExceptionThrown;
import static com.cpen491.remote_mobility_monitoring.TestUtils.buildCaregiver;
import static com.cpen491.remote_mobility_monitoring.TestUtils.buildOrganization;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.GET_ORGANIZATION_NULL_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.ORGANIZATION_ID_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.ORGANIZATION_ID_INVALID_ERROR_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrganizationServiceTest {
    private static final String ORGANIZATION_ID = "org-1";
    private static final String ORGANIZATION_NAME = "Organization1";
    private static final String CAREGIVER_ID1 = "car-1";
    private static final String CAREGIVER_ID2 = "car-2";
    private static final String EMAIL = "jackjackson@email.com";
    private static final String TITLE = "caregiver";
    private static final String FIRST_NAME = "Jack";
    private static final String LAST_NAME = "Jackson";
    private static final String PHONE_NUMBER = "1234567890";

    OrganizationService cut;
    @Mock
    OrganizationDao organizationDao;

    @BeforeEach
    public void setup() {
        cut = new OrganizationService(organizationDao);
    }

    @Test
    public void testGetOrganization_HappyCase() {
        when(organizationDao.findById(anyString())).thenReturn(buildOrganizationDefault());
        Caregiver caregiver1 = buildCaregiverDefault();
        Caregiver caregiver2 = buildCaregiverDefault();
        caregiver2.setPid(CAREGIVER_ID2);
        caregiver2.setSid(CAREGIVER_ID2);
        List<Caregiver> caregivers = Arrays.asList(caregiver1, caregiver2);
        when(organizationDao.findAllCaregivers(anyString())).thenReturn(caregivers);

        GetOrganizationRequestBody requestBody = buildGetOrganizationRequestBody();
        GetOrganizationResponseBody responseBody = cut.getOrganization(requestBody);

        assertEquals(ORGANIZATION_NAME, responseBody.getOrganizationName());
        List<CaregiverSerialization> expected = caregivers.stream().map(CaregiverSerialization::fromCaregiver).collect(Collectors.toList());
        assertThat(responseBody.getCaregivers()).hasSameElementsAs(expected);
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

    private static Caregiver buildCaregiverDefault() {
        return buildCaregiver(CAREGIVER_ID1, CAREGIVER_ID1, EMAIL, FIRST_NAME, LAST_NAME, TITLE, PHONE_NUMBER);
    }
}
