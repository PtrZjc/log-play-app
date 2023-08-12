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
import pl.zajacp.repository.ItemQueryKey;
import pl.zajacp.shared.ObjMapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static pl.zajacp.repository.GameLogRepositoryCommons.GAME_NAME_HASH_KEY;
import static pl.zajacp.repository.GameLogRepositoryCommons.TIMESTAMP_RANGE_KEY;
import static pl.zajacp.repository.GameLogRepositoryCommons.getGameRecordKey;
import static pl.zajacp.rest.GetRequestValidator.DataType.INTEGER;
import static pl.zajacp.rest.GetRequestValidator.DataType.STRING;
import static pl.zajacp.rest.GetRequestValidator.ParamType.QUERY;
import static pl.zajacp.rest.GetRequestValidator.RequiredParam;
import static pl.zajacp.rest.GetRequestValidator.validateParameters;

@AllArgsConstructor
public class GetGameRecordHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final List<RequiredParam> REQUIRED_PARAMS = List.of(
            new RequiredParam(GAME_NAME_HASH_KEY, QUERY, STRING),
            new RequiredParam(TIMESTAMP_RANGE_KEY, QUERY, INTEGER)
    );

    private final DynamoDbRepository<GameRecord> gameItemRepository;

    public GetGameRecordHandler() {
        this.gameItemRepository = GamesLogRepository.INSTANCE.get();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        context.getLogger().log("Received request: " + requestEvent);

        var responseEvent = new APIGatewayProxyResponseEvent();
        try {
            Map<String, String> validationErrors = validateParameters(requestEvent, REQUIRED_PARAMS);

            if (!validationErrors.isEmpty()) {
                return getValidationFailedResponseEvent(validationErrors);
            }

            Map<String, String> queryParams = Optional.ofNullable(requestEvent.getQueryStringParameters())
                    .orElse(Collections.emptyMap());

            ItemQueryKey itemQueryKey = getGameRecordKey(
                    queryParams.get(GAME_NAME_HASH_KEY),
                    Long.valueOf(queryParams.get(TIMESTAMP_RANGE_KEY)));

            Optional<GameRecord> item = gameItemRepository.getItem(itemQueryKey);

            if (item.isEmpty()) {
                return new APIGatewayProxyResponseEvent()
                        .withBody(asErrorJson("Game record not found"))
                        .withStatusCode(404);
            }

            String gameJson = ObjMapper.INSTANCE.get().writeValueAsString(item.get());

            responseEvent
                    .withBody(gameJson)
                    .withStatusCode(200);

        } catch (Exception e) {
            context.getLogger().log("Error processing request: " + e.getMessage());
            responseEvent
                    .withBody(asErrorJson("Internal server error"))
                    .withStatusCode(500);
        }
        responseEvent.withHeaders(Map.of("Content-Type", "application/json"));
        return responseEvent;
    }

    private APIGatewayProxyResponseEvent getValidationFailedResponseEvent(Map<String, String> validationErrors) throws JsonProcessingException {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(400);
        var jsonErrors = ObjMapper.INSTANCE.get().writeValueAsString(Map.of("errors", validationErrors));
        response.setBody(jsonErrors);
        return response;
    }

    private static String asErrorJson(String reason) {
        return "{\"error\": \"" + reason + "\"}";
    }
}

