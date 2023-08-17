package pl.zajacp;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import pl.zajacp.model.GameRecord;
import pl.zajacp.repository.DynamoDbRepository;
import pl.zajacp.shared.ObjMapper;
import pl.zajacp.test.FakeContext;
import pl.zajacp.test.database.DynamoDbTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static pl.zajacp.repository.GameLogRepositoryCommons.GLOBAL_USER;
import static pl.zajacp.repository.GameLogRepositoryCommons.TIMESTAMP_RANGE_KEY;
import static pl.zajacp.repository.GameLogRepositoryCommons.USER_HASH_KEY;
import static pl.zajacp.repository.GameLogRepositoryCommons.getGameRecordKey;
import static pl.zajacp.rest.RestCommons.UNSUPPORTED_JSON_ERROR_MESSAGE;
import static pl.zajacp.test.domain.GameRecordAssertion.assertThat;
import static pl.zajacp.test.domain.GameRecordBuilder.aGameRecord;
import static pl.zajacp.test.TestData.DIFFERENT_DESCRIPTION;
import static pl.zajacp.test.TestData.GAME_DATE;
import static pl.zajacp.test.TestData.GAME_DESCRIPTION;
import static pl.zajacp.test.TestData.GAME_NAME;
import static pl.zajacp.test.TestData.TIMESTAMP;

@DynamoDbTest(entityClass = GameRecord.class, hashKey = USER_HASH_KEY, rangeKey = TIMESTAMP_RANGE_KEY)
public class PutGameRecordTest {

    private final PutGameRecordHandler putGameRecordHandler;
    private final DynamoDbRepository<GameRecord> repository;

    private static final ObjectMapper MAPPER = ObjMapper.INSTANCE.get();

    public PutGameRecordTest(DynamoDbRepository<GameRecord> repository) {
        this.repository = repository;
        putGameRecordHandler = new PutGameRecordHandler(repository);
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