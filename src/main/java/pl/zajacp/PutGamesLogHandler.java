package pl.zajacp;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import pl.zajacp.model.GameRecord;
import pl.zajacp.model.GamesLog;
import pl.zajacp.repository.DynamoDbRepository;
import pl.zajacp.repository.GamesLogRepository;
import pl.zajacp.rest.GameValidator;
import pl.zajacp.shared.ObjMapper;

import java.util.Map;

import static pl.zajacp.repository.GameLogRepositoryCommons.GLOBAL_USER;
import static pl.zajacp.rest.RestCommons.getUserFromHeader;
import static pl.zajacp.rest.RestCommons.getValidationFailedResponseEvent;

@AllArgsConstructor
public class PutGamesLogHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final DynamoDbRepository<GameRecord> gameItemRepository;

    public PutGamesLogHandler() {
        this.gameItemRepository = GamesLogRepository.INSTANCE.get();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        context.getLogger().log("Received request: " + requestEvent);

        var responseEvent = new APIGatewayProxyResponseEvent();

        try {
            GamesLog gamesLog = ObjMapper.INSTANCE.get().readValue(requestEvent.getBody(), GamesLog.class);

            var validationErrors = GameValidator.validateGamesLog(gamesLog);

            if (!validationErrors.isEmpty()) {
                return getValidationFailedResponseEvent(validationErrors);
            }

            enrichWithUser(requestEvent, gamesLog);

            if (gamesLog.games().size() == 1) {
                gameItemRepository.putItem(gamesLog.games().get(0));
            } else {
                gameItemRepository.batchPutItems(gamesLog.games());
            }

            responseEvent.withStatusCode(204);
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

    private static void enrichWithUser(APIGatewayProxyRequestEvent requestEvent, GamesLog gamesLog) {
        String user = getUserFromHeader(requestEvent, GLOBAL_USER);
        gamesLog.games().forEach(g -> g.setUserName(user));
    }

    private static String asErrorJson(String reason) {
        return "{\"error\": \"" + reason + "\"}";
    }
}
