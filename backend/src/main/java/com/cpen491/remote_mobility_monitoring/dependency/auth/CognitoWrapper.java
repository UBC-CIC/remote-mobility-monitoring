package com.cpen491.remote_mobility_monitoring.dependency.auth;

import com.cpen491.remote_mobility_monitoring.dependency.exception.CognitoException;
import com.cpen491.remote_mobility_monitoring.dependency.utility.Validator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminSetUserPasswordRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.DeliveryMediumType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserNotFoundException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class CognitoWrapper {
    @Getter
    @AllArgsConstructor
    public static class CognitoUser {
        private String id;
        private String password;
    }

    @Getter
    @AllArgsConstructor
    public static class Group {
        private String name;
    }

    public static final String ADMIN_GROUP_NAME = "Admin";
    public static final String CAREGIVER_GROUP_NAME = "Caregiver";
    public static final String PATIENT_GROUP_NAME = "Patient";

    @NonNull
    private String userpoolId;
    @NonNull
    private CognitoIdentityProviderClient cognitoIdentityProviderClient;

    /**
     * Creates a user in Cognito if user with email does not already exist and adds the user to group.
     *
     * @param email The email of the user
     * @param groupName The Cognito group
     * @return {@link CognitoUser}
     * @throws CognitoException If Cognito get, create, or add user to group fails and throws an exception
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if email or group is empty or invalid
     */
    public CognitoUser createUserIfNotExistAndAddToGroup(String email, String groupName) {
        Validator.validateEmail(email);
        Validator.validateGroupName(groupName);

        CognitoUser user = getUser(email);
        if (user == null) {
            user = createUser(email);
        }
        addUserToGroup(email, groupName);

        return user;
    }

    /**
     * Creates a user in Cognito.
     *
     * @param email The email of the user
     * @return {@link CognitoUser}
     * @throws CognitoException If Cognito create user fails and throws an exception
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if email is empty
     */
    public CognitoUser createUser(String email) {
        log.info("Creating user with email {} in Cognito", email);
        Validator.validateEmail(email);

        String password = "PASS.word" + UUID.randomUUID();
        AdminCreateUserRequest request = AdminCreateUserRequest.builder()
                .desiredDeliveryMediums(DeliveryMediumType.EMAIL)
                .username(email)
                .userAttributes(
                        AttributeType.builder().name("email_verified").value("true").build(),
                        AttributeType.builder().name("email").value(email).build()
                )
                .userPoolId(userpoolId)
                .temporaryPassword(password)
                .build();
        try {
            AdminCreateUserResponse response = cognitoIdentityProviderClient.adminCreateUser(request);
            String id = getId(response.user().attributes());
            return new CognitoUser(id, password);
        } catch (CognitoIdentityProviderException | NoSuchElementException e) {
            log.error("Encountered Cognito error", e);
            throw new CognitoException(e);
        }
    }

    /**
     * Sets the password for a user in Cognito.
     *
     * @param email The email of the user
     * @param password The new password
     * @throws CognitoException If Cognito set password fails and throws an exception
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if email is empty
     */
    public void setPassword(String email, String password) {
        log.info("Setting password for user {} in Cognito", email);
        Validator.validateEmail(email);
        Validator.validatePassword(password);

        AdminSetUserPasswordRequest request = AdminSetUserPasswordRequest.builder()
                .username(email)
                .password(password)
                .permanent(true)
                .userPoolId(userpoolId)
                .build();
        try {
            cognitoIdentityProviderClient.adminSetUserPassword(request);
        } catch (CognitoIdentityProviderException e) {
            log.error("Encountered Cognito error", e);
            throw new CognitoException(e);
        }
    }

    /**
     * Adds a user to a group in Cognito.
     *
     * @param email The email of the user
     * @param groupName The Cognito group
     * @throws CognitoException If Cognito add user to group fails and throws an exception
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if email or group is empty or invalid
     */
    public void addUserToGroup(String email, String groupName) {
        log.info("Adding user with email {} to group {} in Cognito", email, groupName);
        Validator.validateEmail(email);
        Validator.validateGroupName(groupName);

        AdminAddUserToGroupRequest request = AdminAddUserToGroupRequest.builder()
                .username(email)
                .groupName(groupName)
                .userPoolId(userpoolId)
                .build();
        try {
            cognitoIdentityProviderClient.adminAddUserToGroup(request);
        } catch (CognitoIdentityProviderException e) {
            log.error("Encountered Cognito error", e);
            throw new CognitoException(e);
        }
    }

    /**
     * Gets a user from Cognito. Returns null if user does not exist.
     *
     * @param email The email of the user
     * @return {@link CognitoUser}
     * @throws CognitoException If Cognito get user fails and throws an exception
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if email is empty
     */
    public CognitoUser getUser(String email) {
        log.info("Getting user with email {} in Cognito", email);
        Validator.validateEmail(email);

        AdminGetUserRequest request = AdminGetUserRequest.builder()
                .username(email)
                .userPoolId(userpoolId)
                .build();
        try {
            AdminGetUserResponse response = cognitoIdentityProviderClient.adminGetUser(request);
            String id = getId(response.userAttributes());
            return new CognitoUser(id, null);
        } catch (UserNotFoundException e) {
            log.warn("User with email {} does not exist in Cognito", email);
            return null;
        } catch (CognitoIdentityProviderException | NoSuchElementException e) {
            log.error("Encountered Cognito error", e);
            throw new CognitoException(e);
        }
    }

    /**
     * Gets the groups of a user in Cognito.
     *
     * @param email The email of the user
     * @throws CognitoException If Cognito list groups of user fails and throws an exception
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if email is empty
     */
    public List<Group> getGroupsForUser(String email) {
        log.info("Listing groups for user with email {} in Cognito", email);
        Validator.validateEmail(email);

        AdminListGroupsForUserRequest request = AdminListGroupsForUserRequest.builder()
                .username(email)
                .userPoolId(userpoolId)
                .build();
        try {
            AdminListGroupsForUserResponse response = cognitoIdentityProviderClient.adminListGroupsForUser(request);
            return response.groups().stream().map(group -> new Group(group.groupName())).collect(Collectors.toList());
        } catch (CognitoIdentityProviderException e) {
            log.error("Encountered Cognito error", e);
            throw new CognitoException(e);
        }
    }

    /**
     * Removes a user from a group in Cognito.
     *
     * @param email The email of the user
     * @param groupName The Cognito group
     * @throws CognitoException If Cognito remove user from group fails and throws an exception
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if email or group is empty or invalid
     */
    public void removeUserFromGroup(String email, String groupName) {
        log.info("Removing user with email {} from group {} in Cognito", email, groupName);
        Validator.validateEmail(email);
        Validator.validateGroupName(groupName);

        AdminRemoveUserFromGroupRequest request = AdminRemoveUserFromGroupRequest.builder()
                .username(email)
                .groupName(groupName)
                .userPoolId(userpoolId)
                .build();
        try {
            cognitoIdentityProviderClient.adminRemoveUserFromGroup(request);
        } catch (CognitoIdentityProviderException e) {
            log.error("Encountered Cognito error", e);
            throw new CognitoException(e);
        }
    }

    /**
     * Removes a user from a group in Cognito and if user no longer belongs in any groups then delete user.
     *
     * @param email The email of the user
     * @param groupName The Cognito group
     * @throws CognitoException If Cognito remove user from group, list groups, or delete user fails and throws an exception
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if email or group is empty or invalid
     */
    public void removeUserFromGroupAndDeleteUser(String email, String groupName) {
        Validator.validateEmail(email);
        Validator.validateGroupName(groupName);

        removeUserFromGroup(email, groupName);
        if (getGroupsForUser(email).size() == 0) {
            deleteUser(email);
        }
    }

    /**
     * Deletes a user in Cognito.
     *
     * @param email The email of the user
     * @throws CognitoException If Cognito delete user fails and throws an exception
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if email is empty
     */
    public void deleteUser(String email) {
        log.info("Deleting user with email {} in Cognito", email);
        Validator.validateEmail(email);

        AdminDeleteUserRequest request = AdminDeleteUserRequest.builder()
                .username(email)
                .userPoolId(userpoolId)
                .build();
        try {
            cognitoIdentityProviderClient.adminDeleteUser(request);
        } catch (CognitoIdentityProviderException e) {
            log.error("Encountered Cognito error", e);
            throw new CognitoException(e);
        }
    }

    private static String getId(List<AttributeType> attributes) {
        return attributes.stream().filter(attribute -> attribute.name().equals("sub")).findFirst().get().value();
    }
}
