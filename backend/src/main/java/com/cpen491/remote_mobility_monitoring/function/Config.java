package com.cpen491.remote_mobility_monitoring.function;

import com.cpen491.remote_mobility_monitoring.function.module.CognitoModule;
import com.cpen491.remote_mobility_monitoring.function.module.DatastoreModule;
import com.cpen491.remote_mobility_monitoring.function.module.ServiceModule;
import com.cpen491.remote_mobility_monitoring.function.module.UtilityModule;
import com.cpen491.remote_mobility_monitoring.function.service.AdminService;
import com.cpen491.remote_mobility_monitoring.function.service.CaregiverService;
import com.cpen491.remote_mobility_monitoring.function.service.OrganizationService;
import com.cpen491.remote_mobility_monitoring.function.service.PatientService;
import com.google.gson.Gson;
import dagger.Component;
import dagger.Provides;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

import javax.inject.Singleton;

@Singleton
@Component(modules = {
        DatastoreModule.class,
        ServiceModule.class,
        UtilityModule.class,
        CognitoModule.class

})
public interface Config {
    OrganizationService organizationService();
    AdminService adminService();
    CaregiverService caregiverService();
    PatientService patientService();
    Gson gson();

    static Config instance() {
        return DaggerConfig.create();
    }
}
