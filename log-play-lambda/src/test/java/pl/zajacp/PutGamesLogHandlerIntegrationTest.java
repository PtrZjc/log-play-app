package pl.zajacp;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.zajacp.model.GameRecord;
import pl.zajacp.model.GamesLog;
import pl.zajacp.repository.DynamoDbRepository;
import pl.zajacp.repository.ItemQueryKey;
import pl.zajacp.shared.ObjMapper;
import pl.zajacp.test.BaseIntegrationHandlerTest;
import pl.zajacp.test.FakeContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static pl.zajacp.repository.DynamoDbRepository.QueryOrder.ASC;
import static pl.zajacp.repository.GameLogRepositoryCommons.GLOBAL_USER;
import static pl.zajacp.repository.GameLogRepositoryCommons.USER_HASH_KEY;
import static pl.zajacp.repository.GameLogRepositoryCommons.getGameRecordKey;
import static pl.zajacp.rest.RestCommons.UNSUPPORTED_JSON_ERROR_MESSAGE;
import static pl.zajacp.test.TestData.DIFFERENT_DESCRIPTION;
import static pl.zajacp.test.TestData.GAME_DATE;
import static pl.zajacp.test.TestData.GAME_DESCRIPTION;
import static pl.zajacp.test.TestData.GAME_NAME;
import static pl.zajacp.test.TestData.TIMESTAMP;
import static pl.zajacp.test.TestData.getExemplaryGamesLogRequest;
import static pl.zajacp.test.assertion.GameRecordAssertion.assertThat;
import static pl.zajacp.test.builder.GameRecordBuilder.aGameRecord;
import static pl.zajacp.test.builder.GamesLogBuilder.aGamesLog;

public class PutGamesLogHandlerIntegrationTest extends BaseIntegrationHandlerTest {

    private final PutGamesLogHandler putGamesLogHandler;
    private final DynamoDbRepository<GameRecord> repository;

    private static final ObjectMapper MAPPER = ObjMapper.INSTANCE.get();

    public PutGamesLogHandlerIntegrationTest(DynamoDbRepository<GameRecord> repository) {
        this.repository = repository;
        this.putGamesLogHandler = new PutGamesLogHandler(repository);
    }

    @Test
    public void shouldPutOneGameInEmptyDatabase() throws JsonProcessingException {
        //given
        GamesLog gamesLog = aGamesLog().withGameRecord(
                aGameRecord().withStandard5PlayersResult().build()).build();

        var requestEvent = getRequestEventWithValidApiKey()
                .withBody(MAPPER.writeValueAsString(gamesLog));

        //when
        var responseEvent = putGamesLogHandler.handleRequest(requestEvent, new FakeContext());

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

        GamesLog gamesLog = aGamesLog().withGameRecord(
                aGameRecord().withStandard5PlayersResult()
                        .withGameDescription(DIFFERENT_DESCRIPTION)
                        .build()).build();

        var requestEvent = getRequestEventWithValidApiKey()
                .withBody(MAPPER.writeValueAsString(gamesLog));

        //when
        var responseEvent = putGamesLogHandler.handleRequest(requestEvent, new FakeContext());

        //then
        assertEquals(204, responseEvent.getStatusCode());

        var savedGameRecord = repository.getItem(getGameRecordKey(TIMESTAMP, GLOBAL_USER));

        assertThat(savedGameRecord)
                .hasGameDescription(DIFFERENT_DESCRIPTION);
    }

    @Test
    public void shouldPutActualGamesLog() throws JsonProcessingException {
        //given
        var requestEvent = getRequestEventWithValidApiKey()
                .withBody(getExemplaryGamesLogRequest());

        //when
        var responseEvent = putGamesLogHandler.handleRequest(requestEvent, new FakeContext());

        //then
        assertEquals(204, responseEvent.getStatusCode());
    }

    @Test
    public void shouldPutMultipleGames() throws JsonProcessingException {
        //given
        repository.putItem(aGameRecord().withStandard5PlayersResult().build());

        GamesLog gamesLog = aGamesLog().withMultipleDefaultGameRecordsStartingWithTimestamp(TIMESTAMP, 30).build();

        var requestEvent = getRequestEventWithValidApiKey()
                .withBody(MAPPER.writeValueAsString(gamesLog));

        //when
        var responseEvent = putGamesLogHandler.handleRequest(requestEvent, new FakeContext());

        //then
        assertEquals(204, responseEvent.getStatusCode());

        var savedGameRecords = repository.getItems(ItemQueryKey.of(USER_HASH_KEY, GLOBAL_USER), ASC);

        Assertions.assertEquals(30, savedGameRecords.size());
    }

    @Test
    public void shouldReturn400ForDuplicatedKeys() throws JsonProcessingException {
        //given
        GamesLog gamesLog = aGamesLog()
                .withGameRecord(aGameRecord().withStandard5PlayersResult().build())
                .withGameRecord(aGameRecord().withStandard5PlayersResult().build()).build();

        var requestEvent = getRequestEventWithValidApiKey()
                .withBody(MAPPER.writeValueAsString(gamesLog));

        //when
        var responseEvent = putGamesLogHandler.handleRequest(requestEvent, new FakeContext());

        //then
        assertEquals(400, responseEvent.getStatusCode());
    }


    @Test
    public void shouldGet400ForInvalidJsonInput() {
        //given
        var requestEvent = getRequestEventWithValidApiKey()
                .withBody("{\"games\": invalid-json-here }");

        //when
        var responseEvent = putGamesLogHandler.handleRequest(requestEvent, new FakeContext());

        //then
        assertEquals(400, responseEvent.getStatusCode());
        assertTrue(responseEvent.getBody().contains(UNSUPPORTED_JSON_ERROR_MESSAGE));
    }

    @Test
    public void shouldGet403WhenAbsentApiKey() {
        //when
        var responseEvent = putGamesLogHandler.handleRequest(
                new APIGatewayProxyRequestEvent(), new FakeContext());

        //then
        assertEquals(403, responseEvent.getStatusCode());
    }
}