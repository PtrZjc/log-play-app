//package pl.zajacp;
//
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import pl.zajacp.model.GameRecord;
//import pl.zajacp.repository.DynamoDbRepository;
//import pl.zajacp.rest.model.GetGameRecordRequest;
//import pl.zajacp.test.FakeContext;
//import pl.zajacp.test.db.DbTableCreator;
//import pl.zajacp.test.db.DynamoDbContainer;
//import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
//import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
//import software.amazon.awssdk.regions.Region;
//import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
//
//import java.net.URI;
//
//import static pl.zajacp.test.TestData.*;
//import static pl.zajacp.test.assertion.GameRecordAssertion.*;
//import static pl.zajacp.test.builder.GameRecordBuilder.*;
//import static pl.zajacp.test.builder.GamesLogBuilder.*;
//
//public class IntegrationTest {
//
//    private static DynamoDbClient client;
//    private static PutGamesLogHandler putGamesLogHandler;
//    private static DynamoDbRepository<GameRecord> repository;
//
//    @BeforeAll
//    public static void beforeAll() {
//        DynamoDbContainer.startContainer();
//        client = DynamoDbClient.builder()
////                .endpointOverride(URI.create("http://localhost:8000"))
//                .endpointOverride(URI.create(DynamoDbContainer.getLocalhostPath()))
//                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("local", "local")))
//                .region(Region.EU_CENTRAL_1).build();
//        repository = new DynamoDbRepository<>(client, "games_log", GameRecord.class);
//        putGamesLogHandler = new PutGamesLogHandler(repository);
//        DbTableCreator.createTableWithCompositePrimaryKey(client, "gameName", "timestamp", "games_log");
//    }
//
//
//    @Test
//    public void shouldPutAndGetOneGame() {
//        //given
//        var gamesLog = aGamesLog()
//                .withGameRecord(aGameRecord()
//                        .withGameName(GAME_NAME)
//                        .withTimestamp(TIMESTAMP_2)
//                        .withStandard5PlayersResult()
//                        .build()).build();
//
//        var getRequest = new GetGameRecordRequest(GAME_NAME, TIMESTAMP_2);
//
//        //when
//        putGamesLogHandler.handleRequest(gamesLog, new FakeContext());
//
//        //then
//        var result = repository.getItem(getRequest.toItemQuery()).get();
//
//        assertThat(result)
//                .hasGameName(GAME_NAME)
//                .hasTimestamp(TIMESTAMP_2)
//                .hasGameDate(GAME_DATE)
//                .hasGameDescription(GAME_DESCRIPTION)
//                .isSolo(false)
//                .hasExpected5StandardPlayersResult();
//    }
//
//    @Test
//    public void shouldPutBatchAndGetMoreGames() {
//        //given
//        var gamesLog = aGamesLog()
//                .withGameRecord(aGameRecord()
//                        .withGameName(GAME_NAME)
//                        .withTimestamp(TIMESTAMP)
//                        .withStandard5PlayersResult()
//                        .build())
//                .withGameRecord(aGameRecord()
//                        .withGameName(GAME_NAME_2)
//                        .withTimestamp(TIMESTAMP_2)
//                        .withStandard5PlayersResult()
//                        .build()
//                ).build();
//
//        var getGame1Request = new GetGameRecordRequest(GAME_NAME, TIMESTAMP);
//        var getGame2Request = new GetGameRecordRequest(GAME_NAME_2, TIMESTAMP_2);
//
//        //when
//        putGamesLogHandler.handleRequest(gamesLog, new FakeContext());
//
//        //then
//        var game1 = repository.getItem(getGame1Request.toItemQuery());
//        var game2 = repository.getItem(getGame2Request.toItemQuery());
//
//        assertThat(game1)
//                .hasGameName(GAME_NAME)
//                .hasTimestamp(TIMESTAMP)
//                .hasGameDate(GAME_DATE)
//                .hasGameDescription(GAME_DESCRIPTION)
//                .isSolo(false)
//                .hasExpected5StandardPlayersResult();
//        assertThat(game2)
//                .hasGameName(GAME_NAME_2)
//                .hasTimestamp(TIMESTAMP_2)
//                .hasGameDate(GAME_DATE)
//                .hasGameDescription(GAME_DESCRIPTION)
//                .isSolo(false)
//                .hasExpected5StandardPlayersResult();
//    }
//}
