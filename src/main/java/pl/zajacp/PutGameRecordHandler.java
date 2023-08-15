package pl.zajacp;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import pl.zajacp.model.GameRecord;
import pl.zajacp.repository.DynamoDbRepository;
import pl.zajacp.repository.GamesLogRepository;
import pl.zajacp.rest.GameValidator;
import pl.zajacp.shared.ObjMapper;

import static pl.zajacp.repository.GameLogRepositoryCommons.GLOBAL_USER;
import static pl.zajacp.rest.RestCommons.SERVER_ERROR_MESSAGE;
import static pl.zajacp.rest.RestCommons.UNSUPPORTED_JSON_ERROR_MESSAGE;
import static pl.zajacp.rest.RestCommons.asErrorJson;
import static pl.zajacp.rest.RestCommons.getResponseEvent;
import static pl.zajacp.rest.RestCommons.getUserFromHeader;
import static pl.zajacp.rest.RestCommons.getValidationFailedResponseEvent;

@AllArgsConstructor
public class PutGameRecordHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final DynamoDbRepository<GameRecord> gameItemRepository;

    public PutGameRecordHandler() {
        this.gameItemRepository = GamesLogRepository.INSTANCE.get();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        context.getLogger().log("Received request: " + requestEvent);

        var responseEvent = getResponseEvent();
        try {
            GameRecord gameRecord = ObjMapper.INSTANCE.get().readValue(requestEvent.getBody(), GameRecord.class);

            var validationErrors = GameValidator.validateGameRecord(gameRecord);

            if (!validationErrors.isEmpty()) {
                return getValidationFailedResponseEvent(validationErrors);
            }

            gameRecord.setUserName(getUserFromHeader(requestEvent, GLOBAL_USER));
            gameItemRepository.putItem(gameRecord);

            responseEvent.withStatusCode(204);
        } catch (JsonProcessingException e) {
            responseEvent
                    .withStatusCode(400)
                    .withBody(asErrorJson(UNSUPPORTED_JSON_ERROR_MESSAGE, e));
        } catch (Exception e) {
            responseEvent
                    .withStatusCode(500)
                    .withBody(asErrorJson(SERVER_ERROR_MESSAGE, e));
        }
        return responseEvent;
    }
}
