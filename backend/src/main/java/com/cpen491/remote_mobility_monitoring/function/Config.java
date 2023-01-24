package com.cpen491.remote_mobility_monitoring.function;

import com.cpen491.remote_mobility_monitoring.function.module.DatastoreModule;
import com.cpen491.remote_mobility_monitoring.function.module.ServiceModule;
import com.cpen491.remote_mobility_monitoring.function.service.PatientService;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {
        DatastoreModule.class,
        ServiceModule.class
})
public interface Config {
    PatientService patientService();

    static Config instance() {
        return DaggerConfig.create();
    }
}
