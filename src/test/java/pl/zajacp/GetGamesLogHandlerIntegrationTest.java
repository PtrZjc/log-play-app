package pl.zajacp;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import pl.zajacp.model.GameRecord;
import pl.zajacp.model.GamesLog;
import pl.zajacp.repository.DynamoDbRepository;
import pl.zajacp.shared.ObjMapper;
import pl.zajacp.test.BaseIntegrationHandlerTest;
import pl.zajacp.test.FakeContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static pl.zajacp.test.TestData.TIMESTAMP;
import static pl.zajacp.test.builder.GamesLogBuilder.aGamesLog;

public class GetGamesLogHandlerIntegrationTest extends BaseIntegrationHandlerTest {

    private final DynamoDbRepository<GameRecord> repository;
    private final GetGamesLogHandler getGamesLogHandler;

    private static final ObjectMapper MAPPER = ObjMapper.INSTANCE.get();

    public GetGamesLogHandlerIntegrationTest(DynamoDbRepository<GameRecord> repository) {
        this.repository = repository;
        getGamesLogHandler = new GetGamesLogHandler(repository);
    }

    @Test
    public void shouldGetEmptyListWhenNoGamesExist() throws JsonProcessingException {
        //when
        var responseEvent = getGamesLogHandler.handleRequest(getRequestEventWithValidApiKey(), new FakeContext());

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
        var responseEvent = getGamesLogHandler.handleRequest(getRequestEventWithValidApiKey(), new FakeContext());

        //then
        assertEquals(200, responseEvent.getStatusCode());
        assertEquals(30, readGamesLogFromBody(responseEvent).games().size());
    }

    @Test
    public void shouldGet403WhenAbsentApiKey() {
        //when
        var responseEvent = getGamesLogHandler.handleRequest(
                new APIGatewayProxyRequestEvent(), new FakeContext());

        //then
        assertEquals(403, responseEvent.getStatusCode());
    }

    private static GamesLog readGamesLogFromBody(APIGatewayProxyResponseEvent responseEvent) throws JsonProcessingException {
        return MAPPER.readValue(responseEvent.getBody(), GamesLog.class);
    }
}
