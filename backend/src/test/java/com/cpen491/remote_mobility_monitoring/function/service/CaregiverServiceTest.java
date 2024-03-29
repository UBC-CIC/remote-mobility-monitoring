package com.cpen491.remote_mobility_monitoring.function.service;

import com.cpen491.remote_mobility_monitoring.datastore.dao.CaregiverDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.OrganizationDao;
import com.cpen491.remote_mobility_monitoring.datastore.exception.RecordDoesNotExistException;
import com.cpen491.remote_mobility_monitoring.datastore.model.Caregiver;
import com.cpen491.remote_mobility_monitoring.datastore.model.Organization;
import com.cpen491.remote_mobility_monitoring.datastore.model.Patient;
import com.cpen491.remote_mobility_monitoring.dependency.auth.CognitoWrapper;
import com.cpen491.remote_mobility_monitoring.dependency.auth.CognitoWrapper.CognitoUser;
import com.cpen491.remote_mobility_monitoring.dependency.email.SesWrapper;
import com.cpen491.remote_mobility_monitoring.dependency.exception.CognitoException;
import com.cpen491.remote_mobility_monitoring.dependency.exception.InvalidAuthCodeException;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.AcceptPatientPrimaryRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.AcceptPatientPrimaryResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.AddPatientPrimaryRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.AddPatientPrimaryResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.AddPatientRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.AddPatientResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.CreateCaregiverRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.CreateCaregiverResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.DeleteCaregiverRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.DeleteCaregiverResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.GetAllPatientsRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.GetAllPatientsResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.GetAllPatientsResponseBody.PatientSerialization;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.GetCaregiverRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.GetCaregiverResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.RemovePatientRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.RemovePatientResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.UpdateCaregiverRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.UpdateCaregiverResponseBody;
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
import static com.cpen491.remote_mobility_monitoring.TestUtils.buildCaregiver;
import static com.cpen491.remote_mobility_monitoring.TestUtils.buildOrganization;
import static com.cpen491.remote_mobility_monitoring.TestUtils.buildPatient;
import static com.cpen491.remote_mobility_monitoring.dependency.auth.CognitoWrapper.CAREGIVER_GROUP_NAME;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.TimeUtils.getCurrentUtcTime;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.TimeUtils.getCurrentUtcTimeString;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.ACCEPT_PATIENT_PRIMARY_NULL_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.ADD_PATIENT_NULL_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.ADD_PATIENT_PRIMARY_NULL_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.AUTH_CODE_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.CAREGIVER_ID_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.CAREGIVER_ID_INVALID_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.CREATE_CAREGIVER_NULL_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.DELETE_CAREGIVER_NULL_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.EMAIL_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.FIRST_NAME_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.GET_ALL_PATIENTS_NULL_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.GET_CAREGIVER_NULL_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.LAST_NAME_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.ORGANIZATION_ID_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.ORGANIZATION_ID_INVALID_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.PATIENT_ID_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.PATIENT_ID_INVALID_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.PHONE_NUMBER_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.REMOVE_PATIENT_NULL_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.TITLE_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.UPDATE_CAREGIVER_NULL_ERROR_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CaregiverServiceTest {
    private static final String CAREGIVER_ID = "car-1";
    private static final String CAREGIVER_ID_NO_PREFIX = "1";
    private static final String PASSWORD = "password123";
    private static final String EMAIL = "jackjackson@email.com";
    private static final String TITLE1 = "caregiver";
    private static final String TITLE2 = "manager";
    private static final String FIRST_NAME = "Jack";
    private static final String LAST_NAME = "Jackson";
    private static final String PHONE_NUMBER = "1234567890";
    private static final String ORGANIZATION_ID = "org-1";
    private static final String ORGANIZATION_NAME = "Organization1";
    private static final String CREATED_AT = "2023-01-01";
    private static final String PATIENT_ID1 = "pat-1";
    private static final String PATIENT_ID2 = "pat-2";
    private static final String AUTH_CODE = "123456";
    private static final String AUTH_CODE_TIMESTAMP = getCurrentUtcTimeString();
    private static final String PATIENT_EMAIL = "patient@email.com";

    CaregiverService cut;
    @Mock
    CaregiverDao caregiverDao;
    @Mock
    OrganizationDao organizationDao;
    @Mock
    CognitoWrapper cognitoWrapper;
    @Mock
    SesWrapper sesWrapper;
    ArgumentCaptor<Caregiver> caregiverCaptor;

    @BeforeEach
    public void setup() {
        caregiverCaptor = ArgumentCaptor.forClass(Caregiver.class);
        cut = new CaregiverService(caregiverDao, organizationDao, cognitoWrapper, sesWrapper);
    }

    @Test
    public void testCreateCaregiver_HappyCase() {
        when(cognitoWrapper.createUserIfNotExistAndAddToGroup(anyString(), anyString(), isNull(), anyBoolean()))
                .thenReturn(new CognitoUser(CAREGIVER_ID_NO_PREFIX, PASSWORD));

        CreateCaregiverRequestBody requestBody = buildCreateCaregiverRequestBody();
        CreateCaregiverResponseBody responseBody = cut.createCaregiver(requestBody);

        verify(caregiverDao, times(1)).create(caregiverCaptor.capture(), eq(ORGANIZATION_ID));
        assertEquals(CAREGIVER_ID, caregiverCaptor.getValue().getPid());
        assertEquals(CAREGIVER_ID, caregiverCaptor.getValue().getSid());
        assertEquals(EMAIL, caregiverCaptor.getValue().getEmail());
        assertEquals(FIRST_NAME, caregiverCaptor.getValue().getFirstName());
        assertEquals(LAST_NAME, caregiverCaptor.getValue().getLastName());
        assertEquals(TITLE1, caregiverCaptor.getValue().getTitle());
        assertEquals(PHONE_NUMBER, caregiverCaptor.getValue().getPhoneNumber());
        assertNotNull(responseBody);
        assertEquals(CAREGIVER_ID, responseBody.getCaregiverId());
        assertEquals(PASSWORD, responseBody.getPassword());
    }

    @Test
    public void testCreateCaregiver_WHEN_OrganizationDaoFindByIdThrows_THEN_ThrowSameException() {
        NullPointerException toThrow = new NullPointerException();
        Mockito.doThrow(toThrow).when(organizationDao).findById(anyString());

        CreateCaregiverRequestBody requestBody = buildCreateCaregiverRequestBody();
        assertThatThrownBy(() -> cut.createCaregiver(requestBody)).isSameAs(toThrow);
    }

    @Test
    public void testCreateCaregiver_WHEN_CognitoWrapperThrows_THEN_ThrowSameException() {
        NullPointerException toThrow = new NullPointerException();
        Mockito.doThrow(toThrow).when(cognitoWrapper).createUserIfNotExistAndAddToGroup(anyString(), anyString(), isNull(), anyBoolean());

        CreateCaregiverRequestBody requestBody = buildCreateCaregiverRequestBody();
        assertThatThrownBy(() -> cut.createCaregiver(requestBody)).isSameAs(toThrow);
    }

    @Test
    public void testCreateCaregiver_WHEN_CaregiverDaoCreateThrows_THEN_ThrowSameException() {
        when(cognitoWrapper.createUserIfNotExistAndAddToGroup(anyString(), anyString(), isNull(), anyBoolean()))
                .thenReturn(new CognitoUser(CAREGIVER_ID_NO_PREFIX, PASSWORD));

        NullPointerException toThrow = new NullPointerException();
        Mockito.doThrow(toThrow).when(caregiverDao).create(any(Caregiver.class), anyString());

        CreateCaregiverRequestBody requestBody = buildCreateCaregiverRequestBody();
        assertThatThrownBy(() -> cut.createCaregiver(requestBody)).isSameAs(toThrow);
    }

    @ParameterizedTest
    @MethodSource("invalidInputsForCreateCaregiver")
    public void testCreateCaregiver_WHEN_InvalidInput_THEN_ThrowInvalidInputException(CreateCaregiverRequestBody body, String errorMessage) {
        assertInvalidInputExceptionThrown(() -> cut.createCaregiver(body), errorMessage);
    }

    private static Stream<Arguments> invalidInputsForCreateCaregiver() {
        return Stream.of(
                Arguments.of(null, CREATE_CAREGIVER_NULL_ERROR_MESSAGE),
                Arguments.of(buildCreateCaregiverRequestBody(null, FIRST_NAME, LAST_NAME, TITLE1, PHONE_NUMBER, ORGANIZATION_ID),
                        EMAIL_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreateCaregiverRequestBody("", FIRST_NAME, LAST_NAME, TITLE1, PHONE_NUMBER, ORGANIZATION_ID),
                        EMAIL_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreateCaregiverRequestBody(EMAIL, null, LAST_NAME, TITLE1, PHONE_NUMBER, ORGANIZATION_ID),
                        FIRST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreateCaregiverRequestBody(EMAIL, "", LAST_NAME, TITLE1, PHONE_NUMBER, ORGANIZATION_ID),
                        FIRST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreateCaregiverRequestBody(EMAIL, FIRST_NAME, null, TITLE1, PHONE_NUMBER, ORGANIZATION_ID),
                        LAST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreateCaregiverRequestBody(EMAIL, FIRST_NAME, "", TITLE1, PHONE_NUMBER, ORGANIZATION_ID),
                        LAST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreateCaregiverRequestBody(EMAIL, FIRST_NAME, LAST_NAME, null, PHONE_NUMBER, ORGANIZATION_ID),
                        TITLE_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreateCaregiverRequestBody(EMAIL, FIRST_NAME, LAST_NAME, "", PHONE_NUMBER, ORGANIZATION_ID),
                        TITLE_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreateCaregiverRequestBody(EMAIL, FIRST_NAME, LAST_NAME, TITLE1, null, ORGANIZATION_ID),
                        PHONE_NUMBER_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreateCaregiverRequestBody(EMAIL, FIRST_NAME, LAST_NAME, TITLE1, "", ORGANIZATION_ID),
                        PHONE_NUMBER_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreateCaregiverRequestBody(EMAIL, FIRST_NAME, LAST_NAME, TITLE1, PHONE_NUMBER, null),
                        ORGANIZATION_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreateCaregiverRequestBody(EMAIL, FIRST_NAME, LAST_NAME, TITLE1, PHONE_NUMBER, ""),
                        ORGANIZATION_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreateCaregiverRequestBody(EMAIL, FIRST_NAME, LAST_NAME, TITLE1, PHONE_NUMBER, CAREGIVER_ID),
                        ORGANIZATION_ID_INVALID_ERROR_MESSAGE)
        );
    }

    @Test
    public void testAddPatientPrimary_WHEN_SendEmailFalse_THEN_SesWrapperDoNotSendEmail() {
        AddPatientPrimaryRequestBody requestBody = buildAddPatientPrimaryRequestBody();
        AddPatientPrimaryResponseBody responseBody = cut.addPatientPrimary(requestBody);

        verify(caregiverDao, times(1)).addPatientPrimary(eq(PATIENT_EMAIL), eq(CAREGIVER_ID), anyString());
        verify(sesWrapper, never()).caregiverAddPatientEmail(anyString(), anyString(), anyString());
        assertNotNull(responseBody.getAuthCode());
    }

    @Test
    public void testAddPatientPrimary_WHEN_SendEmailTrue_THEN_SesWrapperSendEmail() {
        AddPatientPrimaryRequestBody requestBody = buildAddPatientPrimaryRequestBody();
        requestBody.setSendEmail(true);
        AddPatientPrimaryResponseBody responseBody = cut.addPatientPrimary(requestBody);

        verify(caregiverDao, times(1)).addPatientPrimary(eq(PATIENT_EMAIL), eq(CAREGIVER_ID), anyString());
        verify(sesWrapper, times(1)).caregiverAddPatientEmail(eq(PATIENT_EMAIL), eq(CAREGIVER_ID), anyString());
        assertNotNull(responseBody.getAuthCode());
    }

    @ParameterizedTest
    @MethodSource("invalidInputsForAddPatientPrimary")
    public void testAddPatientPrimary_WHEN_InvalidInput_THEN_ThrowInvalidInputException(AddPatientPrimaryRequestBody body, String errorMessage) {
        assertInvalidInputExceptionThrown(() -> cut.addPatientPrimary(body), errorMessage);
    }

    private static Stream<Arguments> invalidInputsForAddPatientPrimary() {
        return Stream.of(
                Arguments.of(null, ADD_PATIENT_PRIMARY_NULL_ERROR_MESSAGE),
                Arguments.of(buildAddPatientPrimaryRequestBody(null, PATIENT_EMAIL), CAREGIVER_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildAddPatientPrimaryRequestBody("", PATIENT_EMAIL), CAREGIVER_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildAddPatientPrimaryRequestBody(PATIENT_ID1, PATIENT_ID1), CAREGIVER_ID_INVALID_ERROR_MESSAGE),
                Arguments.of(buildAddPatientPrimaryRequestBody(CAREGIVER_ID, null), EMAIL_BLANK_ERROR_MESSAGE),
                Arguments.of(buildAddPatientPrimaryRequestBody(CAREGIVER_ID, ""), EMAIL_BLANK_ERROR_MESSAGE)
        );
    }

    @Test
    public void testAcceptPatientPrimary_HappyCase() {
        when(caregiverDao.findUnverifiedPrimaryCaregiver(anyString(), any())).thenReturn(buildUnverifiedPrimaryCaregiver());

        AcceptPatientPrimaryRequestBody requestBody = buildAcceptPatientPrimaryRequestBody();
        AcceptPatientPrimaryResponseBody responseBody = cut.acceptPatientPrimary(requestBody);

        verify(caregiverDao, times(1)).acceptPatientPrimary(eq(PATIENT_ID1), eq(CAREGIVER_ID));
        assertEquals("OK", responseBody.getMessage());
    }

    @Test
    public void testAcceptPatientPrimary_WHEN_AuthCodesDoNotMatch_THEN_ThrowInvalidAuthCodeException() {
        when(caregiverDao.findUnverifiedPrimaryCaregiver(anyString(), any())).thenReturn(buildUnverifiedPrimaryCaregiver());

        AcceptPatientPrimaryRequestBody requestBody = buildAcceptPatientPrimaryRequestBody();
        requestBody.setAuthCode(AUTH_CODE + "1");

        assertThatThrownBy(() -> cut.acceptPatientPrimary(requestBody)).isInstanceOf(InvalidAuthCodeException.class);
    }

    @Test
    public void testAcceptPatientPrimary_WHEN_AuthCodesExpired_THEN_ThrowInvalidAuthCodeException() {
        Caregiver caregiver = buildUnverifiedPrimaryCaregiver();
        String threeDaysAgo = getCurrentUtcTime().minusDays(3).toString();
        caregiver.setAuthCodeTimestamp(threeDaysAgo);
        when(caregiverDao.findUnverifiedPrimaryCaregiver(anyString(), any())).thenReturn(caregiver);

        AcceptPatientPrimaryRequestBody requestBody = buildAcceptPatientPrimaryRequestBody();

        assertThatThrownBy(() -> cut.acceptPatientPrimary(requestBody)).isInstanceOf(InvalidAuthCodeException.class);
    }

    @ParameterizedTest
    @MethodSource("invalidInputsForAcceptPatientPrimary")
    public void testAcceptPatientPrimary_WHEN_InvalidInput_THEN_ThrowInvalidInputException(AcceptPatientPrimaryRequestBody body, String errorMessage) {
        assertInvalidInputExceptionThrown(() -> cut.acceptPatientPrimary(body), errorMessage);
    }

    private static Stream<Arguments> invalidInputsForAcceptPatientPrimary() {
        return Stream.of(
                Arguments.of(null, ACCEPT_PATIENT_PRIMARY_NULL_ERROR_MESSAGE),
                Arguments.of(buildAcceptPatientPrimaryRequestBody(null, PATIENT_ID1, AUTH_CODE), CAREGIVER_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildAcceptPatientPrimaryRequestBody("", PATIENT_ID1, AUTH_CODE), CAREGIVER_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildAcceptPatientPrimaryRequestBody(PATIENT_ID1, PATIENT_ID1, AUTH_CODE), CAREGIVER_ID_INVALID_ERROR_MESSAGE),
                Arguments.of(buildAcceptPatientPrimaryRequestBody(CAREGIVER_ID, null, AUTH_CODE), PATIENT_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildAcceptPatientPrimaryRequestBody(CAREGIVER_ID, "", AUTH_CODE), PATIENT_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildAcceptPatientPrimaryRequestBody(CAREGIVER_ID, CAREGIVER_ID, AUTH_CODE), PATIENT_ID_INVALID_ERROR_MESSAGE),
                Arguments.of(buildAcceptPatientPrimaryRequestBody(CAREGIVER_ID, PATIENT_ID1, null), AUTH_CODE_BLANK_ERROR_MESSAGE),
                Arguments.of(buildAcceptPatientPrimaryRequestBody(CAREGIVER_ID, PATIENT_ID1, ""), AUTH_CODE_BLANK_ERROR_MESSAGE)
        );
    }

    @Test
    public void testAddPatient_HappyCase() {
        AddPatientRequestBody requestBody = buildAddPatientRequestBody();
        AddPatientResponseBody responseBody = cut.addPatient(requestBody);

        verify(caregiverDao, times(1)).addPatient(eq(PATIENT_ID1), eq(CAREGIVER_ID));
        assertEquals("OK", responseBody.getMessage());
    }

    @Test
    public void testAddPatient_WHEN_CaregiverDaoAddPatientThrows_THEN_ThrowSameException() {
        NullPointerException toThrow = new NullPointerException();
        Mockito.doThrow(toThrow).when(caregiverDao).addPatient(anyString(), anyString());

        AddPatientRequestBody requestBody = buildAddPatientRequestBody();
        assertThatThrownBy(() -> cut.addPatient(requestBody)).isSameAs(toThrow);
    }

    @ParameterizedTest
    @MethodSource("invalidInputsForAddPatient")
    public void testAddPatent_WHEN_InvalidInput_THEN_ThrowInvalidInputException(AddPatientRequestBody body, String errorMessage) {
        assertInvalidInputExceptionThrown(() -> cut.addPatient(body), errorMessage);
    }

    private static Stream<Arguments> invalidInputsForAddPatient() {
        return Stream.of(
                Arguments.of(null, ADD_PATIENT_NULL_ERROR_MESSAGE),
                Arguments.of(buildAddPatientRequestBody(null, PATIENT_ID1), CAREGIVER_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildAddPatientRequestBody("", PATIENT_ID1), CAREGIVER_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildAddPatientRequestBody(PATIENT_ID1, PATIENT_ID1), CAREGIVER_ID_INVALID_ERROR_MESSAGE),
                Arguments.of(buildAddPatientRequestBody(CAREGIVER_ID, null), PATIENT_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildAddPatientRequestBody(CAREGIVER_ID, ""), PATIENT_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildAddPatientRequestBody(CAREGIVER_ID, CAREGIVER_ID), PATIENT_ID_INVALID_ERROR_MESSAGE)
        );
    }

    @Test
    public void testRemovePatient_HappyCase() {
        RemovePatientRequestBody requestBody = buildRemovePatientRequestBody();
        RemovePatientResponseBody responseBody = cut.removePatient(requestBody);

        verify(caregiverDao, times(1)).removePatient(eq(PATIENT_ID1), eq(CAREGIVER_ID));
        assertEquals("OK", responseBody.getMessage());
    }

    @Test
    public void testRemovePatient_WHEN_CaregiverDaoRemovePatientThrows_THEN_ThrowSameException() {
        NullPointerException toThrow = new NullPointerException();
        Mockito.doThrow(toThrow).when(caregiverDao).removePatient(anyString(), anyString());

        RemovePatientRequestBody requestBody = buildRemovePatientRequestBody();
        assertThatThrownBy(() -> cut.removePatient(requestBody)).isSameAs(toThrow);
    }

    @ParameterizedTest
    @MethodSource("invalidInputsForRemovePatient")
    public void testRemovePatent_WHEN_InvalidInput_THEN_ThrowInvalidInputException(RemovePatientRequestBody body, String errorMessage) {
        assertInvalidInputExceptionThrown(() -> cut.removePatient(body), errorMessage);
    }

    private static Stream<Arguments> invalidInputsForRemovePatient() {
        return Stream.of(
                Arguments.of(null, REMOVE_PATIENT_NULL_ERROR_MESSAGE),
                Arguments.of(buildRemovePatientRequestBody(null, PATIENT_ID1), CAREGIVER_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildRemovePatientRequestBody("", PATIENT_ID1), CAREGIVER_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildRemovePatientRequestBody(PATIENT_ID1, PATIENT_ID1), CAREGIVER_ID_INVALID_ERROR_MESSAGE),
                Arguments.of(buildRemovePatientRequestBody(CAREGIVER_ID, null), PATIENT_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildRemovePatientRequestBody(CAREGIVER_ID, ""), PATIENT_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildRemovePatientRequestBody(CAREGIVER_ID, CAREGIVER_ID), PATIENT_ID_INVALID_ERROR_MESSAGE)
        );
    }

    @Test
    public void testGetCaregiver_HappyCase() {
        when(caregiverDao.findById(anyString())).thenReturn(buildCaregiverDefault());
        when(caregiverDao.findOrganization(anyString())).thenReturn(buildOrganizationDefault());

        GetCaregiverRequestBody requestBody = buildGetCaregiverRequestBody();
        GetCaregiverResponseBody responseBody = cut.getCaregiver(requestBody);

        assertEquals(EMAIL, responseBody.getEmail());
        assertEquals(FIRST_NAME, responseBody.getFirstName());
        assertEquals(LAST_NAME, responseBody.getLastName());
        assertEquals(TITLE1, responseBody.getTitle());
        assertEquals(PHONE_NUMBER, responseBody.getPhoneNumber());
        assertEquals(ORGANIZATION_ID, responseBody.getOrganizationId());
        assertEquals(ORGANIZATION_NAME, responseBody.getOrganizationName());
        assertEquals(CREATED_AT, responseBody.getCreatedAt());
    }

    @Test
    public void testGetCaregiver_WHEN_CaregiverDaoFindByIdThrows_THEN_ThrowSameException() {
        NullPointerException toThrow = new NullPointerException();
        Mockito.doThrow(toThrow).when(caregiverDao).findById(anyString());

        GetCaregiverRequestBody requestBody = buildGetCaregiverRequestBody();
        assertThatThrownBy(() -> cut.getCaregiver(requestBody)).isSameAs(toThrow);
    }

    @Test
    public void testGetCaregiver_WHEN_CaregiverDaoFindOrganizationThrows_THEN_ThrowSameException() {
        NullPointerException toThrow = new NullPointerException();
        Mockito.doThrow(toThrow).when(caregiverDao).findOrganization(anyString());

        GetCaregiverRequestBody requestBody = buildGetCaregiverRequestBody();
        assertThatThrownBy(() -> cut.getCaregiver(requestBody)).isSameAs(toThrow);
    }

    @ParameterizedTest
    @MethodSource("invalidInputsForGetCaregiver")
    public void testGetCaregiver_WHEN_InvalidInput_THEN_ThrowInvalidInputException(GetCaregiverRequestBody body, String errorMessage) {
        assertInvalidInputExceptionThrown(() -> cut.getCaregiver(body), errorMessage);
    }

    private static Stream<Arguments> invalidInputsForGetCaregiver() {
        return Stream.of(
                Arguments.of(null, GET_CAREGIVER_NULL_ERROR_MESSAGE),
                Arguments.of(buildGetCaregiverRequestBody(null), CAREGIVER_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildGetCaregiverRequestBody(""), CAREGIVER_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildGetCaregiverRequestBody(ORGANIZATION_ID), CAREGIVER_ID_INVALID_ERROR_MESSAGE)
        );
    }

    @Test
    public void testGetAllPatients_HappyCase() {
        Patient patient1 = buildPatientDefault();
        Patient patient2 = buildPatientDefault();
        patient2.setPid(PATIENT_ID2);
        patient2.setSid(PATIENT_ID2);
        patient2.setIsPrimary(true);
        patient2.setVerified(true);
        List<Patient> patients = Arrays.asList(patient1, patient2);
        when(caregiverDao.findAllPatients(anyString())).thenReturn(patients);

        GetAllPatientsRequestBody requestBody = buildGetAllPatientsRequestBody();
        GetAllPatientsResponseBody responseBody = cut.getAllPatients(requestBody);

        List<PatientSerialization> expected = patients.stream().map(PatientSerialization::fromPatient).collect(Collectors.toList());
        assertThat(responseBody.getPatients()).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    public void testGetAllPatients_WHEN_CaregiverHasNoPatients_THEN_ReturnEmptyPatients() {
        GetAllPatientsRequestBody requestBody = buildGetAllPatientsRequestBody();
        GetAllPatientsResponseBody responseBody = cut.getAllPatients(requestBody);

        assertThat(responseBody.getPatients()).isEmpty();
    }

    @Test
    public void testGetAllPatients_WHEN_CaregiverDaoFindByIdThrows_THEN_ThrowSameException() {
        NullPointerException toThrow = new NullPointerException();
        Mockito.doThrow(toThrow).when(caregiverDao).findById(anyString());

        GetAllPatientsRequestBody requestBody = buildGetAllPatientsRequestBody();
        assertThatThrownBy(() -> cut.getAllPatients(requestBody)).isSameAs(toThrow);
    }

    @Test
    public void testGetAllPatients_WHEN_CaregiverDaoFindAllPatientsThrows_THEN_ThrowSameException() {
        NullPointerException toThrow = new NullPointerException();
        Mockito.doThrow(toThrow).when(caregiverDao).findAllPatients(anyString());

        GetAllPatientsRequestBody requestBody = buildGetAllPatientsRequestBody();
        assertThatThrownBy(() -> cut.getAllPatients(requestBody)).isSameAs(toThrow);
    }

    @ParameterizedTest
    @MethodSource("invalidInputsForGetAllPatients")
    public void testGetAllPatients_WHEN_InvalidInput_THEN_ThrowInvalidInputException(GetAllPatientsRequestBody body, String errorMessage) {
        assertInvalidInputExceptionThrown(() -> cut.getAllPatients(body), errorMessage);
    }

    private static Stream<Arguments> invalidInputsForGetAllPatients() {
        return Stream.of(
                Arguments.of(null, GET_ALL_PATIENTS_NULL_ERROR_MESSAGE),
                Arguments.of(buildGetAllPatientsRequestBody(null), CAREGIVER_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildGetAllPatientsRequestBody(""), CAREGIVER_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildGetAllPatientsRequestBody(ORGANIZATION_ID), CAREGIVER_ID_INVALID_ERROR_MESSAGE)
        );
    }

    @Test
    public void testUpdateCaregiver_HappyCase() {
        when(caregiverDao.findById(anyString())).thenReturn(buildCaregiverDefault());

        UpdateCaregiverRequestBody requestBody = buildUpdateCaregiverRequestBody();
        requestBody.setTitle(TITLE2);
        UpdateCaregiverResponseBody responseBody = cut.updateCaregiver(requestBody);

        verify(caregiverDao, times(1)).update(caregiverCaptor.capture());
        assertEquals(EMAIL, caregiverCaptor.getValue().getEmail());
        assertEquals(FIRST_NAME, caregiverCaptor.getValue().getFirstName());
        assertEquals(LAST_NAME, caregiverCaptor.getValue().getLastName());
        assertEquals(TITLE2, caregiverCaptor.getValue().getTitle());
        assertEquals(PHONE_NUMBER, caregiverCaptor.getValue().getPhoneNumber());
        assertEquals("OK", responseBody.getMessage());
    }

    @Test
    public void testUpdateCaregiver_WHEN_CaregiverDaoFindByIdThrows_THEN_ThrowSameException() {
        NullPointerException toThrow = new NullPointerException();
        Mockito.doThrow(toThrow).when(caregiverDao).findById(anyString());

        UpdateCaregiverRequestBody requestBody = buildUpdateCaregiverRequestBody();
        assertThatThrownBy(() -> cut.updateCaregiver(requestBody)).isSameAs(toThrow);
    }

    @Test
    public void testUpdateCaregiver_WHEN_CaregiverDaoUpdateThrows_THEN_ThrowSameException() {
        when(caregiverDao.findById(anyString())).thenReturn(buildCaregiverDefault());

        NullPointerException toThrow = new NullPointerException();
        Mockito.doThrow(toThrow).when(caregiverDao).update(any(Caregiver.class));

        UpdateCaregiverRequestBody requestBody = buildUpdateCaregiverRequestBody();
        assertThatThrownBy(() -> cut.updateCaregiver(requestBody)).isSameAs(toThrow);
    }

    @ParameterizedTest
    @MethodSource("invalidInputsForUpdateCaregiver")
    public void testUpdateCaregiver_WHEN_InvalidInput_THEN_ThrowInvalidInputException(UpdateCaregiverRequestBody body, String errorMessage) {
        assertInvalidInputExceptionThrown(() -> cut.updateCaregiver(body), errorMessage);
    }

    private static Stream<Arguments> invalidInputsForUpdateCaregiver() {
        return Stream.of(
                Arguments.of(null, UPDATE_CAREGIVER_NULL_ERROR_MESSAGE),
                Arguments.of(buildUpdateCaregiverRequestBody(null, FIRST_NAME, LAST_NAME, TITLE1, PHONE_NUMBER),
                        CAREGIVER_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildUpdateCaregiverRequestBody("", FIRST_NAME, LAST_NAME, TITLE1, PHONE_NUMBER),
                        CAREGIVER_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildUpdateCaregiverRequestBody(PATIENT_ID1, FIRST_NAME, LAST_NAME, TITLE1, PHONE_NUMBER),
                        CAREGIVER_ID_INVALID_ERROR_MESSAGE),
                Arguments.of(buildUpdateCaregiverRequestBody(CAREGIVER_ID, null, LAST_NAME, TITLE1, PHONE_NUMBER),
                        FIRST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildUpdateCaregiverRequestBody(CAREGIVER_ID, "", LAST_NAME, TITLE1, PHONE_NUMBER),
                        FIRST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildUpdateCaregiverRequestBody(CAREGIVER_ID, FIRST_NAME, null, TITLE1, PHONE_NUMBER),
                        LAST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildUpdateCaregiverRequestBody(CAREGIVER_ID, FIRST_NAME, "", TITLE1, PHONE_NUMBER),
                        LAST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildUpdateCaregiverRequestBody(CAREGIVER_ID, FIRST_NAME, LAST_NAME, null, PHONE_NUMBER),
                        TITLE_BLANK_ERROR_MESSAGE),
                Arguments.of(buildUpdateCaregiverRequestBody(CAREGIVER_ID, FIRST_NAME, LAST_NAME, "", PHONE_NUMBER),
                        TITLE_BLANK_ERROR_MESSAGE),
                Arguments.of(buildUpdateCaregiverRequestBody(CAREGIVER_ID, FIRST_NAME, LAST_NAME, TITLE1, null),
                        PHONE_NUMBER_BLANK_ERROR_MESSAGE),
                Arguments.of(buildUpdateCaregiverRequestBody(CAREGIVER_ID, FIRST_NAME, LAST_NAME, TITLE1, ""),
                        PHONE_NUMBER_BLANK_ERROR_MESSAGE)
        );
    }

    @Test
    public void testDeleteCaregiver_HappyCase() {
        when(caregiverDao.findById(anyString())).thenReturn(buildCaregiverDefault());

        DeleteCaregiverRequestBody requestBody = buildDeleteCaregiverRequestBody();
        DeleteCaregiverResponseBody responseBody = cut.deleteCaregiver(requestBody);

        verify(cognitoWrapper, times(1)).removeUserFromGroupAndDeleteUser(eq(EMAIL), eq(CAREGIVER_GROUP_NAME));
        verify(caregiverDao, times(1)).delete(eq(CAREGIVER_ID));
        assertEquals("OK", responseBody.getMessage());
    }

    @Test
    public void testDeleteCaregiver_WHEN_CaregiverDaoFindByIdThrows_THEN_NoThrow() {
        Mockito.doThrow(RecordDoesNotExistException.class).when(caregiverDao).findById(anyString());

        DeleteCaregiverRequestBody requestBody = buildDeleteCaregiverRequestBody();
        DeleteCaregiverResponseBody responseBody = cut.deleteCaregiver(requestBody);

        verify(cognitoWrapper, never()).removeUserFromGroupAndDeleteUser(anyString(), anyString());
        verify(caregiverDao, times(1)).delete(eq(CAREGIVER_ID));
        assertEquals("OK", responseBody.getMessage());
    }

    @Test
    public void testDeleteCaregiver_WHEN_CognitoWrapperThrows_THEN_NoThrow() {
        when(caregiverDao.findById(anyString())).thenReturn(buildCaregiverDefault());

        Mockito.doThrow(CognitoException.class).when(cognitoWrapper).removeUserFromGroupAndDeleteUser(anyString(), anyString());

        DeleteCaregiverRequestBody requestBody = buildDeleteCaregiverRequestBody();
        DeleteCaregiverResponseBody responseBody = cut.deleteCaregiver(requestBody);

        verify(caregiverDao, times(1)).delete(eq(CAREGIVER_ID));
        assertEquals("OK", responseBody.getMessage());
    }

    @Test
    public void testDeleteCaregiver_WHEN_CaregiverDaoDeleteThrows_THEN_ThrowSameException() {
        NullPointerException toThrow = new NullPointerException();
        Mockito.doThrow(toThrow).when(caregiverDao).delete(anyString());

        DeleteCaregiverRequestBody requestBody = buildDeleteCaregiverRequestBody();
        assertThatThrownBy(() -> cut.deleteCaregiver(requestBody)).isSameAs(toThrow);
    }

    @ParameterizedTest
    @MethodSource("invalidInputsForDeleteCaregiver")
    public void testDeleteCaregiver_WHEN_InvalidInput_THEN_ThrowInvalidInputException(DeleteCaregiverRequestBody body, String errorMessage) {
        assertInvalidInputExceptionThrown(() -> cut.deleteCaregiver(body), errorMessage);
    }

    private static Stream<Arguments> invalidInputsForDeleteCaregiver() {
        return Stream.of(
                Arguments.of(null, DELETE_CAREGIVER_NULL_ERROR_MESSAGE),
                Arguments.of(buildDeleteCaregiverRequestBody(null), CAREGIVER_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildDeleteCaregiverRequestBody(""), CAREGIVER_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildDeleteCaregiverRequestBody(ORGANIZATION_ID), CAREGIVER_ID_INVALID_ERROR_MESSAGE)
        );
    }

    private static CreateCaregiverRequestBody buildCreateCaregiverRequestBody() {
        return buildCreateCaregiverRequestBody(EMAIL, FIRST_NAME, LAST_NAME, TITLE1, PHONE_NUMBER, ORGANIZATION_ID);
    }

    private static CreateCaregiverRequestBody buildCreateCaregiverRequestBody(String email, String firstName, String lastName,
                                                                              String title, String phoneNumber, String organizationId) {
        return CreateCaregiverRequestBody.builder()
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .title(title)
                .phoneNumber(phoneNumber)
                .organizationId(organizationId)
                .build();
    }

    private static AddPatientPrimaryRequestBody buildAddPatientPrimaryRequestBody() {
        return buildAddPatientPrimaryRequestBody(CAREGIVER_ID, PATIENT_EMAIL);
    }

    private static AddPatientPrimaryRequestBody buildAddPatientPrimaryRequestBody(String caregiverId, String patientEmail) {
        return AddPatientPrimaryRequestBody.builder()
                .caregiverId(caregiverId)
                .patientEmail(patientEmail)
                .build();
    }

    private static AcceptPatientPrimaryRequestBody buildAcceptPatientPrimaryRequestBody() {
        return buildAcceptPatientPrimaryRequestBody(CAREGIVER_ID, PATIENT_ID1, AUTH_CODE);
    }

    private static AcceptPatientPrimaryRequestBody buildAcceptPatientPrimaryRequestBody(String caregiverId, String patientId, String authCode) {
        return AcceptPatientPrimaryRequestBody.builder()
                .caregiverId(caregiverId)
                .patientId(patientId)
                .authCode(authCode)
                .build();
    }

    private static AddPatientRequestBody buildAddPatientRequestBody() {
        return buildAddPatientRequestBody(CAREGIVER_ID, PATIENT_ID1);
    }

    private static AddPatientRequestBody buildAddPatientRequestBody(String caregiverId, String patientId) {
        return AddPatientRequestBody.builder()
                .caregiverId(caregiverId)
                .patientId(patientId)
                .build();
    }

    private static RemovePatientRequestBody buildRemovePatientRequestBody() {
        return buildRemovePatientRequestBody(CAREGIVER_ID, PATIENT_ID1);
    }

    private static RemovePatientRequestBody buildRemovePatientRequestBody(String caregiverId, String patientId) {
        return RemovePatientRequestBody.builder()
                .caregiverId(caregiverId)
                .patientId(patientId)
                .build();
    }

    private static GetCaregiverRequestBody buildGetCaregiverRequestBody() {
        return buildGetCaregiverRequestBody(CAREGIVER_ID);
    }

    private static GetCaregiverRequestBody buildGetCaregiverRequestBody(String caregiverId) {
        return GetCaregiverRequestBody.builder()
                .caregiverId(caregiverId)
                .build();
    }

    private static GetAllPatientsRequestBody buildGetAllPatientsRequestBody() {
        return buildGetAllPatientsRequestBody(CAREGIVER_ID);
    }

    private static GetAllPatientsRequestBody buildGetAllPatientsRequestBody(String caregiverId) {
        return GetAllPatientsRequestBody.builder()
                .caregiverId(caregiverId)
                .build();
    }

    private static UpdateCaregiverRequestBody buildUpdateCaregiverRequestBody() {
        return buildUpdateCaregiverRequestBody(CAREGIVER_ID, FIRST_NAME, LAST_NAME, TITLE1, PHONE_NUMBER);
    }

    private static UpdateCaregiverRequestBody buildUpdateCaregiverRequestBody(String caregiverId, String firstName, String lastName,
                                                                              String title, String phoneNumber) {
        return UpdateCaregiverRequestBody.builder()
                .caregiverId(caregiverId)
                .firstName(firstName)
                .lastName(lastName)
                .title(title)
                .phoneNumber(phoneNumber)
                .build();
    }

    private static DeleteCaregiverRequestBody buildDeleteCaregiverRequestBody() {
        return buildDeleteCaregiverRequestBody(CAREGIVER_ID);
    }

    private static DeleteCaregiverRequestBody buildDeleteCaregiverRequestBody(String caregiverId) {
        return DeleteCaregiverRequestBody.builder()
                .caregiverId(caregiverId)
                .build();
    }

    private static Caregiver buildCaregiverDefault() {
        Caregiver caregiver = buildCaregiver(CAREGIVER_ID, CAREGIVER_ID, EMAIL, FIRST_NAME, LAST_NAME, TITLE1, PHONE_NUMBER);
        caregiver.setCreatedAt(CREATED_AT);
        return caregiver;
    }

    private static Caregiver buildUnverifiedPrimaryCaregiver() {
        Caregiver caregiver = buildCaregiverDefault();
        caregiver.setAuthCode(AUTH_CODE);
        caregiver.setAuthCodeTimestamp(AUTH_CODE_TIMESTAMP);
        return caregiver;
    }

    private static Organization buildOrganizationDefault() {
        return buildOrganization(ORGANIZATION_ID, ORGANIZATION_ID, ORGANIZATION_NAME);
    }

    private static Patient buildPatientDefault() {
        return buildPatient(PATIENT_ID1, PATIENT_ID1, PATIENT_EMAIL, null, FIRST_NAME, LAST_NAME, PHONE_NUMBER);
    }
}
