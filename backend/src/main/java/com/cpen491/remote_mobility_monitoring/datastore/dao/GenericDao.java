package com.cpen491.remote_mobility_monitoring.datastore.dao;

import com.cpen491.remote_mobility_monitoring.datastore.exception.RecordDoesNotExistException;
import com.cpen491.remote_mobility_monitoring.datastore.model.BaseModel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchGetItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchGetResultPageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.ReadBatch;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.BaseTable;

@AllArgsConstructor
public class GenericDao<T extends BaseModel> {
    private static final int INDEX_LIMIT = 50;

    @NonNull
    DynamoDbTable<T> table;
    Map<String, DynamoDbIndex<T>> indexMap;
    DynamoDbEnhancedClient enhancedClient;

    public void create(T newRecord) {
        newRecord.setId(UUID.randomUUID().toString());
        String currentTime = LocalDateTime.now(ZoneOffset.UTC).toString();
        newRecord.setCreatedAt(currentTime);
        newRecord.setUpdatedAt(currentTime);

        table.putItem(newRecord);
    }

    public T findByPartitionKey(String partitionKey) {
        Key key = Key.builder().partitionValue(partitionKey).build();
        return table.getItem(key);
    }

    public T findOneByIndexPartitionKey(String indexName, String indexPartitionKey) {
        Iterator<Page<T>> results = findAllByIndexPartitionKey(indexName, indexPartitionKey);

        if (results.hasNext()) {
            List<T> items = results.next().items();
            if (items.size() == 1) {
                return items.get(0);
            }
        }

        return null;
    }

    public Iterator<Page<T>> findAllByIndexPartitionKey(String indexName, String indexPartitionKey) {
        DynamoDbIndex<T> index = indexMap.get(indexName);

        AttributeValue val = AttributeValue.builder().s(indexPartitionKey).build();
        Key key = Key.builder().partitionValue(val).build();
        QueryConditional queryConditional = QueryConditional.keyEqualTo(key);

        return index.query(QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .limit(INDEX_LIMIT)
                .build()).iterator();
    }

    public List<T> batchFindByPartitionKey(Set<String> partitionKeys, Class<T> itemClass) {
        ReadBatch.Builder<T> readBatchBuilder = ReadBatch.builder(itemClass).mappedTableResource(table);
        for (String partitionKey : partitionKeys) {
            Key key = Key.builder().partitionValue(partitionKey).build();
            readBatchBuilder.addGetItem(key);
        }

        BatchGetItemEnhancedRequest request = BatchGetItemEnhancedRequest.builder()
                .addReadBatch(readBatchBuilder.build())
                .build();
        BatchGetResultPageIterable results = enhancedClient.batchGetItem(request);
        return results.resultsForTable(table).stream().collect(Collectors.toList());
    }

    public void update(T updatedRecord, Class<T> itemClass) {
        String currentTime = LocalDateTime.now().toString();
        updatedRecord.setUpdatedAt(currentTime);

        Expression expression = Expression.builder()
                .expression(String.format("attribute_exists(%s)", BaseTable.ID_NAME))
                .build();

        UpdateItemEnhancedRequest<T> request = UpdateItemEnhancedRequest.builder(itemClass)
                .conditionExpression(expression)
                .item(updatedRecord)
                .build();

        try {
            table.updateItem(request);
        } catch (ConditionalCheckFailedException e) {
            throw new RecordDoesNotExistException(itemClass.getSimpleName(), updatedRecord.getId());
        }
    }

    public void delete(String partitionKey) {
        Key key = Key.builder().partitionValue(partitionKey).build();
        table.deleteItem(key);
    }
}
