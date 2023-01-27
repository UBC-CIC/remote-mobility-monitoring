package com.cpen491.remote_mobility_monitoring.datastore.dao;

import com.cpen491.remote_mobility_monitoring.datastore.model.BaseModel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BatchGetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.BatchWriteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.KeysAndAttributes;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import software.amazon.awssdk.services.dynamodb.model.WriteRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.BaseTable;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.DynamoDbUtils.convertToAttributeValue;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.TimeUtils.getCurrentUtcTimeString;

@AllArgsConstructor
public class GenericDao {
    @NonNull
    DynamoDbClient ddbClient;

    public void create(Map<String, AttributeValue> item) {
        PutItemRequest request = PutItemRequest.builder()
                .item(item)
                .tableName(BaseTable.TABLE_NAME)
                .build();

        ddbClient.putItem(request);
    }

    public void addAssociation(Map<String, AttributeValue> item1, Map<String, AttributeValue> item2) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.putAll(item1);
        item.putAll(item2);
        AttributeValue currentTime = convertToAttributeValue(getCurrentUtcTimeString());
        item.put(BaseTable.PID_NAME, item1.get(BaseTable.PID_NAME));
        item.put(BaseTable.SID_NAME, item2.get(BaseTable.PID_NAME));
        item.put(BaseTable.CREATED_AT_NAME, currentTime);
        item.put(BaseTable.UPDATED_AT_NAME, currentTime);

        PutItemRequest request = PutItemRequest.builder()
                .item(item)
                .tableName(BaseTable.TABLE_NAME)
                .build();

        ddbClient.putItem(request);
    }

    public GetItemResponse findByPartitionKey(String keyVal) {
        return findByPrimaryKey(keyVal, keyVal);
    }

    public GetItemResponse findByPrimaryKey(String pid, String sid) {
        Map<String, AttributeValue> keyMap = new HashMap<>();
        keyMap.put(BaseTable.PID_NAME, convertToAttributeValue(pid));
        keyMap.put(BaseTable.SID_NAME, convertToAttributeValue(sid));

        GetItemRequest request = GetItemRequest.builder()
                .key(keyMap)
                .tableName(BaseTable.TABLE_NAME)
                .build();

        return ddbClient.getItem(request);
    }

    public QueryResponse findAllByPartitionKey(String keyName, String keyVal) {
        return findAllByPartitionKey(keyName, keyVal, null, false);
    }

    public QueryResponse findAllByPartitionKeyOnIndex(String keyName, String keyVal, String indexName) {
        return findAllByPartitionKey(keyName, keyVal, indexName, true);
    }

    private QueryResponse findAllByPartitionKey(String keyName, String keyVal, String indexName, boolean index) {
        String expression = "#pid = :pidValue";
        Map<String, String> attributeNames = new HashMap<>();
        attributeNames.put("#pid", keyName);
        Map<String, AttributeValue> attributeValues = new HashMap<>();
        attributeValues.put(":pidValue", convertToAttributeValue(keyVal));

        return runQuery(expression, attributeNames, attributeValues, indexName, index);
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

        return runQuery(expression, attributeNames, attributeValues, indexName, index);
    }

    private QueryResponse runQuery(String expression, Map<String, String> attributeNames,
                                   Map<String, AttributeValue> attributeValues, String indexName, boolean index) {
        QueryRequest.Builder requestBuilder = QueryRequest.builder()
                .keyConditionExpression(expression)
                .expressionAttributeNames(attributeNames)
                .expressionAttributeValues(attributeValues)
                .tableName(BaseTable.TABLE_NAME);

        QueryRequest request = index ? requestBuilder.indexName(indexName).build() : requestBuilder.build();
        return ddbClient.query(request);
    }

    public List<Map<String, AttributeValue>> batchFindByPartitionKey(List<String> keyValues) {
        List<Map<String, AttributeValue>> keyMaps = new ArrayList<>();
        for (String keyVal : keyValues) {
            Map<String, AttributeValue> map = new HashMap<>();
            AttributeValue key = convertToAttributeValue(keyVal);
            map.put(BaseTable.PID_NAME, key);
            map.put(BaseTable.SID_NAME, key);
            keyMaps.add(map);
        }

        KeysAndAttributes keysAndAttributes = KeysAndAttributes.builder()
                .keys(keyMaps)
                .build();
        Map<String, KeysAndAttributes> requestItems = new HashMap<>();
        requestItems.put(BaseTable.TABLE_NAME, keysAndAttributes);

        BatchGetItemRequest request = BatchGetItemRequest.builder()
                .requestItems(requestItems)
                .build();

        return ddbClient.batchGetItem(request).responses().get(BaseTable.TABLE_NAME);
    }

    public void update(Map<String, AttributeValue> item) {
        String currentTime = getCurrentUtcTimeString();
        item.put(BaseTable.UPDATED_AT_NAME, convertToAttributeValue(currentTime));

        String expression = "attribute_exists(#pid)";
        Map<String, String> attributesNames = new HashMap<>();
        attributesNames.put("#pid", BaseTable.PID_NAME);

        PutItemRequest request = PutItemRequest.builder()
                .conditionExpression(expression)
                .expressionAttributeNames(attributesNames)
                .item(item)
                .tableName(BaseTable.TABLE_NAME)
                .build();

        ddbClient.putItem(request);
    }

    public void delete(String keyVal) {
        List<Map<String, AttributeValue>> keyMaps = new ArrayList<>();

        Map<String, AttributeValue> keyMap = new HashMap<>();
        AttributeValue key = convertToAttributeValue(keyVal);
        keyMap.put(BaseTable.PID_NAME, key);
        keyMap.put(BaseTable.SID_NAME, key);

        List<Map<String, AttributeValue>> pidItems = findAllByPartitionKey(BaseTable.PID_NAME, keyVal).items();
        List<Map<String, AttributeValue>> sidItems = findAllByPartitionKeyOnIndex(BaseTable.SID_NAME, keyVal, BaseTable.SID_INDEX_NAME).items();

        keyMaps.addAll(pidItems);
        keyMaps.addAll(sidItems);
        List<WriteRequest> writeRequests = keyMaps.stream().filter(item -> {
            String pid = item.get(BaseTable.PID_NAME).s();
            String sid = item.get(BaseTable.SID_NAME).s();
            return !pid.equals(sid);
        }).map(item -> {
            Map<String, AttributeValue> newMap = new HashMap<>();
            newMap.put(BaseTable.PID_NAME, item.get(BaseTable.PID_NAME));
            newMap.put(BaseTable.SID_NAME, item.get(BaseTable.SID_NAME));
            DeleteRequest deleteRequest = DeleteRequest.builder().key(newMap).build();
            return WriteRequest.builder().deleteRequest(deleteRequest).build();
        }).collect(Collectors.toCollection(ArrayList::new));
        writeRequests.add(WriteRequest.builder().deleteRequest(DeleteRequest.builder().key(keyMap).build()).build());

        Map<String, List<WriteRequest>> requestItems = new HashMap<>();
        requestItems.put(BaseTable.TABLE_NAME, writeRequests);

        BatchWriteItemRequest request = BatchWriteItemRequest.builder()
                .requestItems(requestItems)
                .build();

        ddbClient.batchWriteItem(request);
    }

    public void setAutoGeneratedAttributes(BaseModel model, String idPrefix) {
        String id = UUID.randomUUID().toString();
        model.setPid(idPrefix + id);
        model.setSid(idPrefix + id);

        String currentTime = getCurrentUtcTimeString();
        model.setCreatedAt(currentTime);
        model.setUpdatedAt(currentTime);
    }
}
