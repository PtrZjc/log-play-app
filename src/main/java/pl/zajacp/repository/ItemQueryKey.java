package pl.zajacp.repository;

import lombok.AllArgsConstructor;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PRIVATE)
public class ItemQueryKey {
    private String primaryKeyName;
    private Object primaryKey;
    private String sortKeyName;
    private Object sortKey;

    public Map<String, AttributeValue> toAttributeMap() {
        Map<String, AttributeValue> map = new HashMap<>();
        map.put(primaryKeyName, toAttributeValue(primaryKey));
        if (sortKeyName != null) {
            map.put(sortKeyName, toAttributeValue(sortKey));
        }
        return map;
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
        throw new UnsupportedOperationException("Unexpected value");
    }
}
