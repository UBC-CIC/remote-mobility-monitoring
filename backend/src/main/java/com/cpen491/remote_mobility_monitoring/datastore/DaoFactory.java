package com.cpen491.remote_mobility_monitoring.datastore;

import com.cpen491.remote_mobility_monitoring.datastore.dao.AdminDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.CaregiverDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.GenericDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.OrganizationDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.PatientDao;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;
import java.net.URISyntaxException;

public class DaoFactory {
    DynamoDbClient ddbClient;
    GenericDao genericDao;

    public DaoFactory(String tableName) {
        this(tableName, createDynamoDbClient());
    }

    public DaoFactory(String tableName, DynamoDbClient ddbClient) {
        this.ddbClient = ddbClient;
        this.genericDao = new GenericDao(tableName, ddbClient);
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
        // TODO: retry policy https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/using.html
        SdkHttpClient httpClient = UrlConnectionHttpClient.builder().build();
        try {
            return DynamoDbClient.builder()
                    .httpClient(httpClient)
                    .region(Region.US_WEST_2)
                    .endpointOverride(new URI("https://dynamodb.us-west-2.amazonaws.com"))
                    .overrideConfiguration(ClientOverrideConfiguration.builder().build())
                    .build();
        } catch (URISyntaxException e) {
            // Should never reach here
            throw new RuntimeException(e);
        }
    }
}
