package com.cpen491.remote_mobility_monitoring.function.module;

import com.cpen491.remote_mobility_monitoring.datastore.DaoFactory;
import com.cpen491.remote_mobility_monitoring.datastore.dao.CaregiverDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.OrganizationDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.PatientDao;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class DatastoreModule {
    @Provides
    @Singleton
    public static DaoFactory daoFactory() {
        // TODO: create own DynamoDbClient?
        return new DaoFactory();
    }

    @Provides
    @Singleton
    public static OrganizationDao organizationDao(DaoFactory daoFactory) {
        return daoFactory.createOrganizationDao();
    }

    @Provides
    @Singleton
    public static CaregiverDao caregiverDao(DaoFactory daoFactory, OrganizationDao organizationDao) {
        return daoFactory.createCaregiverDao(organizationDao);
    }

    @Provides
    @Singleton
    public static PatientDao patientDao(DaoFactory daoFactory, CaregiverDao caregiverDao) {
        return daoFactory.createPatientDao(caregiverDao);
    }
}
