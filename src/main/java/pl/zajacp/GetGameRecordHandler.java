package pl.zajacp;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import static pl.zajacp.rest.RestCommons.USER_HEADER;
import static pl.zajacp.rest.RestCommons.asErrorJson;
import static pl.zajacp.rest.RestCommons.getHeaderValue;
import static pl.zajacp.rest.RestCommons.getResponseEvent;
import static pl.zajacp.rest.RestCommons.getValidationFailedResponseEvent;

public class GetGameRecordHandler extends BaseGameRecordHandler {

    private final static String NOT_FOUND_MESSAGE = "Game record not found";

    private static final List<RequiredParam> REQUIRED_PARAMS = List.of(
            new RequiredParam(TIMESTAMP_RANGE_KEY, QUERY, INTEGER)
    );

    public GetGameRecordHandler() {
        super(GamesLogRepository.INSTANCE.get());
    }

    public GetGameRecordHandler(DynamoDbRepository<GameRecord> gameItemRepository) {
        super(gameItemRepository);
    }

    @Override
    protected APIGatewayProxyResponseEvent handleValidRequestEvent(APIGatewayProxyRequestEvent requestEvent) throws JsonProcessingException {
        Map<String, String> validationErrors = validateParameters(requestEvent, REQUIRED_PARAMS);
        if (!validationErrors.isEmpty()) return getValidationFailedResponseEvent(validationErrors);

        Map<String, String> queryParams = Optional.ofNullable(requestEvent.getQueryStringParameters())
                .orElse(Map.of());

        ItemQueryKey itemQueryKey = getGameRecordKey(
                Long.valueOf(queryParams.get(TIMESTAMP_RANGE_KEY)),
                getHeaderValue(requestEvent, USER_HEADER).orElse(GLOBAL_USER)
        );

        Optional<GameRecord> item = gameItemRepository.getItem(itemQueryKey);

        return item.isPresent() ?
                getResponseEvent()
                        .withBody(ObjMapper.INSTANCE.get().writeValueAsString(item.get()))
                        .withStatusCode(200) :
                getResponseEvent()
                        .withBody(asErrorJson(NOT_FOUND_MESSAGE))
                        .withStatusCode(404);
    }
}
