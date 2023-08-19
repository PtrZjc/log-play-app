package pl.zajacp;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import lombok.AllArgsConstructor;
import pl.zajacp.model.GameRecord;
import pl.zajacp.model.GamesLog;
import pl.zajacp.repository.DynamoDbRepository;
import pl.zajacp.repository.DynamoDbRepository.QueryOrder;
import pl.zajacp.repository.GamesLogRepository;
import pl.zajacp.repository.ItemQueryKey;
import pl.zajacp.shared.ObjMapper;

import java.util.List;

import static pl.zajacp.repository.GameLogRepositoryCommons.GLOBAL_USER;
import static pl.zajacp.repository.GameLogRepositoryCommons.getGamesLogKey;
import static pl.zajacp.rest.RestCommons.SERVER_ERROR_MESSAGE;
import static pl.zajacp.rest.RestCommons.USER_HEADER;
import static pl.zajacp.rest.RestCommons.apiKeysMatch;
import static pl.zajacp.rest.RestCommons.asErrorJson;
import static pl.zajacp.rest.RestCommons.getHeaderValue;
import static pl.zajacp.rest.RestCommons.getResponseEvent;
import static pl.zajacp.rest.RestCommons.getUnauthorizedResponseEvent;

@AllArgsConstructor
public class GetGamesLogHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final DynamoDbRepository<GameRecord> gameItemRepository;

    public GetGamesLogHandler() {
        this.gameItemRepository = GamesLogRepository.INSTANCE.get();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        context.getLogger().log("Received request: " + requestEvent);

        var responseEvent = getResponseEvent();
        try {
            if (!apiKeysMatch(requestEvent)) return getUnauthorizedResponseEvent();

            String user = getHeaderValue(requestEvent, USER_HEADER).orElse(GLOBAL_USER);
            ItemQueryKey itemQueryKey = getGamesLogKey(user);

            List<GameRecord> games = gameItemRepository.getItems(itemQueryKey, QueryOrder.DESC);

            String gameJson = ObjMapper.INSTANCE.get().writeValueAsString(new GamesLog(games));

            responseEvent
                    .withBody(gameJson)
                    .withStatusCode(200);
        } catch (Exception e) {
            responseEvent
                    .withStatusCode(500)
                    .withBody(asErrorJson(SERVER_ERROR_MESSAGE, e));
        }
        return responseEvent;
    }
}

