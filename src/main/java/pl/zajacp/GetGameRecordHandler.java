package pl.zajacp;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import pl.zajacp.model.GameRecord;
import pl.zajacp.repository.DynamoDbRepository;
import pl.zajacp.repository.GamesLogRepository;
import pl.zajacp.repository.ItemQueryKey;
import pl.zajacp.shared.ObjMapper;

import java.util.Map;

import static pl.zajacp.rest.RequestValidator.*;
import static pl.zajacp.rest.RequestValidator.DataType.*;
import static pl.zajacp.rest.RequestValidator.ParamType.*;

//https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/migration-whats-different.html

@AllArgsConstructor
public class GetGameRecordHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final DynamoDbRepository<GameRecord> gameItemRepository;

    public GetGameRecordHandler() {
        this.gameItemRepository = GamesLogRepository.INSTANCE.get();
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

        var item = gameItemRepository.getItem(itemQueryKey);
        if(item.isEmpty()) {
            return new APIGatewayProxyResponseEvent().withStatusCode(404);
        }

        String gameJson = ObjMapper.INSTANCE.get().writeValueAsString(item.get());

        var response = new APIGatewayProxyResponseEvent();
        response.setBody(gameJson);
        response.setStatusCode(200);
        response.setHeaders(Map.of("Content-Type", "application/json"));
        return response;
    }
}
