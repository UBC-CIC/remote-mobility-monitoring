package com.cpen491.remote_mobility_monitoring.datastore;

import com.cpen491.remote_mobility_monitoring.datastore.dao.AdminDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.CaregiverDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.GenericDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.OrganizationDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.PatientDao;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class DaoFactory {
    DynamoDbClient ddbClient;
    GenericDao genericDao;

    public DaoFactory() {
        this(createDynamoDbClient());
    }

    public DaoFactory(DynamoDbClient ddbClient) {
        this.ddbClient = ddbClient;
        this.genericDao = new GenericDao(ddbClient);
    }

    public OrganizationDao createOrganizationDao() {
        return new OrganizationDao(genericDao);
    }

    public AdminDao createAdminDao(OrganizationDao organizationDao) {
        return new AdminDao(genericDao, organizationDao);
    }

    public CaregiverDao createCaregiverDao(OrganizationDao organizationDao) {
        return new CaregiverDao(genericDao, organizationDao);
    }

    public PatientDao createPatientDao(CaregiverDao caregiverDao) {
        return new PatientDao(genericDao, caregiverDao);
    }

    public static DynamoDbClient createDynamoDbClient() {
        return DynamoDbClient.builder()
                // TODO: get region from environment variable
                .region(Region.US_WEST_2)
                .build();
    }
}
