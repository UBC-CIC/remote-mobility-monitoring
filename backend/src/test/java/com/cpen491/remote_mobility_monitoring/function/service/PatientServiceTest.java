package com.cpen491.remote_mobility_monitoring.function.service;

import com.cpen491.remote_mobility_monitoring.datastore.dao.CaregiverDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.PatientDao;
import com.cpen491.remote_mobility_monitoring.datastore.exception.DuplicateRecordException;
import com.cpen491.remote_mobility_monitoring.datastore.model.Caregiver;
import com.cpen491.remote_mobility_monitoring.datastore.model.Patient;
import com.cpen491.remote_mobility_monitoring.dependency.exception.InvalidAuthCodeException;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.CreatePatientRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.CreatePatientResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.DeletePatientRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.DeletePatientResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.GetAllCaregiversRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.GetAllCaregiversResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.GetAllCaregiversResponseBody.CaregiverSerialization;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.GetPatientRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.GetPatientResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.UpdatePatientDeviceRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.UpdatePatientDeviceResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.UpdatePatientRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.UpdatePatientResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.VerifyPatientRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.VerifyPatientResponseBody;
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
import static com.cpen491.remote_mobility_monitoring.TestUtils.buildPatient;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.TimeUtils.getCurrentUtcTime;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.TimeUtils.getCurrentUtcTimeString;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.AUTH_CODE_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.CAREGIVER_ID_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.CAREGIVER_ID_INVALID_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.CREATE_PATIENT_NULL_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.DELETE_PATIENT_NULL_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.DEVICE_ID_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.FIRST_NAME_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.GET_ALL_CAREGIVERS_NULL_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.GET_PATIENT_NULL_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.LAST_NAME_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.PATIENT_ID_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.PATIENT_ID_INVALID_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.PHONE_NUMBER_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.UPDATE_PATIENT_DEVICE_NULL_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.UPDATE_PATIENT_NULL_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.VERIFY_PATIENT_NULL_ERROR_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {
    private static final String EMAIL = "jackjackson@email.com";
    private static final String TITLE = "caregiver";
    private static final String FIRST_NAME = "Jack";
    private static final String LAST_NAME = "Jackson";
    private static final String PHONE_NUMBER1 = "1234567890";
    private static final String PHONE_NUMBER2 = "1313131313";
    private static final String PATIENT_ID = "pat-1";
    private static final String CAREGIVER_ID1 = "car-1";
    private static final String CAREGIVER_ID2 = "car-2";
    private static final String AUTH_CODE = "auth_code-123";
    private static final String AUTH_CODE_TIMESTAMP = getCurrentUtcTimeString();
    private static final String DEVICE_ID = "device-id-1";
    private static final String CREATED_AT = "2023-01-01";

    PatientService cut;
    @Mock
    PatientDao patientDao;
    @Mock
    CaregiverDao caregiverDao;
    ArgumentCaptor<Patient> patientCaptor;
    ArgumentCaptor<Caregiver> caregiverCaptor;

    @BeforeEach
    public void setup() {
        patientCaptor = ArgumentCaptor.forClass(Patient.class);
        caregiverCaptor = ArgumentCaptor.forClass(Caregiver.class);
        cut = new PatientService(patientDao, caregiverDao);
    }

    @Test
    public void testCreatePatient_HappyCase() {
        CreatePatientRequestBody requestBody = buildCreatePatientRequestBody();
        CreatePatientResponseBody responseBody = cut.createPatient(requestBody);

        verify(patientDao, times(1)).create(patientCaptor.capture());
        assertEquals(FIRST_NAME, patientCaptor.getValue().getFirstName());
        assertEquals(LAST_NAME, patientCaptor.getValue().getLastName());
        assertEquals(PHONE_NUMBER1, patientCaptor.getValue().getPhoneNumber());
        assertNotEquals(AUTH_CODE, patientCaptor.getValue().getAuthCode());
        assertNotEquals(AUTH_CODE_TIMESTAMP, patientCaptor.getValue().getAuthCodeTimestamp());
        assertNotNull(responseBody);
        assertEquals(responseBody.getAuthCode(), patientCaptor.getValue().getAuthCode());
    }

    @Test
    public void testCreatePatient_WHEN_PatientDaoCreateThrows_THEN_ThrowSameException() {
        NullPointerException toThrow = new NullPointerException();
        Mockito.doThrow(toThrow).when(patientDao).create(any(Patient.class));

        CreatePatientRequestBody requestBody = buildCreatePatientRequestBody();
        assertThatThrownBy(() -> cut.createPatient(requestBody)).isSameAs(toThrow);
    }

    @ParameterizedTest
    @MethodSource("invalidInputsForCreatePatient")
    public void testCreatePatient_WHEN_InvalidInput_THEN_ThrowInvalidInputException(CreatePatientRequestBody body, String errorMessage) {
        assertInvalidInputExceptionThrown(() -> cut.createPatient(body), errorMessage);
    }

    private static Stream<Arguments> invalidInputsForCreatePatient() {
        return Stream.of(
                Arguments.of(null, CREATE_PATIENT_NULL_ERROR_MESSAGE),
                Arguments.of(buildCreatePatientRequestBody(null, LAST_NAME, PHONE_NUMBER1), FIRST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreatePatientRequestBody("", LAST_NAME, PHONE_NUMBER1), FIRST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreatePatientRequestBody(FIRST_NAME, null, PHONE_NUMBER1), LAST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreatePatientRequestBody(FIRST_NAME, "", PHONE_NUMBER1), LAST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreatePatientRequestBody(FIRST_NAME, LAST_NAME, null), PHONE_NUMBER_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreatePatientRequestBody(FIRST_NAME, LAST_NAME, ""), PHONE_NUMBER_BLANK_ERROR_MESSAGE)
        );
    }

    @Test
    public void testUpdatePatientDevice_HappyCase() {
        when(patientDao.findById(anyString())).thenReturn(buildPatientDefault());

        UpdatePatientDeviceRequestBody requestBody = buildUpdatePatientDeviceRequestBody();
        UpdatePatientDeviceResponseBody responseBody = cut.updatePatientDevice(requestBody);

        verify(patientDao, times(1)).update(patientCaptor.capture());
        assertNotEquals(AUTH_CODE, patientCaptor.getValue().getAuthCode());
        assertNotEquals(AUTH_CODE_TIMESTAMP, patientCaptor.getValue().getAuthCodeTimestamp());
        assertNotNull(responseBody);
        assertEquals(responseBody.getAuthCode(), patientCaptor.getValue().getAuthCode());
    }

    @Test
    public void testUpdatePatientDevice_WHEN_PatientDaoFindByIdThrows_THEN_ThrowSameException() {
        NullPointerException toThrow = new NullPointerException();
        Mockito.doThrow(toThrow).when(patientDao).findById(anyString());

        UpdatePatientDeviceRequestBody requestBody = buildUpdatePatientDeviceRequestBody();
        assertThatThrownBy(() -> cut.updatePatientDevice(requestBody)).isSameAs(toThrow);
    }

    @Test
    public void testUpdatePatientDevice_WHEN_PatientDaoUpdateThrows_THEN_ThrowSameException() {
        when(patientDao.findById(anyString())).thenReturn(buildPatientDefault());

        NullPointerException toThrow = new NullPointerException();
        Mockito.doThrow(toThrow).when(patientDao).update(any(Patient.class));

        UpdatePatientDeviceRequestBody requestBody = buildUpdatePatientDeviceRequestBody();
        assertThatThrownBy(() -> cut.updatePatientDevice(requestBody)).isSameAs(toThrow);
    }

    @ParameterizedTest
    @MethodSource("invalidInputsForUpdatePatientDevice")
    public void testUpdatePatientDevice_WHEN_InvalidInput_THEN_ThrowInvalidInputException(UpdatePatientDeviceRequestBody body, String errorMessage) {
        assertInvalidInputExceptionThrown(() -> cut.updatePatientDevice(body), errorMessage);
    }

    private static Stream<Arguments> invalidInputsForUpdatePatientDevice() {
        return Stream.of(
                Arguments.of(null, UPDATE_PATIENT_DEVICE_NULL_ERROR_MESSAGE),
                Arguments.of(buildUpdatePatientDeviceRequestBody(null), PATIENT_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildUpdatePatientDeviceRequestBody(""), PATIENT_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildUpdatePatientDeviceRequestBody(CAREGIVER_ID1), PATIENT_ID_INVALID_ERROR_MESSAGE)
        );
    }

    @Test
    public void testVerifyPatient_HappyCase() {
        when(patientDao.findById(anyString())).thenReturn(buildPatientDefault());

        VerifyPatientRequestBody requestBody = buildVerifyPatientRequestBody();
        VerifyPatientResponseBody responseBody = cut.verifyPatient(requestBody);

        verify(patientDao, times(1)).update(patientCaptor.capture());
        assertTrue(patientCaptor.getValue().getVerified());
        assertEquals(DEVICE_ID, patientCaptor.getValue().getDeviceId());

        verify(caregiverDao, times(1)).addPatient(eq(PATIENT_ID), eq(CAREGIVER_ID1));

        assertNotNull(responseBody);
        assertEquals("OK", responseBody.getMessage());
    }

    @Test
    public void testVerifyPatient_WHEN_PatientDaoFindByIdThrows_THEN_ThrowSameException() {
        NullPointerException toThrow = new NullPointerException();
        Mockito.doThrow(toThrow).when(patientDao).findById(anyString());

        VerifyPatientRequestBody requestBody = buildVerifyPatientRequestBody();
        assertThatThrownBy(() -> cut.verifyPatient(requestBody)).isSameAs(toThrow);
    }

    @Test
    public void testVerifyPatient_WHEN_CaregiverDaoFindByIdThrows_THEN_ThrowSameException() {
        when(patientDao.findById(anyString())).thenReturn(buildPatientDefault());

        NullPointerException toThrow = new NullPointerException();
        Mockito.doThrow(toThrow).when(caregiverDao).findById(anyString());

        VerifyPatientRequestBody requestBody = buildVerifyPatientRequestBody();
        assertThatThrownBy(() -> cut.verifyPatient(requestBody)).isSameAs(toThrow);
    }

    @Test
    public void testVerifyPatient_WHEN_PatientDaoUpdateThrows_THEN_ThrowSameException() {
        when(patientDao.findById(anyString())).thenReturn(buildPatientDefault());

        NullPointerException toThrow = new NullPointerException();
        Mockito.doThrow(toThrow).when(patientDao).update(any(Patient.class));

        VerifyPatientRequestBody requestBody = buildVerifyPatientRequestBody();
        assertThatThrownBy(() -> cut.verifyPatient(requestBody)).isSameAs(toThrow);
    }

    @Test
    public void testVerifyPatient_WHEN_CaregiverDaoAddPatientThrowsDuplicationRecordException_THEN_NoThrow() {
        when(patientDao.findById(anyString())).thenReturn(buildPatientDefault());
        Mockito.doThrow(DuplicateRecordException.class).when(caregiverDao).addPatient(anyString(), anyString());

        VerifyPatientRequestBody requestBody = buildVerifyPatientRequestBody();
        assertDoesNotThrow(() -> cut.verifyPatient(requestBody));
    }

    @Test
    public void testVerifyPatient_WHEN_AuthCodesDoNotMatch_THEN_ThrowInvalidAuthCodeException() {
        when(patientDao.findById(anyString())).thenReturn(buildPatientDefault());

        VerifyPatientRequestBody requestBody = buildVerifyPatientRequestBody();
        requestBody.setAuthCode(AUTH_CODE + "1");
        assertThatThrownBy(() -> cut.verifyPatient(requestBody)).isInstanceOf(InvalidAuthCodeException.class);
    }

    @Test
    public void testVerifyPatient_WHEN_AuthCodeExpired_THEN_ThrowInvalidAuthCodeException() {
        Patient patient = buildPatientDefault();
        String tenMinutesAgo = getCurrentUtcTime().minusMinutes(10).toString();
        patient.setAuthCodeTimestamp(tenMinutesAgo);
        when(patientDao.findById(anyString())).thenReturn(patient);

        VerifyPatientRequestBody requestBody = buildVerifyPatientRequestBody();
        assertThatThrownBy(() -> cut.verifyPatient(requestBody)).isInstanceOf(InvalidAuthCodeException.class);
    }

    @ParameterizedTest
    @MethodSource("invalidInputsForVerifyPatient")
    public void testVerifyPatient_WHEN_InvalidInput_THEN_ThrowInvalidInputException(VerifyPatientRequestBody body, String errorMessage) {
        assertInvalidInputExceptionThrown(() -> cut.verifyPatient(body), errorMessage);
    }

    private static Stream<Arguments> invalidInputsForVerifyPatient() {
        return Stream.of(
                Arguments.of(null, VERIFY_PATIENT_NULL_ERROR_MESSAGE),
                Arguments.of(buildVerifyPatientRequestBody(null, PATIENT_ID, AUTH_CODE, DEVICE_ID), CAREGIVER_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildVerifyPatientRequestBody("", PATIENT_ID, AUTH_CODE, DEVICE_ID), CAREGIVER_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildVerifyPatientRequestBody(PATIENT_ID, PATIENT_ID, AUTH_CODE, DEVICE_ID), CAREGIVER_ID_INVALID_ERROR_MESSAGE),
                Arguments.of(buildVerifyPatientRequestBody(CAREGIVER_ID1, null, AUTH_CODE, DEVICE_ID), PATIENT_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildVerifyPatientRequestBody(CAREGIVER_ID1, "", AUTH_CODE, DEVICE_ID), PATIENT_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildVerifyPatientRequestBody(CAREGIVER_ID1, CAREGIVER_ID1, AUTH_CODE, DEVICE_ID), PATIENT_ID_INVALID_ERROR_MESSAGE),
                Arguments.of(buildVerifyPatientRequestBody(CAREGIVER_ID1, PATIENT_ID, null, DEVICE_ID), AUTH_CODE_BLANK_ERROR_MESSAGE),
                Arguments.of(buildVerifyPatientRequestBody(CAREGIVER_ID1, PATIENT_ID, "", DEVICE_ID), AUTH_CODE_BLANK_ERROR_MESSAGE),
                Arguments.of(buildVerifyPatientRequestBody(CAREGIVER_ID1, PATIENT_ID, AUTH_CODE, null), DEVICE_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildVerifyPatientRequestBody(CAREGIVER_ID1, PATIENT_ID, AUTH_CODE, ""), DEVICE_ID_BLANK_ERROR_MESSAGE)
        );
    }

    @Test
    public void testGetPatient_HappyCase() {
        when(patientDao.findById(anyString())).thenReturn(buildPatientDefault());

        GetPatientRequestBody requestBody = buildGetPatientRequestBody();
        GetPatientResponseBody responseBody = cut.getPatient(requestBody);

        assertNull(responseBody.getDeviceId());
        assertEquals(FIRST_NAME, responseBody.getFirstName());
        assertEquals(LAST_NAME, responseBody.getLastName());
        assertEquals(PHONE_NUMBER1, responseBody.getPhoneNumber());
        assertEquals(CREATED_AT, responseBody.getCreatedAt());
    }

    @Test
    public void testGetPatient_WHEN_PatientDaoFindByIdThrows_THEN_ThrowSameException() {
        NullPointerException toThrow = new NullPointerException();
        Mockito.doThrow(toThrow).when(patientDao).findById(anyString());

        GetPatientRequestBody requestBody = buildGetPatientRequestBody();
        assertThatThrownBy(() -> cut.getPatient(requestBody)).isSameAs(toThrow);
    }

    @ParameterizedTest
    @MethodSource("invalidInputsForGetPatient")
    public void testGetPatient_WHEN_InvalidInput_THEN_ThrowInvalidInputException(GetPatientRequestBody body, String errorMessage) {
        assertInvalidInputExceptionThrown(() -> cut.getPatient(body), errorMessage);
    }

    private static Stream<Arguments> invalidInputsForGetPatient() {
        return Stream.of(
                Arguments.of(null, GET_PATIENT_NULL_ERROR_MESSAGE),
                Arguments.of(buildGetPatientRequestBody(null), PATIENT_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildGetPatientRequestBody(""), PATIENT_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildGetPatientRequestBody(CAREGIVER_ID1), PATIENT_ID_INVALID_ERROR_MESSAGE)
        );
    }

    @Test
    public void testGetAllCaregivers_HappyCase() {
        Caregiver caregiver1 = buildCaregiverDefault();
        Caregiver caregiver2 = buildCaregiverDefault();
        caregiver2.setPid(CAREGIVER_ID2);
        caregiver2.setSid(CAREGIVER_ID2);
        List<Caregiver> caregivers = Arrays.asList(caregiver1, caregiver2);
        when(patientDao.findAllCaregivers(anyString())).thenReturn(caregivers);

        GetAllCaregiversRequestBody requestBody = buildGetAllCaregiversRequestBody();
        GetAllCaregiversResponseBody responseBody = cut.getAllCaregivers(requestBody);

        List<CaregiverSerialization> expected = caregivers.stream().map(CaregiverSerialization::fromCaregiver).collect(Collectors.toList());
        assertThat(responseBody.getCaregivers()).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    public void testGetAllCaregivers_WHEN_PatientDaoFindAllCaregiversThrows_THEN_ThrowSameException() {
        NullPointerException toThrow = new NullPointerException();
        Mockito.doThrow(toThrow).when(patientDao).findAllCaregivers(anyString());

        GetAllCaregiversRequestBody requestBody = buildGetAllCaregiversRequestBody();
        assertThatThrownBy(() -> cut.getAllCaregivers(requestBody)).isSameAs(toThrow);
    }

    @ParameterizedTest
    @MethodSource("invalidInputsForGetAllCaregivers")
    public void testGetAllCaregivers_WHEN_InvalidInput_THEN_ThrowInvalidInputException(GetAllCaregiversRequestBody body, String errorMessage) {
        assertInvalidInputExceptionThrown(() -> cut.getAllCaregivers(body), errorMessage);
    }

    private static Stream<Arguments> invalidInputsForGetAllCaregivers() {
        return Stream.of(
                Arguments.of(null, GET_ALL_CAREGIVERS_NULL_ERROR_MESSAGE),
                Arguments.of(buildGetAllCaregiversRequestBody(null), PATIENT_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildGetAllCaregiversRequestBody(""), PATIENT_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildGetAllCaregiversRequestBody(CAREGIVER_ID1), PATIENT_ID_INVALID_ERROR_MESSAGE)
        );
    }

    @Test
    public void testUpdatePatient_HappyCase() {
        when(patientDao.findById(anyString())).thenReturn(buildPatientDefault());

        UpdatePatientRequestBody requestBody = buildUpdatePatientRequestBody();
        requestBody.setPhoneNumber(PHONE_NUMBER2);
        UpdatePatientResponseBody responseBody = cut.updatePatient(requestBody);

        verify(patientDao, times(1)).update(patientCaptor.capture());
        assertEquals(FIRST_NAME, patientCaptor.getValue().getFirstName());
        assertEquals(LAST_NAME, patientCaptor.getValue().getLastName());
        assertEquals(PHONE_NUMBER2, patientCaptor.getValue().getPhoneNumber());
        assertEquals("OK", responseBody.getMessage());
    }

    @Test
    public void testUpdatePatient_WHEN_PatientDaoFindByIdThrows_THEN_ThrowSameException() {
        NullPointerException toThrow = new NullPointerException();
        Mockito.doThrow(toThrow).when(patientDao).findById(anyString());

        UpdatePatientRequestBody requestBody = buildUpdatePatientRequestBody();
        assertThatThrownBy(() -> cut.updatePatient(requestBody)).isSameAs(toThrow);
    }

    @Test
    public void testUpdatePatient_WHEN_PatientDaoUpdateThrows_THEN_ThrowSameException() {
        when(patientDao.findById(anyString())).thenReturn(buildPatientDefault());

        NullPointerException toThrow = new NullPointerException();
        Mockito.doThrow(toThrow).when(patientDao).update(any(Patient.class));

        UpdatePatientRequestBody requestBody = buildUpdatePatientRequestBody();
        assertThatThrownBy(() -> cut.updatePatient(requestBody)).isSameAs(toThrow);
    }

    @ParameterizedTest
    @MethodSource("invalidInputsForUpdatePatient")
    public void testUpdatePatient_WHEN_InvalidInput_THEN_ThrowInvalidInputException(UpdatePatientRequestBody body, String errorMessage) {
        assertInvalidInputExceptionThrown(() -> cut.updatePatient(body), errorMessage);
    }

    private static Stream<Arguments> invalidInputsForUpdatePatient() {
        return Stream.of(
                Arguments.of(null, UPDATE_PATIENT_NULL_ERROR_MESSAGE),
                Arguments.of(buildUpdatePatientRequestBody(null, FIRST_NAME, LAST_NAME, PHONE_NUMBER1),
                        PATIENT_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildUpdatePatientRequestBody("", FIRST_NAME, LAST_NAME, PHONE_NUMBER1),
                        PATIENT_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildUpdatePatientRequestBody(CAREGIVER_ID1, FIRST_NAME, LAST_NAME, PHONE_NUMBER1),
                        PATIENT_ID_INVALID_ERROR_MESSAGE),
                Arguments.of(buildUpdatePatientRequestBody(PATIENT_ID, null, LAST_NAME, PHONE_NUMBER1),
                        FIRST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildUpdatePatientRequestBody(PATIENT_ID, "", LAST_NAME, PHONE_NUMBER1),
                        FIRST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildUpdatePatientRequestBody(PATIENT_ID, FIRST_NAME, null, PHONE_NUMBER1),
                        LAST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildUpdatePatientRequestBody(PATIENT_ID, FIRST_NAME, "", PHONE_NUMBER1),
                        LAST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildUpdatePatientRequestBody(PATIENT_ID, FIRST_NAME, LAST_NAME, null),
                        PHONE_NUMBER_BLANK_ERROR_MESSAGE),
                Arguments.of(buildUpdatePatientRequestBody(PATIENT_ID, FIRST_NAME, LAST_NAME, ""),
                        PHONE_NUMBER_BLANK_ERROR_MESSAGE)
        );
    }

    @Test
    public void testDeletePatient_HappyCase() {
        DeletePatientRequestBody requestBody = buildDeletePatientRequestBody();
        DeletePatientResponseBody responseBody = cut.deletePatient(requestBody);

        verify(patientDao, times(1)).delete(eq(PATIENT_ID));
        assertEquals("OK", responseBody.getMessage());
    }

    @Test
    public void testDeletePatient_WHEN_PatientDaoDeleteThrows_THEN_ThrowSameException() {
        NullPointerException toThrow = new NullPointerException();
        Mockito.doThrow(toThrow).when(patientDao).delete(anyString());

        DeletePatientRequestBody requestBody = buildDeletePatientRequestBody();
        assertThatThrownBy(() -> cut.deletePatient(requestBody)).isSameAs(toThrow);
    }

    @ParameterizedTest
    @MethodSource("invalidInputsForDeletePatient")
    public void testDeletePatient_WHEN_InvalidInput_THEN_ThrowInvalidInputException(DeletePatientRequestBody body, String errorMessage) {
        assertInvalidInputExceptionThrown(() -> cut.deletePatient(body), errorMessage);
    }

    private static Stream<Arguments> invalidInputsForDeletePatient() {
        return Stream.of(
                Arguments.of(null, DELETE_PATIENT_NULL_ERROR_MESSAGE),
                Arguments.of(buildDeletePatientRequestBody(null), PATIENT_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildDeletePatientRequestBody(""), PATIENT_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildDeletePatientRequestBody(CAREGIVER_ID1), PATIENT_ID_INVALID_ERROR_MESSAGE)
        );
    }

    private static CreatePatientRequestBody buildCreatePatientRequestBody() {
        return buildCreatePatientRequestBody(FIRST_NAME, LAST_NAME, PHONE_NUMBER1);
    }

    private static CreatePatientRequestBody buildCreatePatientRequestBody(String firstName, String lastName, String phoneNumber) {
        return CreatePatientRequestBody.builder()
                .firstName(firstName)
                .lastName(lastName)
                .phoneNumber(phoneNumber)
                .build();
    }

    private static UpdatePatientDeviceRequestBody buildUpdatePatientDeviceRequestBody() {
        return buildUpdatePatientDeviceRequestBody(PATIENT_ID);
    }

    private static UpdatePatientDeviceRequestBody buildUpdatePatientDeviceRequestBody(String patientId) {
        return UpdatePatientDeviceRequestBody.builder()
                .patientId(patientId)
                .build();
    }

    private static VerifyPatientRequestBody buildVerifyPatientRequestBody() {
        return buildVerifyPatientRequestBody(CAREGIVER_ID1, PATIENT_ID, AUTH_CODE, DEVICE_ID);
    }

    private static VerifyPatientRequestBody buildVerifyPatientRequestBody(String caregiverId, String patientId,
                                                                          String authCode, String deviceId) {
        return VerifyPatientRequestBody.builder()
                .caregiverId(caregiverId)
                .patientId(patientId)
                .authCode(authCode)
                .deviceId(deviceId)
                .build();
    }

    private static GetPatientRequestBody buildGetPatientRequestBody() {
        return buildGetPatientRequestBody(PATIENT_ID);
    }

    private static GetPatientRequestBody buildGetPatientRequestBody(String patientId) {
        return GetPatientRequestBody.builder()
                .patientId(patientId)
                .build();
    }

    private static GetAllCaregiversRequestBody buildGetAllCaregiversRequestBody() {
        return buildGetAllCaregiversRequestBody(PATIENT_ID);
    }

    private static GetAllCaregiversRequestBody buildGetAllCaregiversRequestBody(String patientId) {
        return GetAllCaregiversRequestBody.builder()
                .patientId(patientId)
                .build();
    }

    private static UpdatePatientRequestBody buildUpdatePatientRequestBody() {
        return buildUpdatePatientRequestBody(PATIENT_ID, FIRST_NAME, LAST_NAME, PHONE_NUMBER1);
    }

    private static UpdatePatientRequestBody buildUpdatePatientRequestBody(String patientId, String firstName,
                                                                          String lastName, String phoneNumber) {
        return UpdatePatientRequestBody.builder()
                .patientId(patientId)
                .firstName(firstName)
                .lastName(lastName)
                .phoneNumber(phoneNumber)
                .build();
    }

    private static DeletePatientRequestBody buildDeletePatientRequestBody() {
        return buildDeletePatientRequestBody(PATIENT_ID);
    }

    private static DeletePatientRequestBody buildDeletePatientRequestBody(String patientId) {
        return DeletePatientRequestBody.builder()
                .patientId(patientId)
                .build();
    }

    private static Patient buildPatientDefault() {
        Patient patient = buildPatient(PATIENT_ID, PATIENT_ID, null, FIRST_NAME, LAST_NAME, PHONE_NUMBER1,
                AUTH_CODE, AUTH_CODE_TIMESTAMP, false);
        patient.setCreatedAt(CREATED_AT);
        return patient;
    }

    private static Caregiver buildCaregiverDefault() {
        return buildCaregiver(CAREGIVER_ID1, CAREGIVER_ID1, EMAIL, FIRST_NAME, LAST_NAME, TITLE, PHONE_NUMBER1);
    }
}
