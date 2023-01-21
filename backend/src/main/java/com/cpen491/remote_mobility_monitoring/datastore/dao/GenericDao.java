package com.cpen491.remote_mobility_monitoring.datastore.dao;

import com.cpen491.remote_mobility_monitoring.datastore.model.BaseModel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class GenericDao<T extends BaseModel> {
    private static final int INDEX_LIMIT = 5;

    @NonNull
    DynamoDbTable<T> table;

    public void create(T newRecord) {
        newRecord.setId(UUID.randomUUID().toString());
        String currentTime = LocalDateTime.now().toString();
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
        DynamoDbIndex<T> index = table.index(indexName);

        AttributeValue val = AttributeValue.builder().s(indexPartitionKey).build();
        Key key = Key.builder().partitionValue(val).build();
        QueryConditional queryConditional = QueryConditional.keyEqualTo(key);

        return index.query(QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .limit(INDEX_LIMIT)
                .build()).iterator();
    }

    public void delete(String partitionKey) {
        Key key = Key.builder().partitionValue(partitionKey).build();
        table.deleteItem(key);
    }
}
