package com.cpen491.remote_mobility_monitoring.datastore.dao;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;

import java.net.URI;

import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.AdminTable;

public class DaoTestParent {
    private static final String PORT = "8000";
    DynamoDbClient ddbClient;
    DynamoDbEnhancedClient ddbEnhancedClient;

    private void setupDynamoDbClients() {
        ddbClient = DynamoDbClient.builder()
                .region(Region.US_WEST_2)
                .endpointOverride(URI.create("http://localhost:" + PORT))
                .build();

        ddbEnhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(ddbClient)
                .build();
    }

    void setupAdminTable() {
        setupDynamoDbClients();

        CreateTableRequest request = CreateTableRequest.builder()
                .attributeDefinitions(AttributeDefinition.builder()
                        .attributeName(AdminTable.EMAIL_NAME)
                        .attributeType(ScalarAttributeType.S)
                        .build())
                .keySchema(KeySchemaElement.builder()
                        .attributeName(AdminTable.EMAIL_NAME)
                        .keyType(KeyType.HASH)
                        .build())
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .tableName(AdminTable.TABLE_NAME)
                .build();
        ddbClient.createTable(request);

        // Wait until table is created
        DescribeTableRequest tableRequest = DescribeTableRequest.builder()
                .tableName(AdminTable.TABLE_NAME)
                .build();
        DynamoDbWaiter ddbWaiter = ddbClient.waiter();
        ddbWaiter.waitUntilTableExists(tableRequest);
    }

    void teardownAdminTable() {
        DeleteTableRequest request = DeleteTableRequest.builder()
                .tableName(AdminTable.TABLE_NAME)
                .build();
        ddbClient.deleteTable(request);

        // Wait until table is deleted
        DescribeTableRequest tableRequest = DescribeTableRequest.builder()
                .tableName(AdminTable.TABLE_NAME)
                .build();
        DynamoDbWaiter ddbWaiter = ddbClient.waiter();
        ddbWaiter.waitUntilTableNotExists(tableRequest);
    }
}
