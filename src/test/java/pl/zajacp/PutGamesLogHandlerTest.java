package pl.zajacp;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static pl.zajacp.repository.GameLogRepositoryCommons.getGameRecordKey;
import static pl.zajacp.test.domain.GameRecordAssertion.assertThat;
import static pl.zajacp.test.domain.GameRecordBuilder.aGameRecord;
import static pl.zajacp.test.domain.GamesLogBuilder.aGamesLog;
import static pl.zajacp.test.utils.DbTableHelper.createTableWithCompositePrimaryKey;
import static pl.zajacp.test.utils.DbTableHelper.deleteTable;
import static pl.zajacp.test.utils.TestData.DIFFERENT_DESCRIPTION;
import static pl.zajacp.test.utils.TestData.GAME_DATE;
import static pl.zajacp.test.utils.TestData.GAME_DESCRIPTION;
import static pl.zajacp.test.utils.TestData.GAME_NAME;
import static pl.zajacp.test.utils.TestData.TIMESTAMP;

public class PutGamesLogHandlerTest {

    private static DynamoDbClient client;
    private static PutGamesLogHandler putGamesLogHandler;
    private static DynamoDbRepository<GameRecord> repository;

    private static final ObjectMapper MAPPER = ObjMapper.INSTANCE.get();

    @BeforeAll
    public static void beforeAll() {
        DynamoDbContainer.startContainer();
        client = DynamoDbClient.builder()
//                .endpointOverride(URI.create(DynamoDbContainer.getLocalhostPath()))
                .endpointOverride(URI.create("http://localhost:8000"))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("local", "local")))
                .region(Region.EU_CENTRAL_1).build();
        repository = new DynamoDbRepository<>(client, "games_log", GameRecord.class);
        putGamesLogHandler = new PutGamesLogHandler(repository);
    }

    @BeforeEach
    public void beforeEach() {
        createTableWithCompositePrimaryKey(client, "gameName", "timestamp", "games_log");
    }

    @AfterEach
    public void afterEach() {
        deleteTable(client, "games_log");
    }

    @Test
    public void shouldPutOneGameInEmptyDatabase() throws JsonProcessingException {
        //given
        GamesLog gamesLog = aGamesLog().withGameRecord(
                aGameRecord().withStandard5PlayersResult().build()).build();

        var requestEvent = new APIGatewayProxyRequestEvent().withBody(MAPPER.writeValueAsString(gamesLog));

        //when
        var responseEvent = putGamesLogHandler.handleRequest(requestEvent, new FakeContext());

        //then
        assertEquals(200, responseEvent.getStatusCode());

        var savedGameRecord = repository.getItem(getGameRecordKey(GAME_NAME, TIMESTAMP));

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

        GamesLog gamesLog = aGamesLog().withGameRecord(
                aGameRecord().withStandard5PlayersResult()
                        .withGameDescription(DIFFERENT_DESCRIPTION)
                        .build()).build();

        var requestEvent = new APIGatewayProxyRequestEvent().withBody(MAPPER.writeValueAsString(gamesLog));

        //when
        var responseEvent = putGamesLogHandler.handleRequest(requestEvent, new FakeContext());

        //then
        assertEquals(200, responseEvent.getStatusCode());

        var savedGameRecord = repository.getItem(getGameRecordKey(GAME_NAME, TIMESTAMP));

        assertThat(savedGameRecord)
                .hasGameDescription(DIFFERENT_DESCRIPTION);
    }


    @Test
    public void shouldGet400ForInvalidJsonInput() {
        //given
        String invalidJson = "{\"games\": invalid-json-here }";
        var requestEvent = new APIGatewayProxyRequestEvent().withBody(invalidJson);

        //when
        var responseEvent = putGamesLogHandler.handleRequest(requestEvent, new FakeContext());

        //then
        assertEquals(400, responseEvent.getStatusCode());
        assertTrue(responseEvent.getBody().contains("Invalid JSON input"));
    }

    @Test
    public void shouldGet500ForInternalServerError() throws JsonProcessingException {
        // Let's assume that for any reason, the GamesLog passed validation but failed during the insertion
        GamesLog gamesLog = new GamesLog(List.of(aGameRecord().withGameName(null).build())); // invalid GameRecord for our case
        var requestEvent = new APIGatewayProxyRequestEvent().withBody(MAPPER.writeValueAsString(gamesLog));

        //when
        var responseEvent = putGamesLogHandler.handleRequest(requestEvent, new FakeContext());

        //then
        assertEquals(500, responseEvent.getStatusCode());
        assertTrue(responseEvent.getBody().contains("Internal Server Error"));
    }
}