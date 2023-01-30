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

    public CaregiverDao createCaregiverDao(OrganizationDao organizationDao, PatientDao patientDao) {
        return new CaregiverDao(genericDao, organizationDao, patientDao);
    }

    public PatientDao createPatientDao() {
        return new PatientDao(genericDao);
    }

    public static DynamoDbClient createDynamoDbClient() {
        return DynamoDbClient.builder()
                // TODO: get region from environment variable
                // TODO: retry policy https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/using.html
                .region(Region.US_WEST_2)
                .build();
    }
}
