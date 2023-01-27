package com.cpen491.remote_mobility_monitoring.dependency.utility;

import com.cpen491.remote_mobility_monitoring.datastore.model.Const;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

public class DynamoDbUtils {
    public static void putInMap(Map<String, AttributeValue> map, String key, String val) {
        if (val == null || val.isEmpty()) return;
        map.put(key, convertToAttributeValue(val));
    }

    public static void putInMap(Map<String, AttributeValue> map, String key, Boolean val) {
        if (val == null) return;
        map.put(key, convertToAttributeValue(val));
    }

    public static AttributeValue convertToAttributeValue(String s) {
        return AttributeValue.builder().s(s).build();
    }

    public static AttributeValue convertToAttributeValue(Boolean b) {
        return AttributeValue.builder().bool(b).build();
    }

    public static String getFromMap(Map<String, AttributeValue> map, String key) {
        AttributeValue val = map.get(key);
        if (val == null) return null;
        else return val.s();
    }
}
