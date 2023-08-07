package pl.zajacp;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.zajacp.model.GameRecord;
import pl.zajacp.model.GamesLog;
import pl.zajacp.repository.DynamoDbRepository;
import pl.zajacp.repository.GamesLogRepository;

@NoArgsConstructor
@AllArgsConstructor
public class PutGamesLogHandler implements RequestHandler<GamesLog, String> {

    private DynamoDbRepository<GameRecord> gameItemRepository = GamesLogRepository.INSTANCE.get();

    @Override
    public String handleRequest(GamesLog gamesLog, Context context) {
        context.getLogger().log("Received message " + gamesLog.toString());
        validateGameRecordsNonEmpty(gamesLog);
        return gamesLog.getGamesLog().size() == 1
                ? gameItemRepository.putItem(gamesLog.getGamesLog().get(0))
                : gameItemRepository.batchPutItems(gamesLog.getGamesLog());
    }

    private void validateGameRecordsNonEmpty(GamesLog gamesLog) {
        if (gamesLog.getGamesLog() == null || gamesLog.getGamesLog().isEmpty()) {
            throw new IllegalArgumentException("Game record list should not be empty");
        }
    }
}
