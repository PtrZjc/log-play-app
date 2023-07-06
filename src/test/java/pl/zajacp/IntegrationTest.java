package pl.zajacp;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import pl.zajacp.model.GameRecord;
import pl.zajacp.model.GamesLog;
import pl.zajacp.repository.DynamoDbRepository;
import pl.zajacp.repository.ItemQueryKey;
import pl.zajacp.rest.model.GetGameRecordRequest;
import pl.zajacp.test.FakeContext;
import pl.zajacp.test.assertion.GameRecordAssertion;
import pl.zajacp.test.builder.GameRecordBuilder;
import pl.zajacp.test.builder.GamesLogBuilder;
import pl.zajacp.test.builder.PlayerResultBuilder;
import pl.zajacp.test.db.DbTableCreator;
import pl.zajacp.test.db.DynamoDbContainer;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

import static pl.zajacp.test.TestData.*;
import static pl.zajacp.test.assertion.GameRecordAssertion.*;
import static pl.zajacp.test.builder.GameRecordBuilder.*;
import static pl.zajacp.test.builder.GamesLogBuilder.*;
import static pl.zajacp.test.builder.PlayerResultBuilder.*;

public class IntegrationTest {

    private static DynamoDbClient client;
    private static PutGamesLogHandler putGamesLogHandler;
    private static DynamoDbRepository<GameRecord> repository;

    @BeforeAll
    public static void beforeAll() {
        DynamoDbContainer.startContainer();
        client = DynamoDbClient.builder()
//                .endpointOverride(URI.create("http://localhost:8000"))
                .endpointOverride(URI.create(DynamoDbContainer.getLocalhostPath()))
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .region(Region.EU_CENTRAL_1).build();
        repository = new DynamoDbRepository<>(client, "test", GameRecord.class);
        putGamesLogHandler = new PutGamesLogHandler(repository);
        DbTableCreator.createTableWithCompositePrimaryKey(client, "gameName", "timestamp", "games_log");
    }


    @Test
    @Disabled("DynamoDbException: One of the required keys was not given a value")
    public void shouldPutAndGetOneGame() {
        //given
        var gamesLog = aGamesLog()
                .withGameRecord(aGameRecord()
                        .withGameName(GAME_NAME)
                        .withTimestamp(TIMESTAMP)
                        .withPlayerResult(aPlayerResult()
                                .withPlayerName(PLAYER_1_NAME)
                                .withPlayerScore(PLAYER_1_SCORE)
                                .withIsWinner(true).build())
                        .withPlayerResult(aPlayerResult()
                                .withPlayerName(PLAYER_2_NAME)
                                .withPlayerScore(PLAYER_2_SCORE).build())
                        .build()).build();


        var getRequest = new GetGameRecordRequest(GAME_NAME, TIMESTAMP);

        //when
        putGamesLogHandler.handleRequest(gamesLog, new FakeContext());

        //then
        var result = repository.getItem(getRequest.toItemQuery());

        assertThat(result)
                .hasGameName(GAME_NAME)
                .hasTimestamp(TIMESTAMP)
                .hasGameDate(GAME_DATE)
                .hasGameDescription(GAME_DESCRIPTION)
                .isSolo(false)
                .withPlayerResultOf(PLAYER_1_NAME)
                .hasScore(PLAYER_1_SCORE)
                .isWinner(true)
                .and()
                .withPlayerResultOf(PLAYER_2_NAME)
                .hasScore(PLAYER_2_SCORE)
                .isWinner(false);

    }
}
