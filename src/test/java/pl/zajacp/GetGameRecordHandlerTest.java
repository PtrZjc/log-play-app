package pl.zajacp;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import pl.zajacp.model.GameRecord;
import pl.zajacp.repository.DynamoDbRepository;
import pl.zajacp.shared.ObjMapper;
import pl.zajacp.test.FakeContext;
import pl.zajacp.test.domain.ValidationResultAssertion;
import pl.zajacp.test.database.DynamoDbTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static pl.zajacp.repository.GameLogRepositoryCommons.TIMESTAMP_RANGE_KEY;
import static pl.zajacp.repository.GameLogRepositoryCommons.USER_HASH_KEY;
import static pl.zajacp.test.domain.GameRecordAssertion.assertThat;
import static pl.zajacp.test.domain.GameRecordBuilder.aGameRecord;
import static pl.zajacp.test.TestData.GAME_DATE;
import static pl.zajacp.test.TestData.GAME_DESCRIPTION;
import static pl.zajacp.test.TestData.GAME_NAME;
import static pl.zajacp.test.TestData.TIMESTAMP;

@DynamoDbTest(entityClass = GameRecord.class, hashKey = USER_HASH_KEY, rangeKey = TIMESTAMP_RANGE_KEY)
public class GetGameRecordHandlerTest {

    private final GetGameRecordHandler getGameRecordHandler;
    private final DynamoDbRepository<GameRecord> repository;

    private static final ObjectMapper MAPPER = ObjMapper.INSTANCE.get();

    public GetGameRecordHandlerTest(DynamoDbRepository<GameRecord> repository) {
        this.repository = repository;
        getGameRecordHandler = new GetGameRecordHandler(repository);
    }

    @Test
    public void shouldGetOneGame() throws JsonProcessingException {
        //given
        repository.putItem(aGameRecord()
                .withStandard5PlayersResult()
                .build());

        var requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.withQueryStringParameters(Map.of(TIMESTAMP_RANGE_KEY, TIMESTAMP.toString()));

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

    @Test
    public void shouldGet404WhenGameRecordDoesNotExist() {
        //given
        var requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.withQueryStringParameters(Map.of(TIMESTAMP_RANGE_KEY, TIMESTAMP.toString()));

        //when
        var responseEvent = getGameRecordHandler.handleRequest(requestEvent, new FakeContext());

        //then
        assertEquals(404, responseEvent.getStatusCode());
    }

    @Test
    public void shouldGet400WhenValidationFindsError() {
        //given
        var requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.withQueryStringParameters(Map.of());

        //when
        var responseEvent = getGameRecordHandler.handleRequest(requestEvent, new FakeContext());

        //then
        assertEquals(400, responseEvent.getStatusCode());

        ValidationResultAssertion.assertThat(responseEvent.getBody())
                .hasErrorOnProperty(TIMESTAMP_RANGE_KEY)
                .withDetails("Parameter is missing");
    }
}
