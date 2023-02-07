package com.cpen491.remote_mobility_monitoring.function.module;

import com.cpen491.remote_mobility_monitoring.datastore.dao.AdminDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.CaregiverDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.OrganizationDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.PatientDao;
import com.cpen491.remote_mobility_monitoring.dependency.auth.CognitoWrapper;
import com.cpen491.remote_mobility_monitoring.function.service.AdminService;
import com.cpen491.remote_mobility_monitoring.function.service.CaregiverService;
import com.cpen491.remote_mobility_monitoring.function.service.OrganizationService;
import com.cpen491.remote_mobility_monitoring.function.service.PatientService;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class ServiceModule {
    @Provides
    @Singleton
    public static OrganizationService organizationService(OrganizationDao organizationDao) {
        return new OrganizationService(organizationDao);
    }

    @Provides
    @Singleton
    public static AdminService adminService(AdminDao adminDao, OrganizationDao organizationDao, CognitoWrapper cognitoWrapper) {
        return new AdminService(adminDao, organizationDao, cognitoWrapper);
    }

    @Provides
    @Singleton
    public static CaregiverService caregiverService(CaregiverDao caregiverDao, OrganizationDao organizationDao, CognitoWrapper cognitoWrapper) {
        return new CaregiverService(caregiverDao, organizationDao, cognitoWrapper);
    }

    @Provides
    @Singleton
    public static PatientService patientService(PatientDao patientDao, CaregiverDao caregiverDao) {
        return new PatientService(patientDao, caregiverDao);
    }
}
