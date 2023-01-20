package com.cpen491.remote_mobility_monitoring.datastore.dao;

import com.cpen491.remote_mobility_monitoring.datastore.model.Admin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

// TODO: finish the test
class AdminDaoTest extends DaoTestParent {
    DynamoDbTable<Admin> table;
    AdminDao cut;

    @BeforeEach
    public void setup() {
        setupAdminTable();
        table = ddbEnhancedClient.table("admin", TableSchema.fromBean(Admin.class));
        cut = new AdminDao(table);
    }

    @AfterEach
    public void teardown() {
        teardownAdminTable();
    }

    @Test
    public void testCreate_HappyCase() {
        Admin newRecord = buildAdmin();
        cut.create(newRecord);
        System.out.println(newRecord);
    }

    @Test
    public void testFind_HappyCase() {
        Admin newRecord = buildAdmin();
        table.putItem(newRecord);

        Admin record = cut.findById("123");
        System.out.println(record);
    }

    private static Admin buildAdmin() {
        return Admin.builder()
                .id("123")
                .email("user@email.com")
                .firstName("John")
                .lastName("Smith")
                .organizationId("a")
                .createdAt("123")
                .updatedAt("123")
                .build();
    }
}