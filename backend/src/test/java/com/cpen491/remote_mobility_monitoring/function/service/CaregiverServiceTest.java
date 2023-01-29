package com.cpen491.remote_mobility_monitoring.function.service;

import com.cpen491.remote_mobility_monitoring.datastore.dao.CaregiverDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.PatientDao;
import com.cpen491.remote_mobility_monitoring.datastore.model.Caregiver;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.CreateCaregiverRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.CreateCaregiverResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.DeleteCaregiverRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.DeleteCaregiverResponseBody;
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
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.CAREGIVER_ID_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.CAREGIVER_ID_INVALID_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.CREATE_CAREGIVER_NULL_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.DELETE_CAREGIVER_NULL_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.EMAIL_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.FIRST_NAME_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.LAST_NAME_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.ORGANIZATION_ID_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.ORGANIZATION_ID_INVALID_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.PHONE_NUMBER_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.TITLE_BLANK_ERROR_MESSAGE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CaregiverServiceTest {
    private static final String CAREGIVER_ID = "car-1";
    private static final String EMAIL = "jackjackson@email.com";
    private static final String TITLE = "caregiver";
    private static final String FIRST_NAME = "Jack";
    private static final String LAST_NAME = "Jackson";
    private static final String PHONE_NUMBER = "1234567890";
    private static final String ORGANIZATION_ID = "org-1";

    CaregiverService cut;
    @Mock
    PatientDao patientDao;
    @Mock
    CaregiverDao caregiverDao;
    ArgumentCaptor<Caregiver> caregiverCaptor;

    @BeforeEach
    public void setup() {
        caregiverCaptor = ArgumentCaptor.forClass(Caregiver.class);
        cut = new CaregiverService(patientDao, caregiverDao);
    }

    @Test
    public void testCreateCaregiver_HappyCase() {
        CreateCaregiverRequestBody requestBody = buildCreateCaregiverRequestBody();
        CreateCaregiverResponseBody responseBody = cut.createCaregiver(requestBody);

        verify(caregiverDao, times(1)).create(caregiverCaptor.capture(), eq(ORGANIZATION_ID));
        assertEquals(EMAIL, caregiverCaptor.getValue().getEmail());
        assertEquals(FIRST_NAME, caregiverCaptor.getValue().getFirstName());
        assertEquals(LAST_NAME, caregiverCaptor.getValue().getLastName());
        assertEquals(TITLE, caregiverCaptor.getValue().getTitle());
        assertEquals(PHONE_NUMBER, caregiverCaptor.getValue().getPhoneNumber());
        assertNotNull(responseBody);
    }

    @Test
    public void testCreateCaregiver_WHEN_CaregiverDaoCreateThrows_THEN_ThrowSameException() {
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
                Arguments.of(buildCreateCaregiverRequestBody(null, FIRST_NAME, LAST_NAME, TITLE, PHONE_NUMBER, ORGANIZATION_ID),
                        EMAIL_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreateCaregiverRequestBody("", FIRST_NAME, LAST_NAME, TITLE, PHONE_NUMBER, ORGANIZATION_ID),
                        EMAIL_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreateCaregiverRequestBody(EMAIL, null, LAST_NAME, TITLE, PHONE_NUMBER, ORGANIZATION_ID),
                        FIRST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreateCaregiverRequestBody(EMAIL, "", LAST_NAME, TITLE, PHONE_NUMBER, ORGANIZATION_ID),
                        FIRST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreateCaregiverRequestBody(EMAIL, FIRST_NAME, null, TITLE, PHONE_NUMBER, ORGANIZATION_ID),
                        LAST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreateCaregiverRequestBody(EMAIL, FIRST_NAME, "", TITLE, PHONE_NUMBER, ORGANIZATION_ID),
                        LAST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreateCaregiverRequestBody(EMAIL, FIRST_NAME, LAST_NAME, null, PHONE_NUMBER, ORGANIZATION_ID),
                        TITLE_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreateCaregiverRequestBody(EMAIL, FIRST_NAME, LAST_NAME, "", PHONE_NUMBER, ORGANIZATION_ID),
                        TITLE_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreateCaregiverRequestBody(EMAIL, FIRST_NAME, LAST_NAME, TITLE, null, ORGANIZATION_ID),
                        PHONE_NUMBER_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreateCaregiverRequestBody(EMAIL, FIRST_NAME, LAST_NAME, TITLE, "", ORGANIZATION_ID),
                        PHONE_NUMBER_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreateCaregiverRequestBody(EMAIL, FIRST_NAME, LAST_NAME, TITLE, PHONE_NUMBER, null),
                        ORGANIZATION_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreateCaregiverRequestBody(EMAIL, FIRST_NAME, LAST_NAME, TITLE, PHONE_NUMBER, ""),
                        ORGANIZATION_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreateCaregiverRequestBody(EMAIL, FIRST_NAME, LAST_NAME, TITLE, PHONE_NUMBER, CAREGIVER_ID),
                        ORGANIZATION_ID_INVALID_ERROR_MESSAGE)
        );
    }

    @Test
    public void testDeleteCaregiver_HappyCase() {
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
        return buildCreateCaregiverRequestBody(EMAIL, FIRST_NAME, LAST_NAME, TITLE, PHONE_NUMBER, ORGANIZATION_ID);
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

    private static DeleteCaregiverRequestBody buildDeleteCaregiverRequestBody() {
        return buildDeleteCaregiverRequestBody(CAREGIVER_ID);
    }

    private static DeleteCaregiverRequestBody buildDeleteCaregiverRequestBody(String caregiverId) {
        return DeleteCaregiverRequestBody.builder()
                .caregiverId(caregiverId)
                .build();
    }

    private static Caregiver buildCaregiverDefault() {
        return buildCaregiver(CAREGIVER_ID, CAREGIVER_ID, EMAIL, FIRST_NAME, LAST_NAME, TITLE, PHONE_NUMBER);
    }
}
