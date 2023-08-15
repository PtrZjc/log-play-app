package pl.zajacp;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import lombok.AllArgsConstructor;
import pl.zajacp.model.GameRecord;
import pl.zajacp.repository.DynamoDbRepository;
import pl.zajacp.repository.GamesLogRepository;
import pl.zajacp.repository.ItemQueryKey;
import pl.zajacp.shared.ObjMapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static pl.zajacp.repository.GameLogRepositoryCommons.GLOBAL_USER;
import static pl.zajacp.repository.GameLogRepositoryCommons.TIMESTAMP_RANGE_KEY;
import static pl.zajacp.repository.GameLogRepositoryCommons.getGameRecordKey;
import static pl.zajacp.rest.RequestParamValidator.DataType.INTEGER;
import static pl.zajacp.rest.RequestParamValidator.ParamType.QUERY;
import static pl.zajacp.rest.RequestParamValidator.RequiredParam;
import static pl.zajacp.rest.RequestParamValidator.validateParameters;
import static pl.zajacp.rest.RestCommons.SERVER_ERROR_MESSAGE;
import static pl.zajacp.rest.RestCommons.asErrorJson;
import static pl.zajacp.rest.RestCommons.getResponseEvent;
import static pl.zajacp.rest.RestCommons.getUserFromHeader;
import static pl.zajacp.rest.RestCommons.getValidationFailedResponseEvent;

@AllArgsConstructor
public class GetGameRecordHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final static String NOT_FOUND_MESSAGE = "Game record not found";

    private static final List<RequiredParam> REQUIRED_PARAMS = List.of(
            new RequiredParam(TIMESTAMP_RANGE_KEY, QUERY, INTEGER)
    );

    private final DynamoDbRepository<GameRecord> gameItemRepository;

    public GetGameRecordHandler() {
        this.gameItemRepository = GamesLogRepository.INSTANCE.get();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        context.getLogger().log("Received request: " + requestEvent);

        var responseEvent = getResponseEvent();
        try {
            Map<String, String> validationErrors = validateParameters(requestEvent, REQUIRED_PARAMS);

            if (!validationErrors.isEmpty()) {
                return getValidationFailedResponseEvent(validationErrors);
            }

            Map<String, String> queryParams = Optional.ofNullable(requestEvent.getQueryStringParameters())
                    .orElse(Map.of());

            ItemQueryKey itemQueryKey = getGameRecordKey(
                    Long.valueOf(queryParams.get(TIMESTAMP_RANGE_KEY)),
                    getUserFromHeader(requestEvent, GLOBAL_USER)
            );

            Optional<GameRecord> item = gameItemRepository.getItem(itemQueryKey);

            if (item.isPresent()) {
                String gameJson = ObjMapper.INSTANCE.get().writeValueAsString(item.get());
                responseEvent
                        .withBody(gameJson)
                        .withStatusCode(200);
            } else {
                responseEvent
                        .withBody(asErrorJson(NOT_FOUND_MESSAGE))
                        .withStatusCode(404);
            }
        } catch (Exception e) {
            responseEvent
                    .withStatusCode(500)
                    .withBody(asErrorJson(SERVER_ERROR_MESSAGE, e));
        }
        return responseEvent;
    }
}
