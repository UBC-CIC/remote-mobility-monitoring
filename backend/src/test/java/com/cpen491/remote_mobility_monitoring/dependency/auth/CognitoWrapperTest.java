package com.cpen491.remote_mobility_monitoring.dependency.auth;

import com.cpen491.remote_mobility_monitoring.dependency.exception.CognitoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminAddUserToGroupRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminDeleteUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminListGroupsForUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminListGroupsForUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminRemoveUserFromGroupRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GroupType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserNotFoundException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserType;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static com.cpen491.remote_mobility_monitoring.TestUtils.assertInvalidInputExceptionThrown;
import static com.cpen491.remote_mobility_monitoring.dependency.auth.CognitoWrapper.ADMIN_GROUP_NAME;
import static com.cpen491.remote_mobility_monitoring.dependency.auth.CognitoWrapper.CAREGIVER_GROUP_NAME;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.EMAIL_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.GROUP_NAME_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.GROUP_NAME_INVALID_ERROR_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CognitoWrapperTest {
    private static final String USERPOOL_ID = "cognito-123";
    private static final String SUB_NAME = "sub";
    private static final String INCORRECT_SUB_NAME = "sup";
    private static final String USER_ID = "1";
    private static final String EMAIL = "user@email.com";
    private static final String INVALID_GROUP_NAME = "group-name";

    CognitoWrapper cut;
    @Mock
    CognitoIdentityProviderClient cognitoIdentityProviderClient;

    @BeforeEach
    public void setup() {
        cut = new CognitoWrapper(USERPOOL_ID, cognitoIdentityProviderClient);
    }

    @Test
    public void testCreateUserIfNotExistAndAddToGroup_WHEN_UserExists_THEN_DoNotCallCreateUser() {
        AdminGetUserResponse response = buildAdminGetUserResponse(SUB_NAME, USER_ID);
        when(cognitoIdentityProviderClient.adminGetUser(any(AdminGetUserRequest.class))).thenReturn(response);

        CognitoWrapper.CognitoUser cognitoUser = cut.createUserIfNotExistAndAddToGroup(EMAIL, ADMIN_GROUP_NAME);

        verify(cognitoIdentityProviderClient, never()).adminCreateUser(any(AdminCreateUserRequest.class));
        verify(cognitoIdentityProviderClient, times(1)).adminAddUserToGroup(any(AdminAddUserToGroupRequest.class));
        assertEquals(USER_ID, cognitoUser.getId());
        assertNull(cognitoUser.getPassword());
    }

    @Test
    public void testCreateUserIfNotExistAndAddToGroup_WHEN_UserDoesNotExist_THEN_CallCreateUser() {
        Mockito.doThrow(UserNotFoundException.class).when(cognitoIdentityProviderClient).adminGetUser(any(AdminGetUserRequest.class));

        AdminCreateUserResponse response = buildAdminCreateUserResponse(SUB_NAME, USER_ID);
        when(cognitoIdentityProviderClient.adminCreateUser(any(AdminCreateUserRequest.class))).thenReturn(response);

        CognitoWrapper.CognitoUser cognitoUser = cut.createUserIfNotExistAndAddToGroup(EMAIL, ADMIN_GROUP_NAME);

        verify(cognitoIdentityProviderClient, times(1)).adminCreateUser(any(AdminCreateUserRequest.class));
        verify(cognitoIdentityProviderClient, times(1)).adminAddUserToGroup(any(AdminAddUserToGroupRequest.class));
        assertEquals(USER_ID, cognitoUser.getId());
        assertThat(cognitoUser.getPassword()).hasSizeGreaterThan(6);
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

    @Test
    public void testAddUserToGroup_HappyCase() {
        cut.addUserToGroup(EMAIL, ADMIN_GROUP_NAME);

        verify(cognitoIdentityProviderClient, times(1)).adminAddUserToGroup(any(AdminAddUserToGroupRequest.class));
    }

    @Test
    public void testAddUserToGroup_WHEN_CognitoClientThrowsCognitoIdentityProviderException_THEN_ThrowCognitoException() {
        Mockito.doThrow(CognitoIdentityProviderException.class).when(cognitoIdentityProviderClient)
                .adminAddUserToGroup(any(AdminAddUserToGroupRequest.class));

        assertThatThrownBy(() -> cut.addUserToGroup(EMAIL, ADMIN_GROUP_NAME)).isInstanceOf(CognitoException.class);
    }

    @Test
    public void testAddUserToGroup_WHEN_CognitoClientThrowsOtherException_THEN_ThrowSameException() {
        NullPointerException toThrow = new NullPointerException();
        Mockito.doThrow(toThrow).when(cognitoIdentityProviderClient).adminAddUserToGroup(any(AdminAddUserToGroupRequest.class));

        assertThatThrownBy(() -> cut.addUserToGroup(EMAIL, CAREGIVER_GROUP_NAME)).isSameAs(toThrow);
    }

    @ParameterizedTest
    @MethodSource("invalidInputsForAddUserToGroup")
    public void testAddUserToGroup_WHEN_InvalidInput_THEN_ThrowInvalidInputException(String email, String groupName, String errorMessage) {
        assertInvalidInputExceptionThrown(() -> cut.addUserToGroup(email, groupName), errorMessage);
    }

    private static Stream<Arguments> invalidInputsForAddUserToGroup() {
        return Stream.of(
                Arguments.of(null, ADMIN_GROUP_NAME, EMAIL_BLANK_ERROR_MESSAGE),
                Arguments.of("", ADMIN_GROUP_NAME, EMAIL_BLANK_ERROR_MESSAGE),
                Arguments.of(EMAIL, null, GROUP_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(EMAIL, "", GROUP_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(EMAIL, INVALID_GROUP_NAME, GROUP_NAME_INVALID_ERROR_MESSAGE)
        );
    }

    @Test
    public void testGetUser_HappyCase() {
        AdminGetUserResponse response = buildAdminGetUserResponse(SUB_NAME, USER_ID);
        when(cognitoIdentityProviderClient.adminGetUser(any(AdminGetUserRequest.class))).thenReturn(response);

        CognitoWrapper.CognitoUser cognitoUser = cut.getUser(EMAIL);
        assertEquals(USER_ID, cognitoUser.getId());
        assertNull(cognitoUser.getPassword());
    }

    @Test
    public void testGetUser_WHEN_CognitoClientThrowsUserNotFoundException_THEN_ReturnNull() {
        Mockito.doThrow(UserNotFoundException.class).when(cognitoIdentityProviderClient).adminGetUser(any(AdminGetUserRequest.class));

        CognitoWrapper.CognitoUser cognitoUser = cut.getUser(EMAIL);
        assertNull(cognitoUser);
    }

    @Test
    public void testGetUser_WHEN_SubDoesNotExist_THEN_ThrowCognitoException() {
        AdminGetUserResponse response = buildAdminGetUserResponse(INCORRECT_SUB_NAME, USER_ID);
        when(cognitoIdentityProviderClient.adminGetUser(any(AdminGetUserRequest.class))).thenReturn(response);

        assertThatThrownBy(() -> cut.getUser(EMAIL)).isInstanceOf(CognitoException.class);
    }

    @Test
    public void testGetUser_WHEN_CognitoClientThrowsCognitoIdentityProviderException_THEN_ThrowCognitoException() {
        Mockito.doThrow(CognitoIdentityProviderException.class).when(cognitoIdentityProviderClient).adminGetUser(any(AdminGetUserRequest.class));

        assertThatThrownBy(() -> cut.getUser(EMAIL)).isInstanceOf(CognitoException.class);
    }

    @Test
    public void testGetUser_WHEN_CognitoClientThrowsOtherException_THEN_ThrowSameException() {
        NullPointerException toThrow = new NullPointerException();
        Mockito.doThrow(toThrow).when(cognitoIdentityProviderClient).adminGetUser(any(AdminGetUserRequest.class));

        assertThatThrownBy(() -> cut.getUser(EMAIL)).isSameAs(toThrow);
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void testGetUser_WHEN_InvalidInput_THEN_ThrowInvalidInputException(String email) {
        assertInvalidInputExceptionThrown(() -> cut.getUser(email), EMAIL_BLANK_ERROR_MESSAGE);
    }

    @Test
    public void testGetGroupsForUser_HappyCase() {
        AdminListGroupsForUserResponse response = buildAdminListGroupsForUserResponse(Collections.singletonList(buildGroupType()));
        when(cognitoIdentityProviderClient.adminListGroupsForUser(any(AdminListGroupsForUserRequest.class))).thenReturn(response);

        List<CognitoWrapper.Group> groups = cut.getGroupsForUser(EMAIL);
        assertThat(groups).hasSize(1);
    }

    @Test
    public void testGetGroupsForUser_WHEN_CognitoClientThrowsCognitoIdentityProviderException_THEN_ThrowCognitoException() {
        Mockito.doThrow(CognitoIdentityProviderException.class).when(cognitoIdentityProviderClient)
                .adminListGroupsForUser(any(AdminListGroupsForUserRequest.class));

        assertThatThrownBy(() -> cut.getGroupsForUser(EMAIL)).isInstanceOf(CognitoException.class);
    }

    @Test
    public void testGetGroupsForUser_WHEN_CognitoClientThrowsOtherException_THEN_ThrowSameException() {
        NullPointerException toThrow = new NullPointerException();
        Mockito.doThrow(toThrow).when(cognitoIdentityProviderClient).adminListGroupsForUser(any(AdminListGroupsForUserRequest.class));

        assertThatThrownBy(() -> cut.getGroupsForUser(EMAIL)).isSameAs(toThrow);
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void testGetGroupsForUser_WHEN_InvalidInput_THEN_ThrowInvalidInputException(String email) {
        assertInvalidInputExceptionThrown(() -> cut.getGroupsForUser(email), EMAIL_BLANK_ERROR_MESSAGE);
    }

    @Test
    public void testRemoveUserFromGroup_HappyCase() {
        cut.removeUserFromGroup(EMAIL, ADMIN_GROUP_NAME);

        verify(cognitoIdentityProviderClient, times(1)).adminRemoveUserFromGroup(any(AdminRemoveUserFromGroupRequest.class));
    }

    @Test
    public void testRemoveUserFromGroup_WHEN_CognitoClientThrowsCognitoIdentityProviderException_THEN_ThrowCognitoException() {
        Mockito.doThrow(CognitoIdentityProviderException.class).when(cognitoIdentityProviderClient)
                .adminRemoveUserFromGroup(any(AdminRemoveUserFromGroupRequest.class));

        assertThatThrownBy(() -> cut.removeUserFromGroup(EMAIL, ADMIN_GROUP_NAME)).isInstanceOf(CognitoException.class);
    }

    @Test
    public void testRemoveUserFromGroup_WHEN_CognitoClientThrowsOtherException_THEN_ThrowSameException() {
        NullPointerException toThrow = new NullPointerException();
        Mockito.doThrow(toThrow).when(cognitoIdentityProviderClient)
                .adminRemoveUserFromGroup(any(AdminRemoveUserFromGroupRequest.class));

        assertThatThrownBy(() -> cut.removeUserFromGroup(EMAIL, ADMIN_GROUP_NAME)).isSameAs(toThrow);
    }

    @ParameterizedTest
    @MethodSource("invalidInputsForRemoveUserFromGroup")
    public void testRemoveUserFromGroup_WHEN_InvalidInput_THEN_ThrowInvalidInputException(String email, String groupName, String errorMessage) {
        assertInvalidInputExceptionThrown(() -> cut.removeUserFromGroup(email, groupName), errorMessage);
    }

    private static Stream<Arguments> invalidInputsForRemoveUserFromGroup() {
        return Stream.of(
                Arguments.of(null, ADMIN_GROUP_NAME, EMAIL_BLANK_ERROR_MESSAGE),
                Arguments.of("", ADMIN_GROUP_NAME, EMAIL_BLANK_ERROR_MESSAGE),
                Arguments.of(EMAIL, null, GROUP_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(EMAIL, "", GROUP_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(EMAIL, INVALID_GROUP_NAME, GROUP_NAME_INVALID_ERROR_MESSAGE)
        );
    }

    @Test
    public void testRemoveUserFromGroupAndDeleteUser_WHEN_UserNotInGroup_THEN_DeleteUser() {
        AdminListGroupsForUserResponse response = buildAdminListGroupsForUserResponse(Collections.emptyList());
        when(cognitoIdentityProviderClient.adminListGroupsForUser(any(AdminListGroupsForUserRequest.class))).thenReturn(response);

        cut.removeUserFromGroupAndDeleteUser(EMAIL, ADMIN_GROUP_NAME);

        verify(cognitoIdentityProviderClient, times(1)).adminRemoveUserFromGroup(any(AdminRemoveUserFromGroupRequest.class));
        verify(cognitoIdentityProviderClient, times(1)).adminDeleteUser(any(AdminDeleteUserRequest.class));
    }

    @Test
    public void testRemoveUserFromGroupAndDeleteUser_WHEN_UserStillInGroup_THEN_DoNotDeleteUser() {
        AdminListGroupsForUserResponse response = buildAdminListGroupsForUserResponse(Collections.singletonList(buildGroupType()));
        when(cognitoIdentityProviderClient.adminListGroupsForUser(any(AdminListGroupsForUserRequest.class))).thenReturn(response);

        cut.removeUserFromGroupAndDeleteUser(EMAIL, ADMIN_GROUP_NAME);

        verify(cognitoIdentityProviderClient, times(1)).adminRemoveUserFromGroup(any(AdminRemoveUserFromGroupRequest.class));
        verify(cognitoIdentityProviderClient, never()).adminDeleteUser(any(AdminDeleteUserRequest.class));
    }

    @Test
    public void testDeleteUser_HappyCase() {
        cut.deleteUser(EMAIL);

        verify(cognitoIdentityProviderClient, times(1)).adminDeleteUser(any(AdminDeleteUserRequest.class));
    }

    @Test
    public void testDeleteUser_WHEN_CognitoClientThrowsCognitoIdentityProviderException_THEN_ThrowCognitoException() {
        Mockito.doThrow(CognitoIdentityProviderException.class).when(cognitoIdentityProviderClient).adminDeleteUser(any(AdminDeleteUserRequest.class));

        assertThatThrownBy(() -> cut.deleteUser(EMAIL)).isInstanceOf(CognitoException.class);
    }

    @Test
    public void testDeleteUser_WHEN_CognitoClientThrowsOtherException_THEN_ThrowSameException() {
        NullPointerException toThrow = new NullPointerException();
        Mockito.doThrow(toThrow).when(cognitoIdentityProviderClient).adminDeleteUser(any(AdminDeleteUserRequest.class));

        assertThatThrownBy(() -> cut.deleteUser(EMAIL)).isSameAs(toThrow);
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void testDeleteUser_WHEN_InvalidInput_THEN_ThrowInvalidInputException(String email) {
        assertInvalidInputExceptionThrown(() -> cut.deleteUser(email), EMAIL_BLANK_ERROR_MESSAGE);
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

    private static AdminGetUserResponse buildAdminGetUserResponse(String name, String value) {
        return AdminGetUserResponse.builder()
                .userAttributes(AttributeType.builder()
                        .name(name)
                        .value(value)
                        .build())
                .build();
    }

    private static AdminListGroupsForUserResponse buildAdminListGroupsForUserResponse(List<GroupType> groups) {
        return AdminListGroupsForUserResponse.builder()
                .groups(groups)
                .build();
    }

    private static GroupType buildGroupType() {
        return GroupType.builder()
                .groupName(ADMIN_GROUP_NAME)
                .build();
    }
}
