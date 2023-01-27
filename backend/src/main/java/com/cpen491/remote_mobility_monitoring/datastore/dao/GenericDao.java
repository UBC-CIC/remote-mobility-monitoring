package com.cpen491.remote_mobility_monitoring.datastore.dao;

import com.cpen491.remote_mobility_monitoring.datastore.model.BaseModel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BatchGetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.KeysAndAttributes;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.BaseTable;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.DynamoDbUtils.convertToAttributeValue;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.TimeUtils.getCurrentUtcTimeString;

@AllArgsConstructor
public class GenericDao {
    @NonNull
    DynamoDbClient ddbClient;

    public void create(Map<String, AttributeValue> map) {
        PutItemRequest request = PutItemRequest.builder()
                .item(map)
                .tableName(BaseTable.TABLE_NAME)
                .build();

        ddbClient.putItem(request);
    }

    public void addAssociation(Map<String, AttributeValue> map1, Map<String, AttributeValue> map2) {
        Map<String, AttributeValue> map = new HashMap<>();
        map.putAll(map1);
        map.putAll(map2);
        String currentTime = getCurrentUtcTimeString();
        map.put(BaseTable.PID_NAME, map1.get(BaseTable.PID_NAME));
        map.put(BaseTable.SID_NAME, map2.get(BaseTable.PID_NAME));
        map.put(BaseTable.CREATED_AT_NAME, convertToAttributeValue(currentTime));
        map.put(BaseTable.UPDATED_AT_NAME, convertToAttributeValue(currentTime));

        PutItemRequest request = PutItemRequest.builder()
                .item(map)
                .tableName(BaseTable.TABLE_NAME)
                .build();

        ddbClient.putItem(request);
    }

    public GetItemResponse findByPartitionKey(String key) {
        Map<String, AttributeValue> map = new HashMap<>();
        map.put(BaseTable.PID_NAME, convertToAttributeValue(key));
        map.put(BaseTable.SID_NAME, convertToAttributeValue(key));

        GetItemRequest request = GetItemRequest.builder()
                .key(map)
                .tableName(BaseTable.TABLE_NAME)
                .build();

        return ddbClient.getItem(request);
    }

    public QueryResponse findAllByIndexPartitionKey(String indexName, String keyName, String keyVal) {
        String expression = "#pid = :pidValue";
        Map<String, String> attributeNames = new HashMap<>();
        attributeNames.put("#pid", keyName);
        Map<String, AttributeValue> attributeValues = new HashMap<>();
        attributeValues.put(":pidValue", convertToAttributeValue(keyVal));

        QueryRequest request = QueryRequest.builder()
                .keyConditionExpression(expression)
                .expressionAttributeNames(attributeNames)
                .expressionAttributeValues(attributeValues)
                .tableName(BaseTable.TABLE_NAME)
                .indexName(indexName)
                .build();

        return ddbClient.query(request);
    }

    public QueryResponse findAllAssociations(String pid, String sidPrefix) {
        return findAllAssociations(pid, sidPrefix, null, false);
    }

    public QueryResponse findAllAssociationsOnIndex(String pid, String sidPrefix, String indexName) {
        return findAllAssociations(pid, sidPrefix, indexName, true);
    }

    private QueryResponse findAllAssociations(String pid, String sidPrefix, String indexName, boolean index) {
        String expression = "#pid = :pidValue AND begins_with(#sid, :sidValue)";
        Map<String, String> attributeNames = new HashMap<>();
        Map<String, AttributeValue> attributeValues = new HashMap<>();
        if (index) {
            attributeNames.put("#pid", BaseTable.SID_NAME);
            attributeNames.put("#sid", BaseTable.PID_NAME);
        } else {
            attributeNames.put("#pid", BaseTable.PID_NAME);
            attributeNames.put("#sid", BaseTable.SID_NAME);
        }
        attributeValues.put(":pidValue", convertToAttributeValue(pid));
        attributeValues.put(":sidValue", convertToAttributeValue(sidPrefix));

        QueryRequest.Builder requestBuilder = QueryRequest.builder()
                .keyConditionExpression(expression)
                .expressionAttributeNames(attributeNames)
                .expressionAttributeValues(attributeValues)
                .tableName(BaseTable.TABLE_NAME);

        QueryRequest request = index ? requestBuilder.indexName(indexName).build() : requestBuilder.build();
        return ddbClient.query(request);
    }

    public List<Map<String, AttributeValue>> batchFindByPartitionKey(List<String> partitionKeys) {
        List<Map<String, AttributeValue>> mapList = new ArrayList<>();
        for (String key : partitionKeys) {
            Map<String, AttributeValue> map = new HashMap<>();
            map.put(BaseTable.PID_NAME, convertToAttributeValue(key));
            map.put(BaseTable.SID_NAME, convertToAttributeValue(key));
            mapList.add(map);
        }

        KeysAndAttributes keysAndAttributes = KeysAndAttributes.builder()
                .keys(mapList)
                .build();
        Map<String, KeysAndAttributes> requestItems = new HashMap<>();
        requestItems.put(BaseTable.TABLE_NAME, keysAndAttributes);

        BatchGetItemRequest request = BatchGetItemRequest.builder()
                .requestItems(requestItems)
                .build();

        return ddbClient.batchGetItem(request).responses().get(BaseTable.TABLE_NAME);
    }

    public void update(Map<String, AttributeValue> map) {
        String currentTime = getCurrentUtcTimeString();
        map.put(BaseTable.UPDATED_AT_NAME, convertToAttributeValue(currentTime));

        String expression = "attribute_exists(#pid)";
        Map<String, String> attributesNames = new HashMap<>();
        attributesNames.put("#pid", BaseTable.PID_NAME);

        PutItemRequest request = PutItemRequest.builder()
                .conditionExpression(expression)
                .expressionAttributeNames(attributesNames)
                .item(map)
                .tableName(BaseTable.TABLE_NAME)
                .build();

        ddbClient.putItem(request);
    }

    public void delete(String partitionKey) {
        Map<String, AttributeValue> map = new HashMap<>();
        map.put(BaseTable.PID_NAME, convertToAttributeValue(partitionKey));
        map.put(BaseTable.SID_NAME, convertToAttributeValue(partitionKey));

        DeleteItemRequest request = DeleteItemRequest.builder()
                .key(map)
                .tableName(BaseTable.TABLE_NAME)
                .build();

        ddbClient.deleteItem(request);
    }

    public static void setId(BaseModel model, String idPrefix) {
        String id = UUID.randomUUID().toString();
        model.setPid(idPrefix + id);
        model.setSid(idPrefix + id);

        String currentTime = getCurrentUtcTimeString();
        model.setCreatedAt(currentTime);
        model.setUpdatedAt(currentTime);
    }
}
