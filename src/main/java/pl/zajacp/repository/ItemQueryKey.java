package pl.zajacp.repository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class ItemQueryKey {
    @NonNull
    private String primaryKeyName;
    @NonNull
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

    private AttributeValue toAttributeValue(Object attribute) {
        var builder = AttributeValue.builder();
        if (attribute instanceof String) {
            return builder.s(attribute.toString()).build();
        } else if (attribute instanceof Long) {
            return builder.n(attribute.toString()).build();
        }
        //add when needed
        throw new UnsupportedOperationException("Unexpected value");
    }
}
