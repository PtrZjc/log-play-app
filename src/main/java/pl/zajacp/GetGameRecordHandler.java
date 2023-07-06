package pl.zajacp;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import lombok.AllArgsConstructor;
import pl.zajacp.model.GameRecord;
import pl.zajacp.repository.DynamoDbRepository;
import pl.zajacp.repository.ItemQueryKey;
import pl.zajacp.rest.model.GetGameRecordRequest;

//https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/migration-whats-different.html

@AllArgsConstructor
public class GetGameRecordHandler implements RequestHandler<GetGameRecordRequest, GameRecord> {

    private DynamoDbRepository<GameRecord> gameItemRepository;

    public GetGameRecordHandler() {
        gameItemRepository = new DynamoDbRepository<>("games_log", GameRecord.class);
    }

    @Override
    public GameRecord handleRequest(GetGameRecordRequest getGameRecordRequest, Context context) {
        var key = toItemQueryKey(getGameRecordRequest);
        return gameItemRepository.getItem(key);
    }


    private ItemQueryKey toItemQueryKey(GetGameRecordRequest getGameRecordRequest) {
        return new ItemQueryKey(
                "gameName", getGameRecordRequest.getGameName(),
                "timestamp", getGameRecordRequest.getTimestamp()
        );
    }
}

