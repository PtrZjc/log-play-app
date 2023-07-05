package pl.zajacp.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
@DynamoDbBean
public class GameRecord {
    private Long timestamp;
    private String gameName;
    private String gameDate;
    private String gameDescription;
    private boolean solo;
    private String duration;
    private List<PlayerResult> playerResults;

    @DynamoDbPartitionKey
    public String getGameName() {
        return gameName;
    }

    @DynamoDbSortKey
    public Long getTimestamp() {
        return timestamp;
    }
}

