package pl.zajacp.repository;

import lombok.AllArgsConstructor;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PRIVATE)
public class ItemQueryKey {
    private String partitionKeyName;
    private Object partitionKey;
    private String sortKeyName;
    private Object sortKey;

    public Map<String, AttributeValue> toAttributeMap() {
        Map<String, AttributeValue> map = new HashMap<>();
        map.put(partitionKeyName, toAttributeValue(partitionKey));
        if (sortKeyName != null) {
            map.put(sortKeyName, toAttributeValue(sortKey));
        }
        return map;
    }

    public String toKeyConditionExpression() {
        return partitionKeyName + " = :" + partitionKeyName;
    }

    public Map<String, AttributeValue> toExpressionAttributeValue() {
        return Map.of(":" + partitionKeyName, toAttributeValue(partitionKey));
    }

    public static ItemQueryKey of(String primaryKeyName, Object primaryKey) {
        return new ItemQueryKey(primaryKeyName, primaryKey, null, null);
    }

    public static ItemQueryKey of(String primaryKeyName, Object primaryKey, String sortKeyName, Object sortKey) {
        return new ItemQueryKey(primaryKeyName, primaryKey, sortKeyName, sortKey);
    }

    private AttributeValue toAttributeValue(Object attribute) {
        var builder = AttributeValue.builder();
        if (attribute instanceof String) {
            return builder.s(attribute.toString()).build();
        } else if (attribute instanceof Number) {
            return builder.n(attribute.toString()).build();
        }
        //add when needed
        throw new UnsupportedOperationException("Unhandled type of query parameter: " + attribute);
    }
}
