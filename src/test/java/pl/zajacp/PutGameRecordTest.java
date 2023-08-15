package pl.zajacp;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.zajacp.model.GameRecord;
import pl.zajacp.repository.DynamoDbRepository;
import pl.zajacp.shared.ObjMapper;
import pl.zajacp.test.FakeContext;
import pl.zajacp.test.utils.DynamoDbContainer;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static pl.zajacp.repository.GameLogRepositoryCommons.GLOBAL_USER;
import static pl.zajacp.repository.GameLogRepositoryCommons.TIMESTAMP_RANGE_KEY;
import static pl.zajacp.repository.GameLogRepositoryCommons.USER_HASH_KEY;
import static pl.zajacp.repository.GameLogRepositoryCommons.getGameRecordKey;
import static pl.zajacp.rest.RestCommons.UNSUPPORTED_JSON_ERROR_MESSAGE;
import static pl.zajacp.test.domain.GameRecordAssertion.assertThat;
import static pl.zajacp.test.domain.GameRecordBuilder.aGameRecord;
import static pl.zajacp.test.utils.DbTableHelper.createTableWithCompositePrimaryKey;
import static pl.zajacp.test.utils.DbTableHelper.deleteTable;
import static pl.zajacp.test.utils.TestData.DIFFERENT_DESCRIPTION;
import static pl.zajacp.test.utils.TestData.GAME_DATE;
import static pl.zajacp.test.utils.TestData.GAME_DESCRIPTION;
import static pl.zajacp.test.utils.TestData.GAME_NAME;
import static pl.zajacp.test.utils.TestData.TIMESTAMP;

public class PutGameRecordTest {

    private static DynamoDbClient client;
    private static PutGameRecordHandler putGameRecordHandler;
    private static DynamoDbRepository<GameRecord> repository;

    private static final ObjectMapper MAPPER = ObjMapper.INSTANCE.get();

    @BeforeAll
    public static void beforeAll() {
        DynamoDbContainer.startContainer();
        client = DynamoDbClient.builder()
                .endpointOverride(URI.create(DynamoDbContainer.getLocalhostPath()))
//                .endpointOverride(URI.create("http://localhost:8000"))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("local", "local")))
                .region(Region.EU_CENTRAL_1).build();
        repository = new DynamoDbRepository<>(client, "games_log", GameRecord.class);
        putGameRecordHandler = new PutGameRecordHandler(repository);
    }

    @BeforeEach
    public void beforeEach() {
        createTableWithCompositePrimaryKey(client, USER_HASH_KEY, TIMESTAMP_RANGE_KEY, "games_log");
    }

    @AfterEach
    public void afterEach() {
        deleteTable(client, "games_log");
    }

    @Test
    public void shouldPutOneGameInEmptyDatabase() throws JsonProcessingException {
        //given
        GameRecord gameRecord = aGameRecord().withStandard5PlayersResult().build();
        var requestEvent = new APIGatewayProxyRequestEvent().withBody(MAPPER.writeValueAsString(gameRecord));

        //when
        var responseEvent = putGameRecordHandler.handleRequest(requestEvent, new FakeContext());

        //then
        assertEquals(204, responseEvent.getStatusCode());

        var savedGameRecord = repository.getItem(getGameRecordKey(TIMESTAMP, GLOBAL_USER));

        assertThat(savedGameRecord)
                .hasGameName(GAME_NAME)
                .hasTimestamp(TIMESTAMP)
                .hasGameDate(GAME_DATE)
                .hasGameDescription(GAME_DESCRIPTION)
                .isSolo(false)
                .hasExpected5StandardPlayersResult();

    }

    @Test
    public void shouldPutAGameOverridingExistingWithTheSameKey() throws JsonProcessingException {
        //given
        repository.putItem(aGameRecord().withStandard5PlayersResult().build());

        GameRecord gameRecord = aGameRecord().withStandard5PlayersResult()
                        .withGameDescription(DIFFERENT_DESCRIPTION)
                        .build();

        var requestEvent = new APIGatewayProxyRequestEvent().withBody(MAPPER.writeValueAsString(gameRecord));

        //when
        var responseEvent = putGameRecordHandler.handleRequest(requestEvent, new FakeContext());

        //then
        assertEquals(204, responseEvent.getStatusCode());

        var savedGameRecord = repository.getItem(getGameRecordKey(TIMESTAMP, GLOBAL_USER));

        assertThat(savedGameRecord)
                .hasGameDescription(DIFFERENT_DESCRIPTION);
    }

    @Test
    public void shouldGet400ForInvalidJsonInput() {
        //given
        String invalidJson = "{\"games\": invalid-json-here }";
        var requestEvent = new APIGatewayProxyRequestEvent().withBody(invalidJson);

        //when
        var responseEvent = putGameRecordHandler.handleRequest(requestEvent, new FakeContext());

        //then
        assertEquals(400, responseEvent.getStatusCode());
        assertTrue(responseEvent.getBody().contains(UNSUPPORTED_JSON_ERROR_MESSAGE));
    }
}