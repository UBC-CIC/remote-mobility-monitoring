package com.cpen491.remote_mobility_monitoring.function.module;

import com.cpen491.remote_mobility_monitoring.datastore.DaoFactory;
import com.cpen491.remote_mobility_monitoring.datastore.dao.AdminDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.CaregiverDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.OrganizationDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.PatientDao;
import dagger.Module;
import dagger.Provides;

import javax.inject.Named;
import javax.inject.Singleton;

import static com.cpen491.remote_mobility_monitoring.function.module.EnvironmentModule.DYNAMO_DB_TABLE_NAME;

@Module
public class DatastoreModule {
    @Provides
    @Singleton
    public static DaoFactory daoFactory(@Named(DYNAMO_DB_TABLE_NAME) String tableName) {
        // TODO: create own DynamoDbClient?
        return new DaoFactory(tableName);
    }

    @Provides
    @Singleton
    public static OrganizationDao organizationDao(DaoFactory daoFactory) {
        return daoFactory.createOrganizationDao();
    }

    @Provides
    @Singleton
    public static AdminDao adminDao(DaoFactory daoFactory, OrganizationDao organizationDao) {
        return daoFactory.createAdminDao(organizationDao);
    }

    @Provides
    @Singleton
    public static CaregiverDao caregiverDao(DaoFactory daoFactory, OrganizationDao organizationDao, PatientDao patientDao) {
        return daoFactory.createCaregiverDao(organizationDao, patientDao);
    }

    @Provides
    @Singleton
    public static PatientDao patientDao(DaoFactory daoFactory) {
        return daoFactory.createPatientDao();
    }
}
