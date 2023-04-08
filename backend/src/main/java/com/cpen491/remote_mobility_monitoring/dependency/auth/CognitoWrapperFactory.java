package com.cpen491.remote_mobility_monitoring.dependency.auth;

import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

public class CognitoWrapperFactory {
    private final String userpoolId;
    private final CognitoIdentityProviderClient cognitoIdentityProviderClient;

    public CognitoWrapperFactory(String userpoolId, CognitoIdentityProviderClient cognitoIdentityProviderClient) {
        this.userpoolId = userpoolId;
        this.cognitoIdentityProviderClient = cognitoIdentityProviderClient;
    }

    public CognitoWrapper createCognitoWrapper() {
        return new CognitoWrapper(userpoolId, cognitoIdentityProviderClient);
    }
}
