package com.cpen491.remote_mobility_monitoring.function.module;

import com.cpen491.remote_mobility_monitoring.datastore.dao.CaregiverDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.PatientDao;
import com.cpen491.remote_mobility_monitoring.function.service.CaregiverService;
import com.cpen491.remote_mobility_monitoring.function.service.PatientService;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class ServiceModule {
    @Provides
    @Singleton
    public static CaregiverService caregiverService(PatientDao patientDao, CaregiverDao caregiverDao) {
        return new CaregiverService(patientDao, caregiverDao);
    }

    @Provides
    @Singleton
    public static PatientService patientService(PatientDao patientDao, CaregiverDao caregiverDao) {
        return new PatientService(patientDao, caregiverDao);
    }
}
