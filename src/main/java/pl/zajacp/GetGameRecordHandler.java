package pl.zajacp;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.zajacp.model.GameRecord;
import pl.zajacp.repository.DynamoDbRepository;
import pl.zajacp.repository.ItemQueryKey;

//https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/migration-whats-different.html

public class GetGameRecordHandler implements RequestHandler<GetGameRecordRequest, GameRecord> {

    private final static DynamoDbRepository<GameRecord> gameItemRepository =
            new DynamoDbRepository<>("games_log", GameRecord.class);

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

@Getter
@Setter
@NoArgsConstructor
class GetGameRecordRequest {
    private Long timestamp;
    private String gameName;
}