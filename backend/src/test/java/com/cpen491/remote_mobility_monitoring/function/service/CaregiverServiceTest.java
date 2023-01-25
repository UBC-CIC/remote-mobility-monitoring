package com.cpen491.remote_mobility_monitoring.function.service;

import com.cpen491.remote_mobility_monitoring.datastore.dao.CaregiverDao;
import com.cpen491.remote_mobility_monitoring.datastore.model.Caregiver;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.CreateCaregiverRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.CreateCaregiverResponseBody;
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
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.CREATE_CAREGIVER_NULL_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.EMAIL_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.FIRST_NAME_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.LAST_NAME_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.ORGANIZATION_ID_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.PHONE_NUMBER_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.TITLE_BLANK_ERROR_MESSAGE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CaregiverServiceTest {
    private static final String EMAIL = "jackjackson@email.com";
    private static final String TITLE = "caregiver";
    private static final String IMAGE_URL = "image.png";
    private static final String ORGANIZATION_ID = "org-id-abc";
    private static final String FIRST_NAME = "Jack";
    private static final String LAST_NAME = "Jackson";
    private static final String PHONE_NUMBER = "1234567890";
    private static final String CAREGIVER_ID = "caregiver-id-1";

    CaregiverService cut;
    @Mock
    CaregiverDao caregiverDao;
    ArgumentCaptor<Caregiver> caregiverCaptor;

    @BeforeEach
    public void setup() {
        caregiverCaptor = ArgumentCaptor.forClass(Caregiver.class);
        cut = new CaregiverService(caregiverDao);
    }

    @Test
    public void testCreateCaregiver_HappyCase() {
        CreateCaregiverRequestBody requestBody = buildCreateCaregiverRequestBody();
        CreateCaregiverResponseBody responseBody = cut.createCaregiver(requestBody);

        verify(caregiverDao, times(1)).create(caregiverCaptor.capture());
        assertEquals(EMAIL, caregiverCaptor.getValue().getEmail());
        assertEquals(FIRST_NAME, caregiverCaptor.getValue().getFirstName());
        assertEquals(LAST_NAME, caregiverCaptor.getValue().getLastName());
        assertEquals(TITLE, caregiverCaptor.getValue().getTitle());
        assertEquals(PHONE_NUMBER, caregiverCaptor.getValue().getPhoneNumber());
        assertEquals(ORGANIZATION_ID, caregiverCaptor.getValue().getOrganizationId());
        assertNotNull(responseBody);
    }

    @Test
    public void testCreateCaregiver_WHEN_CaregiverDaoCreateThrows_THEN_ThrowSameException() {
        NullPointerException toThrow = new NullPointerException();
        Mockito.doThrow(toThrow).when(caregiverDao).create(any(Caregiver.class));

        CreateCaregiverRequestBody requestBody = buildCreateCaregiverRequestBody();
        assertThatThrownBy(() -> cut.createCaregiver(requestBody)).isSameAs(toThrow);
    }

    @ParameterizedTest
    @MethodSource("invalidInputsForCreatePatient")
    public void testCreateCaregiver_WHEN_InvalidInput_THEN_ThrowInvalidInputException(CreateCaregiverRequestBody body, String errorMessage) {
        assertInvalidInputExceptionThrown(() -> cut.createCaregiver(body), errorMessage);
    }

    private static Stream<Arguments> invalidInputsForCreatePatient() {
        return Stream.of(
                Arguments.of(null, CREATE_CAREGIVER_NULL_ERROR_MESSAGE),
                Arguments.of(buildCreateCaregiverRequestBody(null, FIRST_NAME, LAST_NAME, TITLE, PHONE_NUMBER, ORGANIZATION_ID),
                        EMAIL_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreateCaregiverRequestBody("", FIRST_NAME, LAST_NAME, TITLE, PHONE_NUMBER, ORGANIZATION_ID),
                        EMAIL_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreateCaregiverRequestBody(EMAIL_BLANK_ERROR_MESSAGE, null, LAST_NAME, TITLE, PHONE_NUMBER, ORGANIZATION_ID),
                        FIRST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreateCaregiverRequestBody(EMAIL_BLANK_ERROR_MESSAGE, "", LAST_NAME, TITLE, PHONE_NUMBER, ORGANIZATION_ID),
                        FIRST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreateCaregiverRequestBody(EMAIL_BLANK_ERROR_MESSAGE, FIRST_NAME, null, TITLE, PHONE_NUMBER, ORGANIZATION_ID),
                        LAST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreateCaregiverRequestBody(EMAIL_BLANK_ERROR_MESSAGE, FIRST_NAME, "", TITLE, PHONE_NUMBER, ORGANIZATION_ID),
                        LAST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreateCaregiverRequestBody(EMAIL_BLANK_ERROR_MESSAGE, FIRST_NAME, LAST_NAME, null, PHONE_NUMBER, ORGANIZATION_ID),
                        TITLE_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreateCaregiverRequestBody(EMAIL_BLANK_ERROR_MESSAGE, FIRST_NAME, LAST_NAME, "", PHONE_NUMBER, ORGANIZATION_ID),
                        TITLE_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreateCaregiverRequestBody(EMAIL_BLANK_ERROR_MESSAGE, FIRST_NAME, LAST_NAME, TITLE, null, ORGANIZATION_ID),
                        PHONE_NUMBER_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreateCaregiverRequestBody(EMAIL_BLANK_ERROR_MESSAGE, FIRST_NAME, LAST_NAME, TITLE, "", ORGANIZATION_ID),
                        PHONE_NUMBER_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreateCaregiverRequestBody(EMAIL_BLANK_ERROR_MESSAGE, FIRST_NAME, LAST_NAME, TITLE, PHONE_NUMBER, null),
                        ORGANIZATION_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreateCaregiverRequestBody(EMAIL_BLANK_ERROR_MESSAGE, FIRST_NAME, LAST_NAME, TITLE, PHONE_NUMBER, ""),
                        ORGANIZATION_ID_BLANK_ERROR_MESSAGE)
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

    private static Caregiver buildCaregiverDefault() {
        Caregiver caregiver = buildCaregiver(CAREGIVER_ID, EMAIL, FIRST_NAME, LAST_NAME, TITLE, PHONE_NUMBER, IMAGE_URL, ORGANIZATION_ID);
        return caregiver;
    }
}
