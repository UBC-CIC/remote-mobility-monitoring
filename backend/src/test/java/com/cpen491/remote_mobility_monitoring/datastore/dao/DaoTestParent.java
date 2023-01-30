package com.cpen491.remote_mobility_monitoring.datastore.dao;

import com.cpen491.remote_mobility_monitoring.datastore.model.Admin;
import com.cpen491.remote_mobility_monitoring.datastore.model.Caregiver;
import com.cpen491.remote_mobility_monitoring.datastore.model.Organization;
import com.cpen491.remote_mobility_monitoring.datastore.model.Patient;
import org.apache.commons.lang3.tuple.Pair;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.GlobalSecondaryIndex;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.Projection;
import software.amazon.awssdk.services.dynamodb.model.ProjectionType;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.AdminTable;
import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.BaseTable;
import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.CaregiverTable;
import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.OrganizationTable;
import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.PatientTable;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.DynamoDbUtils.convertToAttributeValue;

public class DaoTestParent {
    private static final String PORT = "8000";
    DynamoDbClient ddbClient;
    DynamoDbEnhancedClient ddbEnhancedClient;
    GenericDao genericDao;

    private void setupDynamoDbClients() {
        if (ddbClient == null || ddbEnhancedClient == null) {
            ddbClient = DynamoDbClient.builder()
                    .region(Region.US_WEST_2)
                    .endpointOverride(URI.create("http://localhost:" + PORT))
                    .build();

            ddbEnhancedClient = DynamoDbEnhancedClient.builder()
                    .dynamoDbClient(ddbClient)
                    .build();

            genericDao = new GenericDao(ddbClient);
        }
    }

    void setupTable() {
        setupDynamoDbClients();

        List<AttributeDefinition> attributeDefinitions = new ArrayList<>();
        List<KeySchemaElement> keySchemaElements = new ArrayList<>();
        List<KeySchemaElement> sidGsiKeySchemaElements = new ArrayList<>();
        List<GlobalSecondaryIndex> globalSecondaryIndexes = new ArrayList<>();

        attributeDefinitions.add(buildAttributeDefinition(BaseTable.PID_NAME, ScalarAttributeType.S));
        attributeDefinitions.add(buildAttributeDefinition(BaseTable.SID_NAME, ScalarAttributeType.S));
        keySchemaElements.add(buildKeySchemaElement(BaseTable.PID_NAME, KeyType.HASH));
        keySchemaElements.add(buildKeySchemaElement(BaseTable.SID_NAME, KeyType.RANGE));
        sidGsiKeySchemaElements.add(buildKeySchemaElement(BaseTable.SID_NAME, KeyType.HASH));
        sidGsiKeySchemaElements.add(buildKeySchemaElement(BaseTable.PID_NAME, KeyType.RANGE));

        GlobalSecondaryIndex gsi = GlobalSecondaryIndex.builder()
                .keySchema(sidGsiKeySchemaElements)
                .projection(Projection.builder().projectionType(ProjectionType.ALL).build())
                .indexName(BaseTable.SID_INDEX_NAME)
                .build();
        globalSecondaryIndexes.add(gsi);

        List<Pair<String, String>> indexNamesAndKeys = Stream.of(
                OrganizationTable.INDEX_NAMES_AND_KEYS,
                AdminTable.INDEX_NAMES_AND_KEYS,
                CaregiverTable.INDEX_NAMES_AND_KEYS,
                PatientTable.INDEX_NAMES_AND_KEYS).flatMap(Collection::stream).collect(Collectors.toList());

        for (Pair<String, String> indexNameAndKey : indexNamesAndKeys) {
            String indexName = indexNameAndKey.getLeft();
            String key = indexNameAndKey.getRight();

            attributeDefinitions.add(buildAttributeDefinition(key, ScalarAttributeType.S));

            gsi = GlobalSecondaryIndex.builder()
                    .keySchema(buildKeySchemaElement(key, KeyType.HASH))
                    .projection(Projection.builder().projectionType(ProjectionType.ALL).build())
                    .indexName(indexName)
                    .build();
            globalSecondaryIndexes.add(gsi);
        }

        CreateTableRequest request = CreateTableRequest.builder()
                .attributeDefinitions(attributeDefinitions)
                .keySchema(keySchemaElements)
                .globalSecondaryIndexes(globalSecondaryIndexes)
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .tableName(BaseTable.TABLE_NAME)
                .build();
        ddbClient.createTable(request);

        // Wait until table is created
        DescribeTableRequest tableRequest = DescribeTableRequest.builder()
                .tableName(BaseTable.TABLE_NAME)
                .build();
        DynamoDbWaiter ddbWaiter = ddbClient.waiter();
        ddbWaiter.waitUntilTableExists(tableRequest);
    }

    void teardownTable() {
        DeleteTableRequest request = DeleteTableRequest.builder()
                .tableName(BaseTable.TABLE_NAME)
                .build();
        ddbClient.deleteTable(request);

        // Wait until table is deleted
        DescribeTableRequest tableRequest = DescribeTableRequest.builder()
                .tableName(BaseTable.TABLE_NAME)
                .build();
        DynamoDbWaiter ddbWaiter = ddbClient.waiter();
        ddbWaiter.waitUntilTableNotExists(tableRequest);
    }

    private static AttributeDefinition buildAttributeDefinition(String name, ScalarAttributeType type) {
        return AttributeDefinition.builder()
                .attributeName(name)
                .attributeType(type)
                .build();
    }

    private static KeySchemaElement buildKeySchemaElement(String name, KeyType type) {
        return KeySchemaElement.builder()
                .attributeName(name)
                .keyType(type)
                .build();
    }

    void createOrganization(Organization organization) {
        ddbClient.putItem(PutItemRequest.builder()
                .item(Organization.convertToMap(organization))
                .tableName(BaseTable.TABLE_NAME)
                .build());
    }

    void createAdmin(Admin admin) {
        ddbClient.putItem(PutItemRequest.builder()
                .item(Admin.convertToMap(admin))
                .tableName(BaseTable.TABLE_NAME)
                .build());
    }

    void createCaregiver(Caregiver caregiver) {
        ddbClient.putItem(PutItemRequest.builder()
                .item(Caregiver.convertToMap(caregiver))
                .tableName(BaseTable.TABLE_NAME)
                .build());
    }

    void createPatient(Patient patient) {
        ddbClient.putItem(PutItemRequest.builder()
                .item(Patient.convertToMap(patient))
                .tableName(BaseTable.TABLE_NAME)
                .build());
    }

    GetItemResponse findByPrimaryKey(String pk, String sk) {
        Map<String, AttributeValue> keyMap = new HashMap<>();
        keyMap.put(BaseTable.PID_NAME, convertToAttributeValue(pk));
        keyMap.put(BaseTable.SID_NAME, convertToAttributeValue(sk));

        return ddbClient.getItem(GetItemRequest.builder()
                .key(keyMap)
                .tableName(BaseTable.TABLE_NAME)
                .build());
    }

    void putPrimaryKey(String pk, String sk) {
        Map<String, AttributeValue> keyMap = new HashMap<>();
        keyMap.put(BaseTable.PID_NAME, convertToAttributeValue(pk));
        keyMap.put(BaseTable.SID_NAME, convertToAttributeValue(sk));

        ddbClient.putItem(PutItemRequest.builder()
                .item(keyMap)
                .tableName(BaseTable.TABLE_NAME)
                .build());
    }
}
