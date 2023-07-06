package pl.zajacp.repository;

import org.apache.commons.collections4.ListUtils;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.BatchWriteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutRequest;
import software.amazon.awssdk.services.dynamodb.model.WriteRequest;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DynamoDbRepository<T> {

    //https://github.com/aws/aws-sdk-java-v2/issues/2265 <- storing nested objects in dynamodb
    private final DynamoDbClient client;

    private final TableSchema<T> tableSchema;

    private final String tableName;

    public DynamoDbRepository(String tableName, Class<T> itemType) {
        this.client = DynamoDbClient.builder().region(Region.EU_CENTRAL_1).build();
        this.tableName = tableName;
        this.tableSchema = TableSchema.fromBean(itemType);
    }

    public DynamoDbRepository(DynamoDbClient client, String tableName, Class<T> itemType) {
        this.client = client;
        this.tableName = tableName;
        this.tableSchema = TableSchema.fromBean(itemType);
    }

    public T getItem(ItemQueryKey itemQueryKey) {
        var getRequest = GetItemRequest.builder()
                .tableName(tableName)
                .key(itemQueryKey.toAttributeMap())
                .build();

        var itemAsMap = client.getItem(getRequest).item();
        return tableSchema.mapToItem(itemAsMap);
    }

//    public void createTable() {
//        client.createTable(CreateTableRequest.builder()
//                .tableName(tableName)
//                        .keySchema(KeySchemaElement.builder()
//                                .attributeName("Id")
//                                .keyType(KeyType.HASH)
//                                .build())
//                        .attributeDefinitions(AttributeDefinition.builder()
//                                .attributeName(tableSchema.attributeNames())
//                                .build())
//                        .attributeDefinitions(AttributeDefinition.builder()
//                                .attributeName(tableSchema.)
//                                .build())
//                .attributeDefinitions(tableSchema.attributeDefinitions())
//                .build());
//    }

    public String putItem(T gameRecord) {
        var map = tableSchema.itemToMap(gameRecord, true);
        var put = PutItemRequest.builder().tableName(tableName).item(map).build();
        return client.putItem(put).toString();
    }

    public String batchPutItems(List<T> items) {
        var batchWriteResponses = ListUtils.partition(items, 25)
                .stream()
                .map(this::mapToBatchWriteRequest)
                .map(client::batchWriteItem)
                .collect(Collectors.toList());

        return batchWriteResponses.toString();
    }

    private BatchWriteItemRequest mapToBatchWriteRequest(List<T> recordBatch) {
        var putRequests = recordBatch.stream()
                .map(i -> tableSchema.itemToMap(i, true))
                .map(m -> PutRequest.builder().item(m).build())
                .map(p -> WriteRequest.builder().putRequest(p).build())
                .collect(Collectors.toList());

        return BatchWriteItemRequest.builder()
                .requestItems(Collections.singletonMap(tableName, putRequests))
                .build();
    }
}
