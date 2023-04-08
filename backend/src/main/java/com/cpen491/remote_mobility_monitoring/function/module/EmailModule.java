package com.cpen491.remote_mobility_monitoring.function.module;

import com.cpen491.remote_mobility_monitoring.dependency.email.SesWrapper;
import com.cpen491.remote_mobility_monitoring.dependency.email.SesWrapperFactory;
import dagger.Module;
import dagger.Provides;
import software.amazon.awssdk.services.sesv2.SesV2Client;

import javax.inject.Named;
import javax.inject.Singleton;

import static com.cpen491.remote_mobility_monitoring.function.module.EnvironmentModule.SES_SENDER;

@Module
public class EmailModule {
    @Provides
    @Singleton
    public static SesWrapperFactory sesWrapperFactory(@Named(SES_SENDER) String sesSender, SesV2Client sesV2Client) {
        return new SesWrapperFactory(sesSender, sesV2Client);
    }

    @Provides
    @Singleton
    public static SesWrapper sesWrapper(SesWrapperFactory sesWrapperFactory) {
        return sesWrapperFactory.createSesWrapper();
    }
}
