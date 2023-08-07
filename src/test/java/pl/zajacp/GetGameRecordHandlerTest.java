package pl.zajacp;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pl.zajacp.model.GameRecord;
import pl.zajacp.repository.DynamoDbRepository;
import pl.zajacp.rest.model.GetGameRecordRequest;
import pl.zajacp.shared.ObjectMapperSingleton;
import pl.zajacp.test.FakeContext;
import pl.zajacp.test.db.DbTableCreator;
import pl.zajacp.test.db.DynamoDbContainer;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;
import java.util.Map;

import static pl.zajacp.test.TestData.GAME_DATE;
import static pl.zajacp.test.TestData.GAME_DESCRIPTION;
import static pl.zajacp.test.TestData.GAME_NAME;
import static pl.zajacp.test.TestData.GAME_NAME_2;
import static pl.zajacp.test.TestData.TIMESTAMP;
import static pl.zajacp.test.TestData.TIMESTAMP_2;
import static pl.zajacp.test.assertion.GameRecordAssertion.assertThat;
import static pl.zajacp.test.builder.GameRecordBuilder.aGameRecord;
import static pl.zajacp.test.builder.GamesLogBuilder.aGamesLog;

public class GetGameRecordHandlerTest {

    private static DynamoDbClient client;
    private static GetGameRecordHandler getGameRecordHandler;
    private static DynamoDbRepository<GameRecord> repository;

    private static final ObjectMapper MAPPER = ObjectMapperSingleton.getInstance();

    @BeforeAll
    public static void beforeAll() {
        DynamoDbContainer.startContainer();
        client = DynamoDbClient.builder()
//                .endpointOverride(URI.create(DynamoDbContainer.getLocalhostPath()))
                .endpointOverride(URI.create("http://localhost:8000"))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("local", "local")))
                .region(Region.EU_CENTRAL_1).build();
        repository = new DynamoDbRepository<>(client, "games_log", GameRecord.class);
        getGameRecordHandler = new GetGameRecordHandler(repository);
        DbTableCreator.createTableWithCompositePrimaryKey(client, "gameName", "timestamp", "games_log");
    }


    @Test
    public void shouldGetOneGame() throws JsonProcessingException {
        //given
        repository.putItem(aGameRecord()
                .withStandard5PlayersResult()
                .build());

        var requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.withQueryStringParameters(Map.of(
                "gameName", GAME_NAME,
                "timestamp", TIMESTAMP.toString()
        ));

        //when
        var responseEvent = getGameRecordHandler.handleRequest(requestEvent, new FakeContext());
        var gameRecord = MAPPER.readValue(responseEvent.getBody(), GameRecord.class);

        assertThat(gameRecord)
                .hasGameName(GAME_NAME)
                .hasTimestamp(TIMESTAMP)
                .hasGameDate(GAME_DATE)
                .hasGameDescription(GAME_DESCRIPTION)
                .isSolo(false)
                .hasExpected5StandardPlayersResult();
    }
}
