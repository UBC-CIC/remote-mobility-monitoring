package com.cpen491.remote_mobility_monitoring.datastore.dao;

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

        // TODO: add constants for attribute names
        List<AttributeDefinition> attributeDefinitions = new ArrayList<>();
        attributeDefinitions.add(AttributeDefinition.builder()
                .attributeName("id")
                .attributeType(ScalarAttributeType.S)
                .build());
        attributeDefinitions.add(AttributeDefinition.builder()
                .attributeName("email")
                .attributeType(ScalarAttributeType.S)
                .build());

        GlobalSecondaryIndex adminEmailGsi = GlobalSecondaryIndex.builder()
                .keySchema(KeySchemaElement.builder()
                        .attributeName("email")
                        .keyType(KeyType.HASH)
                        .build())
                .indexName("adminEmailGsi")
                .projection(Projection.builder().projectionType(ProjectionType.ALL).build())
                .build();

        CreateTableRequest request = CreateTableRequest.builder()
                .attributeDefinitions(attributeDefinitions)
                .keySchema(KeySchemaElement.builder()
                        .attributeName("id")
                        .keyType(KeyType.HASH)
                        .build())
                .globalSecondaryIndexes(adminEmailGsi)
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .tableName("admin")
                .build();
        ddbClient.createTable(request);

        // Wait until table is created
        DescribeTableRequest tableRequest = DescribeTableRequest.builder()
                .tableName("admin")
                .build();
        DynamoDbWaiter ddbWaiter = ddbClient.waiter();
        ddbWaiter.waitUntilTableExists(tableRequest);
    }

    void teardownAdminTable() {
        DeleteTableRequest request = DeleteTableRequest.builder()
                .tableName("admin")
                .build();
        ddbClient.deleteTable(request);

        // Wait until table is deleted
        DescribeTableRequest tableRequest = DescribeTableRequest.builder()
                .tableName("admin")
                .build();
        DynamoDbWaiter ddbWaiter = ddbClient.waiter();
        ddbWaiter.waitUntilTableNotExists(tableRequest);
    }
}
