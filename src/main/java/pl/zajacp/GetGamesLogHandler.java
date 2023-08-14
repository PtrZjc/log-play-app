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
import java.util.Map;

import static pl.zajacp.repository.GameLogRepositoryCommons.GLOBAL_USER;
import static pl.zajacp.repository.GameLogRepositoryCommons.getGamesLogKey;
import static pl.zajacp.rest.RestCommons.asErrorJson;
import static pl.zajacp.rest.RestCommons.getUserFromHeader;

@AllArgsConstructor
public class GetGamesLogHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final DynamoDbRepository<GameRecord> gameItemRepository;

    public GetGamesLogHandler() {
        this.gameItemRepository = GamesLogRepository.INSTANCE.get();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        context.getLogger().log("Received request: " + requestEvent);

        var responseEvent = new APIGatewayProxyResponseEvent();
        try {
            ItemQueryKey itemQueryKey = getGamesLogKey(getUserFromHeader(requestEvent, GLOBAL_USER));

            List<GameRecord> games = gameItemRepository.getItems(itemQueryKey, QueryOrder.DESC);

            GamesLog gamesLog = new GamesLog(games);

            String gameJson = ObjMapper.INSTANCE.get().writeValueAsString(gamesLog);

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
}

