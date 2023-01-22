package com.cpen491.remote_mobility_monitoring.datastore.dao;

import org.assertj.core.api.ThrowableAssert;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.GlobalSecondaryIndex;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.Projection;
import software.amazon.awssdk.services.dynamodb.model.ProjectionType;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.AdminTable;
import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.CaregiverTable;
import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.OrganizationTable;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class DaoTestParent {
    private static final String PORT = "8000";
    DynamoDbClient ddbClient;
    DynamoDbEnhancedClient ddbEnhancedClient;

    private void setupDynamoDbClients() {
        if (ddbClient == null || ddbEnhancedClient == null) {
            ddbClient = DynamoDbClient.builder()
                    .region(Region.US_WEST_2)
                    .endpointOverride(URI.create("http://localhost:" + PORT))
                    .build();

            ddbEnhancedClient = DynamoDbEnhancedClient.builder()
                    .dynamoDbClient(ddbClient)
                    .build();
        }
    }

    void setupOrganizationTable() {
        setupDynamoDbClients();
        setupTable(OrganizationTable.TABLE_NAME, OrganizationTable.ID_NAME, OrganizationTable.NAME_INDEX_NAME, OrganizationTable.NAME_NAME);
    }

    void setupAdminTable() {
        setupDynamoDbClients();
        setupTable(AdminTable.TABLE_NAME, AdminTable.ID_NAME, AdminTable.EMAIL_INDEX_NAME, AdminTable.EMAIL_NAME);
    }

    void setupCaregiverTable() {
        setupDynamoDbClients();
        setupTable(CaregiverTable.TABLE_NAME, CaregiverTable.ID_NAME, CaregiverTable.EMAIL_INDEX_NAME, CaregiverTable.EMAIL_NAME);
    }

    private void setupTable(String tableName, String partitionKey, String gsiName, String gsiPartitionKey) {
        List<AttributeDefinition> attributeDefinitions = new ArrayList<>();
        attributeDefinitions.add(AttributeDefinition.builder()
                .attributeName(partitionKey)
                .attributeType(ScalarAttributeType.S)
                .build());
        attributeDefinitions.add(AttributeDefinition.builder()
                .attributeName(gsiPartitionKey)
                .attributeType(ScalarAttributeType.S)
                .build());

        GlobalSecondaryIndex gsi = GlobalSecondaryIndex.builder()
                .keySchema(KeySchemaElement.builder()
                        .attributeName(gsiPartitionKey)
                        .keyType(KeyType.HASH)
                        .build())
                .projection(Projection.builder().projectionType(ProjectionType.ALL).build())
                .indexName(gsiName)
                .build();

        CreateTableRequest request = CreateTableRequest.builder()
                .attributeDefinitions(attributeDefinitions)
                .keySchema(KeySchemaElement.builder()
                        .attributeName(partitionKey)
                        .keyType(KeyType.HASH)
                        .build())
                .globalSecondaryIndexes(gsi)
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .tableName(tableName)
                .build();
        ddbClient.createTable(request);

        // Wait until table is created
        DescribeTableRequest tableRequest = DescribeTableRequest.builder()
                .tableName(tableName)
                .build();
        DynamoDbWaiter ddbWaiter = ddbClient.waiter();
        ddbWaiter.waitUntilTableExists(tableRequest);
    }

    void teardownOrganizationTable() {
        teardownTable(OrganizationTable.TABLE_NAME);
    }

    void teardownAdminTable() {
        teardownTable(AdminTable.TABLE_NAME);
    }

    void teardownCaregiverTable() {
        teardownTable(CaregiverTable.TABLE_NAME);
    }

    private void teardownTable(String tableName) {
        DeleteTableRequest request = DeleteTableRequest.builder()
                .tableName(tableName)
                .build();
        ddbClient.deleteTable(request);

        // Wait until table is deleted
        DescribeTableRequest tableRequest = DescribeTableRequest.builder()
                .tableName(tableName)
                .build();
        DynamoDbWaiter ddbWaiter = ddbClient.waiter();
        ddbWaiter.waitUntilTableNotExists(tableRequest);
    }

    static void assertInvalidInputExceptionThrown(ThrowableAssert.ThrowingCallable shouldRaiseThrowable, String errorMessage) {
        assertThatThrownBy(shouldRaiseThrowable)
                .isInstanceOfAny(IllegalArgumentException.class, NullPointerException.class)
                .hasMessage(errorMessage);
    }
}
