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
import static com.cpen491.remote_mobility_monitoring.dependency.utility.DynamoDbUtils.getFromMap;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.TimeUtils.getCurrentUtcTimeString;

@AllArgsConstructor
public class GenericDao {
    @NonNull
    private String tableName;
    @NonNull
    private DynamoDbClient ddbClient;

    /**
     * Create or overwrites record.
     *
     * @param item The map containing attribute names and values to create or overwrite
     */
    public void put(Map<String, AttributeValue> item) {
        PutItemRequest request = PutItemRequest.builder()
                .item(item)
                .tableName(tableName)
                .build();

        ddbClient.putItem(request);
    }

    /**
     * Associates item1 with item2. Done by creating a record with pid = item1.pid and sid = item2.pid,
     * as well as all the attributes of item1 and item2.
     *
     * @param item1 The map containing attribute names and values of item1
     * @param item2 The map containing attribute names and values of item2
     */
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
                .tableName(tableName)
                .build();

        ddbClient.putItem(request);
    }

    /**
     * Finds record with pid and sid both matching keyVal.
     *
     * @param keyVal The partition key value
     * @return {@link GetItemResponse}
     */
    public GetItemResponse findByPartitionKey(String keyVal) {
        return findByPrimaryKey(keyVal, keyVal);
    }

    /**
     * Finds record with pid and sid matching input pid and sid.
     *
     * @param pid The partition key value
     * @param sid The sort key value
     * @return {@link GetItemResponse}
     */
    public GetItemResponse findByPrimaryKey(String pid, String sid) {
        Map<String, AttributeValue> keyMap = new HashMap<>();
        keyMap.put(BaseTable.PID_NAME, convertToAttributeValue(pid));
        keyMap.put(BaseTable.SID_NAME, convertToAttributeValue(sid));

        GetItemRequest request = GetItemRequest.builder()
                .key(keyMap)
                .tableName(tableName)
                .build();

        return ddbClient.getItem(request);
    }

    /**
     * Finds all records with pid matching keyVal.
     *
     * @param keyVal The partition key value
     * @return {@link QueryResponse}
     */
    public QueryResponse findAllByPartitionKey(String keyVal) {
        return findAllByPartitionKey(BaseTable.PID_NAME, keyVal, null, false);
    }

    /**
     * Finds all records with keyName matching keyVal.
     *
     * @param keyName The GSI key name
     * @param keyVal The GSI partition key value
     * @param indexName The GSI name
     * @return {@link QueryResponse}
     */
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

    /**
     * Finds all records with pid matching input pid and sid starting with sidPrefix
     *
     * @param pid The partition key value
     * @param sidPrefix The sort key prefix
     * @return {@link QueryResponse}
     */
    public QueryResponse findAllAssociations(String pid, String sidPrefix) {
        return findAllAssociations(pid, sidPrefix, null, false);
    }

    /**
     * Finds all records with sid matching input sid and pid starting with pidPrefix.
     * Query will be executed on sid GSI.
     *
     * @param sid The GSI partition key value
     * @param pidPrefix The GSI sort key prefix
     * @return {@link QueryResponse}
     */
    public QueryResponse findAllAssociationsOnSidIndex(String sid, String pidPrefix) {
        return findAllAssociations(sid, pidPrefix, BaseTable.SID_INDEX_NAME, true);
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
                .tableName(tableName);

        QueryRequest request = index ? requestBuilder.indexName(indexName).build() : requestBuilder.build();
        return ddbClient.query(request);
    }

    /**
     * Batch finds all records with pid and sid matching input list of keyValues.
     *
     * @param keyValues The list of partition key values
     * @return {@link List}
     */
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
        requestItems.put(tableName, keysAndAttributes);

        BatchGetItemRequest request = BatchGetItemRequest.builder()
                .requestItems(requestItems)
                .build();

        return ddbClient.batchGetItem(request).responses().get(tableName);
    }

    /**
     * Updates all records with pid or sid matching item.pid.
     *
     * @param item The map containing attribute names and values to overwrite record with
     */
    public void update(Map<String, AttributeValue> item) {
        String currentTime = getCurrentUtcTimeString();
        item.put(BaseTable.UPDATED_AT_NAME, convertToAttributeValue(currentTime));

        List<Map<String, AttributeValue>> keyMaps = findAllRecordsContainingId(getFromMap(item, BaseTable.PID_NAME));

        for (Map<String, AttributeValue> keyMap : keyMaps) {
            keyMap = new HashMap<>(keyMap);
            AttributeValue pid = keyMap.get(BaseTable.PID_NAME);
            AttributeValue sid = keyMap.get(BaseTable.SID_NAME);
            keyMap.putAll(item);
            keyMap.put(BaseTable.PID_NAME, pid);
            keyMap.put(BaseTable.SID_NAME, sid);
            put(keyMap);
        }
    }

    /**
     * Deletes a record with pid and sid matching input pid and sid.
     *
     * @param pid The partition key value
     * @param sid The sort key value
     */
    public void deleteByPrimaryKey(String pid, String sid) {
        Map<String, AttributeValue> keyMap = new HashMap<>();
        keyMap.put(BaseTable.PID_NAME, convertToAttributeValue(pid));
        keyMap.put(BaseTable.SID_NAME, convertToAttributeValue(sid));

        DeleteItemRequest request = DeleteItemRequest.builder()
                .key(keyMap)
                .tableName(tableName)
                .build();

        ddbClient.deleteItem(request);
    }

    /**
     * Deletes all records with pid or sid matching item.pid.
     *
     * @param keyVal The partition key value
     */
    public void delete(String keyVal) {
        List<Map<String, AttributeValue>> keyMaps = findAllRecordsContainingId(keyVal);

        for (Map<String, AttributeValue> keyMap : keyMaps) {
            deleteByPrimaryKey(getFromMap(keyMap, BaseTable.PID_NAME), getFromMap(keyMap, BaseTable.SID_NAME));
        }
    }

    private List<Map<String, AttributeValue>> findAllRecordsContainingId(String keyVal) {
        List<Map<String, AttributeValue>> keyMaps = new ArrayList<>();
        List<Map<String, AttributeValue>> pidItems = findAllByPartitionKey(keyVal).items();
        List<Map<String, AttributeValue>> sidItems = findAllByPartitionKeyOnIndex(BaseTable.SID_NAME, keyVal, BaseTable.SID_INDEX_NAME).items();

        keyMaps.addAll(pidItems);
        keyMaps.addAll(sidItems);
        return keyMaps;
    }

    /**
     * Sets the pid, sid, createdAt, and updatedAt attributes of model.
     *
     * @param model The model to set attributes for
     * @param idPrefix The prefix for pid and sid
     */
    public void setIdAndDate(BaseModel model, String idPrefix) {
        String id = UUID.randomUUID().toString();
        model.setPid(idPrefix + id);
        model.setSid(idPrefix + id);

        setDate(model);
    }

    /**
     * Sets the createdAt and updatedAt attributes of model.
     *
     * @param model The model to set attributes for
     */
    public void setDate(BaseModel model) {
        String currentTime = getCurrentUtcTimeString();
        model.setCreatedAt(currentTime);
        model.setUpdatedAt(currentTime);
    }

    /**
     * Finds and sets the correct id on pid and sid by matching idPrefix.
     *
     * @param model The model to set ids for
     * @param idPrefix The desired prefix for pid and sid
     */
    public void setCorrectId(BaseModel model, String idPrefix) {
        if (model.getPid().startsWith(idPrefix)) {
            model.setSid(model.getPid());
        } else {
            model.setPid(model.getSid());
        }
    }
}
