package pl.zajacp;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import pl.zajacp.model.GameRecord;
import pl.zajacp.model.GamesLog;
import pl.zajacp.repository.DynamoDbRepository;
import pl.zajacp.repository.GamesLogRepository;
import pl.zajacp.rest.GameValidator;
import pl.zajacp.shared.ObjMapper;

import static pl.zajacp.repository.GameLogRepositoryCommons.GLOBAL_USER;
import static pl.zajacp.rest.RestCommons.USER_HEADER;
import static pl.zajacp.rest.RestCommons.getHeaderValue;
import static pl.zajacp.rest.RestCommons.getResponseEvent;
import static pl.zajacp.rest.RestCommons.getValidationFailedResponseEvent;

public class PutGamesLogHandler extends BaseGameRecordHandler {

    public PutGamesLogHandler() {
        super(GamesLogRepository.INSTANCE.get());
    }

    public PutGamesLogHandler(DynamoDbRepository<GameRecord> gameItemRepository) {
        super(gameItemRepository);
    }


    @Override
    protected APIGatewayProxyResponseEvent handleValidRequestEvent(APIGatewayProxyRequestEvent requestEvent) throws JsonProcessingException {
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

        return getResponseEvent().withStatusCode(204);
    }

    private static void enrichWithUser(APIGatewayProxyRequestEvent requestEvent, GamesLog gamesLog) {
        String user = getHeaderValue(requestEvent, USER_HEADER).orElse(GLOBAL_USER);
        gamesLog.games().forEach(g -> g.setUserName(user));
    }
}
