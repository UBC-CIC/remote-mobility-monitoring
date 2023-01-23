package com.cpen491.remote_mobility_monitoring.datastore;

import com.cpen491.remote_mobility_monitoring.datastore.dao.AdminDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.CaregiverDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.GenericDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.OrganizationDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.PatientDao;
import com.cpen491.remote_mobility_monitoring.datastore.model.Admin;
import com.cpen491.remote_mobility_monitoring.datastore.model.Caregiver;
import com.cpen491.remote_mobility_monitoring.datastore.model.Organization;
import com.cpen491.remote_mobility_monitoring.datastore.model.Patient;
import org.apache.commons.lang3.tuple.Pair;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.HashMap;
import java.util.Map;

import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.AdminTable;
import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.CaregiverTable;
import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.OrganizationTable;
import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.PatientTable;

public class DaoFactory {
    DynamoDbClient ddbClient;
    DynamoDbEnhancedClient ddbEnhancedClient;

    public DaoFactory() {
        this.ddbClient = createDynamoDbClient();
        this.ddbEnhancedClient = createDynamoDbEnhancedClient(this.ddbClient);
    }

    public DaoFactory(DynamoDbClient ddbClient, DynamoDbEnhancedClient ddbEnhancedClient) {
        this.ddbClient = ddbClient;
        this.ddbEnhancedClient = ddbEnhancedClient;
    }

    public OrganizationDao createOrganizationDao() {
        DynamoDbTable<Organization> table = ddbEnhancedClient
                .table(OrganizationTable.TABLE_NAME, TableSchema.fromBean(Organization.class));
        Map<String, DynamoDbIndex<Organization>> indexMap = new HashMap<>();
        for (Pair<String, String> indexNameAndKey : OrganizationTable.INDEX_NAMES_AND_KEYS) {
            String indexName = indexNameAndKey.getLeft();
            indexMap.put(indexName, table.index(indexName));
        }
        return new OrganizationDao(new GenericDao<>(table, indexMap, ddbEnhancedClient));
    }

    public AdminDao createAdminDao(OrganizationDao organizationDao) {
        DynamoDbTable<Admin> table = ddbEnhancedClient
                .table(AdminTable.TABLE_NAME, TableSchema.fromBean(Admin.class));
        Map<String, DynamoDbIndex<Admin>> indexMap = new HashMap<>();
        for (Pair<String, String> indexNameAndKey : AdminTable.INDEX_NAMES_AND_KEYS) {
            String indexName = indexNameAndKey.getLeft();
            indexMap.put(indexName, table.index(indexName));
        }
        return new AdminDao(new GenericDao<>(table, indexMap, ddbEnhancedClient), organizationDao);
    }

    public CaregiverDao createCaregiverDao(OrganizationDao organizationDao) {
        DynamoDbTable<Caregiver> table = ddbEnhancedClient
                .table(CaregiverTable.TABLE_NAME, TableSchema.fromBean(Caregiver.class));
        Map<String, DynamoDbIndex<Caregiver>> indexMap = new HashMap<>();
        for (Pair<String, String> indexNameAndKey : CaregiverTable.INDEX_NAMES_AND_KEYS) {
            String indexName = indexNameAndKey.getLeft();
            indexMap.put(indexName, table.index(indexName));
        }
        return new CaregiverDao(new GenericDao<>(table, indexMap, ddbEnhancedClient), organizationDao);
    }

    public PatientDao createPatientDao() {
        DynamoDbTable<Patient> table = ddbEnhancedClient
                .table(PatientTable.TABLE_NAME, TableSchema.fromBean(Patient.class));
        Map<String, DynamoDbIndex<Patient>> indexMap = new HashMap<>();
        for (Pair<String, String> indexNameAndKey : PatientTable.INDEX_NAMES_AND_KEYS) {
            String indexName = indexNameAndKey.getLeft();
            indexMap.put(indexName, table.index(indexName));
        }
        return new PatientDao(new GenericDao<>(table, indexMap, ddbEnhancedClient));
    }

    public static DynamoDbClient createDynamoDbClient() {
        return DynamoDbClient.builder()
                // TODO: get region from environment variable
                .region(Region.US_WEST_2)
                .build();
    }

    public static DynamoDbEnhancedClient createDynamoDbEnhancedClient(DynamoDbClient ddbClient) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(ddbClient)
                .build();
    }
}
