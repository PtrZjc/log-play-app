package pl.zajacp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pl.zajacp.test.DbHelper;
import pl.zajacp.test.DynamoDbContainer;
import pl.zajacp.repository.DynamoDbRepository;
import pl.zajacp.repository.ItemQueryKey;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

public class RepositoryTest {

    private static DynamoDbClient client;

    @BeforeAll
    public static void beforeAll() {
        DynamoDbContainer.startContainer();
        client = DynamoDbClient.builder()
//                .endpointOverride(URI.create("http://localhost:8000"))
                .endpointOverride(URI.create(DynamoDbContainer.getLocalhostPath()))
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .region(Region.EU_CENTRAL_1).build();

        DbHelper.createTable(client, "test", "id");
    }

    @Test
    public void someTestMethod() {
        var repo = new DynamoDbRepository<>(client, "test", Obj.class);

        var item = new Obj("item");
        repo.putItem(item);

        var readItem = repo.getItem(new ItemQueryKey("id", "item", null, null));

        Assertions.assertEquals(item, readItem);
    }
}
