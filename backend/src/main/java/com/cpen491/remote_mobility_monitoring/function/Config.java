package com.cpen491.remote_mobility_monitoring.function;

import com.cpen491.remote_mobility_monitoring.function.module.AuthModule;
import com.cpen491.remote_mobility_monitoring.function.module.AwsModule;
import com.cpen491.remote_mobility_monitoring.function.module.DatastoreModule;
import com.cpen491.remote_mobility_monitoring.function.module.EnvironmentModule;
import com.cpen491.remote_mobility_monitoring.function.module.ServiceModule;
import com.cpen491.remote_mobility_monitoring.function.module.UtilityModule;
import com.cpen491.remote_mobility_monitoring.function.service.AdminService;
import com.cpen491.remote_mobility_monitoring.function.service.AuthService;
import com.cpen491.remote_mobility_monitoring.function.service.CaregiverService;
import com.cpen491.remote_mobility_monitoring.function.service.OrganizationService;
import com.cpen491.remote_mobility_monitoring.function.service.PatientService;
import com.google.gson.Gson;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {
        EnvironmentModule.class,
        AwsModule.class,
        DatastoreModule.class,
        AuthModule.class,
        ServiceModule.class,
        UtilityModule.class,
})
public interface Config {
    AuthService authService();
    OrganizationService organizationService();
    AdminService adminService();
    CaregiverService caregiverService();
    PatientService patientService();
    Gson gson();

    static Config instance() {
        return DaggerConfig.create();
    }
}
