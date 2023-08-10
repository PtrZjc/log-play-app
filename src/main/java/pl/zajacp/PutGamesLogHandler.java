package pl.zajacp;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.zajacp.model.GameRecord;
import pl.zajacp.model.GamesLog;
import pl.zajacp.repository.DynamoDbRepository;
import pl.zajacp.repository.GamesLogRepository;
import pl.zajacp.rest.PutRequestValidator;
import pl.zajacp.shared.ObjMapper;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
public class PutGamesLogHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private DynamoDbRepository<GameRecord> gameItemRepository = GamesLogRepository.INSTANCE.get();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        context.getLogger().log("Received request: " + requestEvent);

        var responseEvent = new APIGatewayProxyResponseEvent();

        try {
            GamesLog gamesLog = ObjMapper.INSTANCE.get().readValue(requestEvent.getBody(), GamesLog.class);

            PutRequestValidator.validateGamesLog(gamesLog);

            String resultMessage = gamesLog.games().size() == 1
                    ? gameItemRepository.putItem(gamesLog.games().get(0))
                    : gameItemRepository.batchPutItems(gamesLog.games());

            responseEvent
                    .withStatusCode(200)
                    .withBody(resultMessage);

        } catch (JsonProcessingException e) {
            responseEvent
                    .withStatusCode(400)
                    .withBody(asErrorJson("Invalid JSON input"));
        } catch (Exception e) {
            responseEvent
                    .withStatusCode(500)
                    .withBody(asErrorJson("Internal Server Error"));
        }
        responseEvent.withHeaders(Map.of("Content-Type", "application/json"));
        return responseEvent;
    }

    private static String asErrorJson(String reason) {
        return "{\"error\": \"" + reason + "\"}";
    }
}
