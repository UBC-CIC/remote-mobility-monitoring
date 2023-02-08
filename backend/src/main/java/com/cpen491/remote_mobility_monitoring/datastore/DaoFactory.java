package com.cpen491.remote_mobility_monitoring.datastore;

import com.cpen491.remote_mobility_monitoring.datastore.dao.AdminDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.CaregiverDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.GenericDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.OrganizationDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.PatientDao;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class DaoFactory {
    private final GenericDao genericDao;

    public DaoFactory(String tableName, DynamoDbClient ddbClient) {
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
}
