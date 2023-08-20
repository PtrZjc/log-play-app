package pl.zajacp;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import pl.zajacp.model.GameRecord;
import pl.zajacp.repository.DynamoDbRepository;
import pl.zajacp.repository.GamesLogRepository;
import pl.zajacp.rest.GameValidator;
import pl.zajacp.shared.ObjMapper;

import static pl.zajacp.repository.GameLogRepositoryCommons.GLOBAL_USER;
import static pl.zajacp.rest.RestCommons.USER_HEADER;
import static pl.zajacp.rest.RestCommons.getHeaderValue;
import static pl.zajacp.rest.RestCommons.getResponseEvent;
import static pl.zajacp.rest.RestCommons.getValidationFailedResponseEvent;

public class PutGameRecordHandler extends BaseGameRecordHandler {

    public PutGameRecordHandler() {
        super(GamesLogRepository.INSTANCE.get());
    }

    public PutGameRecordHandler(DynamoDbRepository<GameRecord> gameItemRepository) {
        super(gameItemRepository);
    }

    @Override
    protected APIGatewayProxyResponseEvent handleValidRequestEvent(APIGatewayProxyRequestEvent requestEvent) throws JsonProcessingException {
        GameRecord gameRecord = ObjMapper.INSTANCE.get().readValue(requestEvent.getBody(), GameRecord.class);

        var validationErrors = GameValidator.validateGameRecord(gameRecord);
        if (!validationErrors.isEmpty()) {
            return getValidationFailedResponseEvent(validationErrors);
        }

        String user = getHeaderValue(requestEvent, USER_HEADER).orElse(GLOBAL_USER);
        gameRecord.setUserName(user);

        gameItemRepository.putItem(gameRecord);

        return getResponseEvent().withStatusCode(204);
    }
}
