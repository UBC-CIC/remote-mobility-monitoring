package com.cpen491.remote_mobility_monitoring.function.module;

import com.cpen491.remote_mobility_monitoring.dependency.auth.CognitoWrapper;
import com.cpen491.remote_mobility_monitoring.dependency.auth.CognitoWrapperFactory;
import dagger.Module;
import dagger.Provides;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

import javax.inject.Named;
import javax.inject.Singleton;

import static com.cpen491.remote_mobility_monitoring.function.module.EnvironmentModule.COGNITO_USERPOOL_ID;

@Module
public class AuthModule {
    @Provides
    @Singleton
    public static CognitoWrapperFactory cognitoWrapperFactory(@Named(COGNITO_USERPOOL_ID) String userpoolId,
                                                              CognitoIdentityProviderClient cognitoIdentityProviderClient) {
        return new CognitoWrapperFactory(userpoolId, cognitoIdentityProviderClient);
    }

    @Provides
    @Singleton
    public static CognitoWrapper cognitoWrapper(CognitoWrapperFactory cognitoWrapperFactory) {
        return cognitoWrapperFactory.createCognitoWrapper();
    }
}
