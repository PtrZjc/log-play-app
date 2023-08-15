package pl.zajacp;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.zajacp.model.GameRecord;
import pl.zajacp.model.GamesLog;
import pl.zajacp.repository.DynamoDbRepository;
import pl.zajacp.shared.ObjMapper;
import pl.zajacp.test.FakeContext;
import pl.zajacp.test.utils.DynamoDbContainer;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static pl.zajacp.repository.GameLogRepositoryCommons.TIMESTAMP_RANGE_KEY;
import static pl.zajacp.repository.GameLogRepositoryCommons.USER_HASH_KEY;
import static pl.zajacp.test.domain.GameRecordAssertion.assertThat;
import static pl.zajacp.test.domain.GamesLogBuilder.aGamesLog;
import static pl.zajacp.test.utils.DbTableHelper.createTableWithCompositePrimaryKey;
import static pl.zajacp.test.utils.DbTableHelper.deleteTable;
import static pl.zajacp.test.utils.TestData.TIMESTAMP;

public class GetGamesLogHandlerTest {

    private static DynamoDbClient client;
    private static GetGamesLogHandler getGamesLogHandler;
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
        getGamesLogHandler = new GetGamesLogHandler(repository);
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
    public void shouldGetEmptyListWhenNoGamesExist() throws JsonProcessingException {
        //when
        var responseEvent = getGamesLogHandler.handleRequest(new APIGatewayProxyRequestEvent(), new FakeContext());

        //then
        assertEquals(200, responseEvent.getStatusCode());
        assertTrue(readGamesLogFromBody(responseEvent).games().isEmpty());
    }

    @Test
    public void shouldGetGameLog() throws JsonProcessingException {
        //given
        List<GameRecord> games = aGamesLog().withMultipleDefaultGameRecordsStartingWithTimestamp(TIMESTAMP, 30).build().games();

        repository.batchPutItems(games);

        //when
        var responseEvent = getGamesLogHandler.handleRequest(new APIGatewayProxyRequestEvent(), new FakeContext());

        //then
        assertEquals(200, responseEvent.getStatusCode());
        assertEquals(30, readGamesLogFromBody(responseEvent).games().size());
    }

    private static GamesLog readGamesLogFromBody(APIGatewayProxyResponseEvent responseEvent) throws JsonProcessingException {
        return MAPPER.readValue(responseEvent.getBody(), GamesLog.class);
    }
}
