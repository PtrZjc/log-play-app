package pl.zajacp;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.zajacp.model.GameRecord;
import pl.zajacp.repository.DynamoDbRepository;
import pl.zajacp.repository.GamesLogRepository;
import pl.zajacp.repository.ItemQueryKey;
import pl.zajacp.rest.model.GetGameRecordRequest;

//https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/migration-whats-different.html

@NoArgsConstructor
@AllArgsConstructor
public class GetGameRecordHandler implements RequestHandler<GetGameRecordRequest, GameRecord> {

    private DynamoDbRepository<GameRecord> gameItemRepository = GamesLogRepository.getInstance();

    @Override
    public GameRecord handleRequest(GetGameRecordRequest getGameRecordRequest, Context context) {
        return gameItemRepository.getItem(getGameRecordRequest.toItemQuery());
    }
}
