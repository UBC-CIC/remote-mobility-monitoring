package com.cpen491.remote_mobility_monitoring.function.service;

import com.cpen491.remote_mobility_monitoring.datastore.dao.CaregiverDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.PatientDao;
import com.cpen491.remote_mobility_monitoring.datastore.exception.InvalidAuthCodeException;
import com.cpen491.remote_mobility_monitoring.datastore.exception.RecordDoesNotExistException;
import com.cpen491.remote_mobility_monitoring.datastore.model.Caregiver;
import com.cpen491.remote_mobility_monitoring.datastore.model.Patient;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.CreatePatientRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.CreatePatientResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.UpdatePatientDeviceRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.UpdatePatientDeviceResponseBody;
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

import java.util.stream.Stream;

import static com.cpen491.remote_mobility_monitoring.TestUtils.assertInvalidInputExceptionThrown;
import static com.cpen491.remote_mobility_monitoring.TestUtils.buildCaregiver;
import static com.cpen491.remote_mobility_monitoring.TestUtils.buildPatient;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.TimeUtils.getCurrentUtcTime;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.TimeUtils.getCurrentUtcTimeString;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.AUTH_CODE_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.CAREGIVER_ID_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.CREATE_PATIENT_NULL_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.DATE_OF_BIRTH_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.DEVICE_ID_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.FIRST_NAME_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.LAST_NAME_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.PATIENT_ID_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.PHONE_NUMBER_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.UPDATE_PATIENT_DEVICE_NULL_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.VERIFY_PATIENT_NULL_ERROR_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {
    private static final String EMAIL = "jackjackson@email.com";
    private static final String TITLE = "caregiver";
    private static final String IMAGE_URL = "image.png";
    private static final String ORGANIZATION_ID = "org-id-abc";
    private static final String FIRST_NAME = "Jack";
    private static final String LAST_NAME = "Jackson";
    private static final String DATE_OF_BIRTH = "2000-12-31";
    private static final String PHONE_NUMBER = "1234567890";
    private static final String PATIENT_ID = "patient-id-1";
    private static final String CAREGIVER_ID = "caregiver-id-1";
    private static final String AUTH_CODE = "auth_code-123";
    private static final String AUTH_CODE_TIMESTAMP = getCurrentUtcTimeString();
    private static final String DEVICE_ID = "device-id-1";

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
        assertEquals(PHONE_NUMBER, patientCaptor.getValue().getPhoneNumber());
        assertEquals(DATE_OF_BIRTH, patientCaptor.getValue().getDateOfBirth());
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
                Arguments.of(buildCreatePatientRequestBody(null, LAST_NAME, PHONE_NUMBER, DATE_OF_BIRTH), FIRST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreatePatientRequestBody("", LAST_NAME, PHONE_NUMBER, DATE_OF_BIRTH), FIRST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreatePatientRequestBody(FIRST_NAME, null, PHONE_NUMBER, DATE_OF_BIRTH), LAST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreatePatientRequestBody(FIRST_NAME, "", PHONE_NUMBER, DATE_OF_BIRTH), LAST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreatePatientRequestBody(FIRST_NAME, LAST_NAME, null, DATE_OF_BIRTH), PHONE_NUMBER_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreatePatientRequestBody(FIRST_NAME, LAST_NAME, "", DATE_OF_BIRTH), PHONE_NUMBER_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreatePatientRequestBody(FIRST_NAME, LAST_NAME, PHONE_NUMBER, null), DATE_OF_BIRTH_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreatePatientRequestBody(FIRST_NAME, LAST_NAME, PHONE_NUMBER, ""), DATE_OF_BIRTH_BLANK_ERROR_MESSAGE)
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
    public void testUpdatePatientDevice_WHEN_PatientDaoFindByIdReturnsNull_THENThrowRecordDoesNotExistException() {
        when(patientDao.findById(anyString())).thenReturn(null);
        UpdatePatientDeviceRequestBody requestBody = buildUpdatePatientDeviceRequestBody();
        assertThatThrownBy(() -> cut.updatePatientDevice(requestBody)).isInstanceOf(RecordDoesNotExistException.class);
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
                Arguments.of(buildUpdatePatientDeviceRequestBody(""), PATIENT_ID_BLANK_ERROR_MESSAGE)
        );
    }

    @Test
    public void testVerifyPatient_HappyCase() {
        when(patientDao.findById(anyString())).thenReturn(buildPatientDefault());
        when(caregiverDao.findById(anyString())).thenReturn(buildCaregiverDefault());

        VerifyPatientRequestBody requestBody = buildVerifyPatientRequestBody();
        VerifyPatientResponseBody responseBody = cut.verifyPatient(requestBody);

        verify(patientDao, times(1)).update(patientCaptor.capture());
        assertTrue(patientCaptor.getValue().getVerified());
        assertEquals(DEVICE_ID, patientCaptor.getValue().getDeviceId());
        assertThat(patientCaptor.getValue().getCaregiverIds()).containsExactly(CAREGIVER_ID);

        verify(caregiverDao, times(1)).update(caregiverCaptor.capture());
        assertThat(caregiverCaptor.getValue().getPatientIds()).containsExactly(PATIENT_ID);

        assertNotNull(responseBody);
        assertEquals("OK", responseBody.getMessage());
    }

    @Test
    public void testVerifyPatient_WHEN_PatientDaoFindByIdReturnsNull_THEN_ThrowRecordDoesNotExistException() {
        when(patientDao.findById(anyString())).thenReturn(null);

        VerifyPatientRequestBody requestBody = buildVerifyPatientRequestBody();
        assertThatThrownBy(() -> cut.verifyPatient(requestBody)).isInstanceOf(RecordDoesNotExistException.class);
    }

    @Test
    public void testVerifyPatient_WHEN_CaregiverDaoFindByIdReturnsNull_THEN_ThrowRecordDoesNotExistException() {
        when(patientDao.findById(anyString())).thenReturn(buildPatientDefault());
        when(caregiverDao.findById(anyString())).thenReturn(null);

        VerifyPatientRequestBody requestBody = buildVerifyPatientRequestBody();
        assertThatThrownBy(() -> cut.verifyPatient(requestBody)).isInstanceOf(RecordDoesNotExistException.class);
    }

    @Test
    public void testVerifyPatient_WHEN_PatientDaoUpdateThrows_THEN_ThrowSameException() {
        when(patientDao.findById(anyString())).thenReturn(buildPatientDefault());
        when(caregiverDao.findById(anyString())).thenReturn(buildCaregiverDefault());

        NullPointerException toThrow = new NullPointerException();
        Mockito.doThrow(toThrow).when(patientDao).update(any(Patient.class));

        VerifyPatientRequestBody requestBody = buildVerifyPatientRequestBody();
        assertThatThrownBy(() -> cut.verifyPatient(requestBody)).isSameAs(toThrow);
    }

    @Test
    public void testVerifyPatient_WHEN_AuthCodesDoNotMatch_THEN_ThrowInvalidAuthCodeException() {
        when(patientDao.findById(anyString())).thenReturn(buildPatientDefault());
        when(caregiverDao.findById(anyString())).thenReturn(buildCaregiverDefault());

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
        when(caregiverDao.findById(anyString())).thenReturn(buildCaregiverDefault());

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
                Arguments.of(buildVerifyPatientRequestBody(CAREGIVER_ID, null, AUTH_CODE, DEVICE_ID), PATIENT_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildVerifyPatientRequestBody(CAREGIVER_ID, "", AUTH_CODE, DEVICE_ID), PATIENT_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildVerifyPatientRequestBody(CAREGIVER_ID, PATIENT_ID, null, DEVICE_ID), AUTH_CODE_BLANK_ERROR_MESSAGE),
                Arguments.of(buildVerifyPatientRequestBody(CAREGIVER_ID, PATIENT_ID, "", DEVICE_ID), AUTH_CODE_BLANK_ERROR_MESSAGE),
                Arguments.of(buildVerifyPatientRequestBody(CAREGIVER_ID, PATIENT_ID, AUTH_CODE, null), DEVICE_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildVerifyPatientRequestBody(CAREGIVER_ID, PATIENT_ID, AUTH_CODE, ""), DEVICE_ID_BLANK_ERROR_MESSAGE)
        );
    }

    private static CreatePatientRequestBody buildCreatePatientRequestBody() {
        return buildCreatePatientRequestBody(FIRST_NAME, LAST_NAME, PHONE_NUMBER, DATE_OF_BIRTH);
    }

    private static CreatePatientRequestBody buildCreatePatientRequestBody(String firstName, String lastName,
                                                                          String phoneNumber, String dateOfBirth) {
        return CreatePatientRequestBody.builder()
                .firstName(firstName)
                .lastName(lastName)
                .phoneNumber(phoneNumber)
                .dateOfBirth(dateOfBirth)
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
        return buildVerifyPatientRequestBody(CAREGIVER_ID, PATIENT_ID, AUTH_CODE, DEVICE_ID);
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

    private static Patient buildPatientDefault() {
        return buildPatient(PATIENT_ID, null, FIRST_NAME, LAST_NAME, DATE_OF_BIRTH, PHONE_NUMBER,
                AUTH_CODE, AUTH_CODE_TIMESTAMP, false);
    }

    private static Caregiver buildCaregiverDefault() {
        return buildCaregiver(CAREGIVER_ID, EMAIL, FIRST_NAME, LAST_NAME, TITLE, PHONE_NUMBER, IMAGE_URL, ORGANIZATION_ID);
    }
}
