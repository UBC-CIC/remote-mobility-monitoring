package com.cpen491.remote_mobility_monitoring.dependency.auth;

import com.cpen491.remote_mobility_monitoring.dependency.exception.CognitoException;
import com.cpen491.remote_mobility_monitoring.dependency.utility.Validator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.DeliveryMediumType;

import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class CognitoWrapper {
    @Getter
    @AllArgsConstructor
    public static class CognitoUser {
        String id;
        String password;
    }

    @NonNull
    private String userpoolId;
    @NonNull
    private CognitoIdentityProviderClient cognitoIdentityProviderClient;

    /**
     * Creates a user in Cognito.
     *
     * @param email The email of the user
     * @throws CognitoException If Cognito create user fails and throws an exception
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if email is empty
     */
    public CognitoUser createUser(String email) {
        log.info("Creating user with email {} in Cognito", email);
        Validator.validateEmail(email);

        // TODO: come up with better way of generating password
        String password = "PASS.word" + UUID.randomUUID();
        AdminCreateUserRequest request = AdminCreateUserRequest.builder()
                .desiredDeliveryMediums(DeliveryMediumType.EMAIL)
//                .messageAction(MessageActionType.SUPPRESS)
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
            String id = response.user().attributes()
                    .stream().filter(attribute -> attribute.name().equals("sub")).findFirst().get().value();
            return new CognitoUser(id, password);
        } catch (CognitoIdentityProviderException | NoSuchElementException e) {
            log.error("Encountered Cognito error", e);
            throw new CognitoException(e);
        }
    }
}
