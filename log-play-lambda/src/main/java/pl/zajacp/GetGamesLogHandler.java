package pl.zajacp;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import static pl.zajacp.rest.RestCommons.USER_HEADER;
import static pl.zajacp.rest.RestCommons.getHeaderValue;
import static pl.zajacp.rest.RestCommons.getResponseEvent;

public class GetGamesLogHandler extends BaseGameRecordHandler {

    public GetGamesLogHandler() {
        super(GamesLogRepository.INSTANCE.get());
    }

    public GetGamesLogHandler(DynamoDbRepository<GameRecord> gameItemRepository) {
        super(gameItemRepository);
    }

    @Override
    protected APIGatewayProxyResponseEvent handleValidRequestEvent(APIGatewayProxyRequestEvent requestEvent) throws JsonProcessingException {
        String user = getHeaderValue(requestEvent, USER_HEADER).orElse(GLOBAL_USER);
        ItemQueryKey itemQueryKey = getGamesLogKey(user);

        List<GameRecord> games = gameItemRepository.getItems(itemQueryKey, QueryOrder.DESC);

        String gameJson = ObjMapper.INSTANCE.get().writeValueAsString(new GamesLog(games));

        return getResponseEvent()
                .withBody(gameJson)
                .withStatusCode(200);
    }
}

