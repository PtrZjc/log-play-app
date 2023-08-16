package pl.zajacp.repository;

import org.apache.commons.collections4.ListUtils;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.BatchWriteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.WriteRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

//https://github.com/aws/aws-sdk-java-v2/issues/2265 <- storing nested objects in dynamodb
public class DynamoDbRepository<T> {

    public enum QueryOrder {
        ASC, DESC;
    }

    private final DynamoDbClient client;

    private final TableSchema<T> tableSchema;

    private final String tableName;

    public DynamoDbRepository(DynamoDbClient client, String tableName, Class<T> itemType) {
        this.client = client;
        this.tableName = tableName;
        this.tableSchema = TableSchema.fromBean(itemType);
    }

    public Optional<T> getItem(ItemQueryKey itemQueryKey) {
        var getRequest = GetItemRequest.builder()
                .tableName(tableName)
                .key(itemQueryKey.toAttributeMap())
                .build();

        var itemAsMap = client.getItem(getRequest).item();
        return Optional.ofNullable(tableSchema.mapToItem(itemAsMap));
    }

        public List<T> getItems(ItemQueryKey itemQueryKey, QueryOrder order) {
        QueryRequest query = QueryRequest.builder()
                .tableName(tableName)
                .scanIndexForward(order == QueryOrder.DESC)
                .keyConditionExpression(itemQueryKey.toKeyConditionExpression())
                .expressionAttributeValues(itemQueryKey.toExpressionAttributeValue())
                .build();

        return client.query(query).items().stream()
                .map(tableSchema::mapToItem)
                .collect(Collectors.toList());
    }

    public void putItem(T item) {
        var itemToPut = PutItemRequest.builder()
                .tableName(tableName)
                .item(tableSchema.itemToMap(item, true))
                .build();
        client.putItem(itemToPut);
    }

    public void batchPutItems(List<T> items) {
        ListUtils.partition(items, 25)
                .stream()
                .map(this::mapToBatchWriteRequest)
                .forEach(client::batchWriteItem);
    }

    private BatchWriteItemRequest mapToBatchWriteRequest(List<T> recordBatch) {
        var putRequests = recordBatch.stream()
                .map(i -> tableSchema.itemToMap(i, true))
                .map(m -> PutRequest.builder().item(m).build())
                .map(p -> WriteRequest.builder().putRequest(p).build())
                .toList();

        return BatchWriteItemRequest.builder()
                .requestItems(Map.of(tableName, putRequests))
                .build();
    }
}
