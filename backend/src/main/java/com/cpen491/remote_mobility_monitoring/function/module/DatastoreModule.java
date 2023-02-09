package com.cpen491.remote_mobility_monitoring.function.module;

import com.cpen491.remote_mobility_monitoring.datastore.DaoFactory;
import com.cpen491.remote_mobility_monitoring.datastore.MetricsDaoFactory;
import com.cpen491.remote_mobility_monitoring.datastore.dao.AdminDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.CaregiverDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.MetricsDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.OrganizationDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.PatientDao;
import dagger.Module;
import dagger.Provides;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.timestreamquery.TimestreamQueryClient;
import software.amazon.awssdk.services.timestreamwrite.TimestreamWriteClient;

import javax.inject.Named;
import javax.inject.Singleton;

import static com.cpen491.remote_mobility_monitoring.function.module.EnvironmentModule.DYNAMO_DB_TABLE_NAME;
import static com.cpen491.remote_mobility_monitoring.function.module.EnvironmentModule.TIMESTREAM_DATABASE_NAME;
import static com.cpen491.remote_mobility_monitoring.function.module.EnvironmentModule.TIMESTREAM_TABLE_NAME;

@Module
public class DatastoreModule {
    // Prevent lazy loading DynamoDbClient by using static initializer
    // This creates DynamoDbClient and significantly reduces cold start time
    static {
        try {
            DynamoDbClient ddbClient = AwsModule.dynamoDbClient(AwsModule.httpClient());
            DaoFactory daoFactory = daoFactory(EnvironmentModule.dynamoDbTableName(), ddbClient);
            OrganizationDao organizationDao = organizationDao(daoFactory);
            PatientDao patientDao = patientDao(daoFactory);
            CaregiverDao caregiverDao = caregiverDao(daoFactory, organizationDao, patientDao);
            caregiverDao.hasPatient("pat-123", "car-123");
        } catch (Exception e) {
            // Expects exception
        }
    }

    @Provides
    @Singleton
    public static DaoFactory daoFactory(@Named(DYNAMO_DB_TABLE_NAME) String tableName, DynamoDbClient ddbClient) {
        return new DaoFactory(tableName, ddbClient);
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

    @Provides
    @Singleton
    public static MetricsDaoFactory metricsDaoFactory(@Named(TIMESTREAM_DATABASE_NAME) String databaseName,
                                                      @Named(TIMESTREAM_TABLE_NAME) String tableName,
                                                      TimestreamWriteClient writeClient,
                                                      TimestreamQueryClient queryClient) {
        return new MetricsDaoFactory(databaseName, tableName, writeClient, queryClient);
    }

    @Provides
    @Singleton
    public static MetricsDao metricsDao(MetricsDaoFactory metricsDaoFactory) {
        return metricsDaoFactory.createMetricsDao();
    }
}
