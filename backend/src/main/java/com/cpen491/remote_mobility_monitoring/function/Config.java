package com.cpen491.remote_mobility_monitoring.function;

import com.cpen491.remote_mobility_monitoring.function.module.DatastoreModule;
import com.cpen491.remote_mobility_monitoring.function.module.ServiceModule;
import com.cpen491.remote_mobility_monitoring.function.module.UtilityModule;
import com.cpen491.remote_mobility_monitoring.function.service.PatientService;
import com.google.gson.Gson;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {
        DatastoreModule.class,
        ServiceModule.class,
        UtilityModule.class
})
public interface Config {
    PatientService patientService();
    Gson gson();

    static Config instance() {
        return DaggerConfig.create();
    }
}
