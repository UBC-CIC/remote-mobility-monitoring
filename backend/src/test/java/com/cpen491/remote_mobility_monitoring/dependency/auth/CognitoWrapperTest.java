package com.cpen491.remote_mobility_monitoring.dependency.auth;

import com.cpen491.remote_mobility_monitoring.dependency.exception.CognitoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserType;

import static com.cpen491.remote_mobility_monitoring.TestUtils.assertInvalidInputExceptionThrown;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.EMAIL_BLANK_ERROR_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CognitoWrapperTest {
    private static final String USERPOOL_ID = "cognito-123";
    private static final String SUB_NAME = "sub";
    private static final String INCORRECT_SUB_NAME = "sup";
    private static final String USER_ID = "1";
    private static final String EMAIL = "user@email.com";

    CognitoWrapper cut;
    @Mock
    CognitoIdentityProviderClient cognitoIdentityProviderClient;

    @BeforeEach
    public void setup() {
        cut = new CognitoWrapper(USERPOOL_ID, cognitoIdentityProviderClient);
    }

    @Test
    public void testCreateUser_HappyCase() {
        AdminCreateUserResponse response = buildAdminCreateUserResponse(SUB_NAME, USER_ID);
        when(cognitoIdentityProviderClient.adminCreateUser(any(AdminCreateUserRequest.class))).thenReturn(response);

        CognitoWrapper.CognitoUser cognitoUser = cut.createUser(EMAIL);
        assertEquals(USER_ID, cognitoUser.getId());
        assertThat(cognitoUser.getPassword()).hasSizeGreaterThan(6);
    }

    @Test
    public void testCreateUser_WHEN_SubDoesNotExist_THEN_ThrowCognitoException() {
        AdminCreateUserResponse response = buildAdminCreateUserResponse(INCORRECT_SUB_NAME, USER_ID);
        when(cognitoIdentityProviderClient.adminCreateUser(any(AdminCreateUserRequest.class))).thenReturn(response);

        assertThatThrownBy(() -> cut.createUser(EMAIL)).isInstanceOf(CognitoException.class);
    }

    @Test
    public void testCreateUser_WHEN_CognitoClientThrowsCognitoIdentityProviderException_THEN_ThrowCognitoException() {
        Mockito.doThrow(CognitoIdentityProviderException.class).when(cognitoIdentityProviderClient).adminCreateUser(any(AdminCreateUserRequest.class));

        assertThatThrownBy(() -> cut.createUser(EMAIL)).isInstanceOf(CognitoException.class);
    }

    @Test
    public void testCreateUser_WHEN_CognitoClientThrowsOtherException_THEN_ThrowSameException() {
        NullPointerException toThrow = new NullPointerException();
        Mockito.doThrow(toThrow).when(cognitoIdentityProviderClient).adminCreateUser(any(AdminCreateUserRequest.class));

        assertThatThrownBy(() -> cut.createUser(EMAIL)).isSameAs(toThrow);
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void testCreateUser_WHEN_InvalidInput_THEN_ThrowInvalidInputException(String email) {
        assertInvalidInputExceptionThrown(() -> cut.createUser(email), EMAIL_BLANK_ERROR_MESSAGE);
    }

    private static AdminCreateUserResponse buildAdminCreateUserResponse(String name, String value) {
        return AdminCreateUserResponse.builder()
                .user(buildUserType(name, value))
                .build();
    }

    private static UserType buildUserType(String name, String value) {
        return UserType.builder()
                .attributes(AttributeType.builder()
                        .name(name)
                        .value(value)
                        .build())
                .build();
    }
}
