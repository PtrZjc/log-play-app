package pl.zajacp;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import pl.zajacp.model.GameRecord;
import pl.zajacp.repository.DynamoDbRepository;
import pl.zajacp.repository.GamesLogRepository;
import pl.zajacp.repository.ItemQueryKey;
import pl.zajacp.rest.RequestValidator;
import pl.zajacp.rest.model.GetGameRecordRequest;
import pl.zajacp.shared.ObjectMapperSingleton;

import java.util.HashMap;
import java.util.Map;

import static pl.zajacp.rest.RequestValidator.*;
import static pl.zajacp.rest.RequestValidator.DataType.*;
import static pl.zajacp.rest.RequestValidator.ParamType.*;

//https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/migration-whats-different.html

@AllArgsConstructor
public class GetGameRecordHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final DynamoDbRepository<GameRecord> gameItemRepository;

    public GetGameRecordHandler() {
        this.gameItemRepository = GamesLogRepository.getInstance();
    }

    @Override
    @SneakyThrows
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
        var requiredParams = Map.of(
                "gameName", new ParameterInfo(QUERY, STRING),
                "timestamp", new ParameterInfo(QUERY, NUMBER)
        );
        var validationErrorResponse = validateParameters(apiGatewayProxyRequestEvent, requiredParams);

        if (validationErrorResponse.isPresent()) {
            return validationErrorResponse.get();
        }

        ItemQueryKey itemQueryKey = ItemQueryKey.of(
                "gameName", apiGatewayProxyRequestEvent.getQueryStringParameters().get("gameName"),
                "timestamp", Long.valueOf(apiGatewayProxyRequestEvent.getQueryStringParameters().get("timestamp"))
        );

        String gameJson = ObjectMapperSingleton.getInstance()
                .writeValueAsString(gameItemRepository.getItem(itemQueryKey));

        var response = new APIGatewayProxyResponseEvent();
        response.setBody(gameJson);
        response.setHeaders(Map.of("Content-Type", "application/json"));
        return response;
    }
}
